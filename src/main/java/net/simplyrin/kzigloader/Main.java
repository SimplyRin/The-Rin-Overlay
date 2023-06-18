package net.simplyrin.kzigloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.md_5.bungee.config.Configuration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.simplyrin.kzigloader.listener.ClientCommandHandler;
import net.simplyrin.kzigloader.utils.*;
import net.simplyrin.kzigloader.utils.Tps;
import net.simplyrin.kzigloader.utils.Tps.MemoryItem;
import net.simplyrin.kzigloader.utils.Tps.TpsItem;

/**
 * Created by SimplyRin on 2019/11/17.
 *
 * Copyright (c) 2019 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@Getter
public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		instance = this;

		System.out.println("Loading kzig...");
		this.startMain();
		System.out.println("Loaded kzig.");
	}

	@Getter
	private static Main instance;

	@Getter
	private static final String version = "0.7.1-SNAPSHOT";

	private String prefix = "&7[&cTheRinOverlay&7] &r";

	private boolean toggle = true;
	private boolean toggleItemBreak = true;
	private boolean showIpAddress = true;

	private boolean debug = false;
	private boolean servertps = true;

	private List<String> list;
	private Overlay overlay;
	private Tps tps;
	private ClientCommandHandler clientCommandHandler;

	private Configuration config;
	private Configuration data;

	public void startMain() {
		File folder = new File("TheRinOverlay");
		folder.mkdirs();

		File file = new File(folder, "main.yml");
		if (!file.exists()) {
			System.out.println("Creating new configuration file...");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Configuration config = Config.getConfig(file);

			List<String> list = new ArrayList<>();
			list.add("FPS&7> &f%FPS");
			list.add("Ping&7> &f%PING");
			list.add("X&7> &f%X");
			list.add("Y&7> &f%Y");
			list.add("Z&7> &f%Z");
			list.add("IP&7> &f%IP");
			list.add("Facing&7> &f%FACING");
			list.add("Players&7> &f%PLAYERS");
			list.add("Biome&7> &f%BIOME");
			list.add("");
			list.add("&nSystem Info");
			list.add("Date&7> &f%DATE");
			list.add("Time&7> &f%TIME");
			list.add("Memory&7> &f%MEMORY");

			config.set("List", list);

			config.set("Mid-Text", "%name: %count");

			Config.saveConfig(config, file);
		}

		File data = new File(folder, "data.yml");
		if (!data.exists()) {
			try {
				data.createNewFile();
			} catch (Exception e) {
			}

			Configuration config = Config.getConfig(data);

			config.set("toggle-ib", true);
			config.set("toggle-ip", true);
			config.set("server-tps", true);

			Config.saveConfig(config, data);
		}

		this.config = Config.getConfig(file);
		this.data = Config.getConfig(data);

		this.toggleItemBreak = this.data.getBoolean("toggle-ib");
		this.showIpAddress = this.data.getBoolean("toggle-ip");
		this.servertps = this.data.getBoolean("server-tps");

		this.overlay = new Overlay(this);
		this.tps = new Tps(this);

		this.clientCommandHandler = new ClientCommandHandler(this);

		this.list = this.config.getStringList("List");
	}

	public void onRenderGameOverlay(MatrixStack matrix) {
		this.matrixStack = matrix;

		if (MinecraftClient.getInstance().player == null) {
			return;
		}

		if (!this.toggle) {
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.options.debugEnabled) {
			return;
		}

		this.drawString("&6The Rin Overlay v" + Main.version, 4, 4);

		int i = 16;
		for (String key : this.list) {
			this.drawString(this.overlay.autoReplace(key), 4, i, 16755200);
			if (key.startsWith("&n")) {
				i += 2;
			}
			i += 10;
		}

		List<String> potions = this.overlay.getPotions();
		if (potions.size() > 0) {
			int baseHeight = mc.getWindow().getScaledHeight() / 2;

			this.drawString("&nActive Potion Effects", 4, baseHeight, 16755200);
			baseHeight += 12;

			for (String potion : potions) {
				// TODO: draw Potion Icon
				// this.potionEffectRenderer.drawActivePotionEffects();

				this.drawString("&f" + potion, 4, baseHeight, 16755200);
				baseHeight += 10;
			}
		}

		int width = mc.getWindow().getScaledWidth();
		int height = mc.getWindow().getScaledHeight();

		boolean isCreative = mc.interactionManager.hasCreativeInventory();

		ItemStack currentItem = mc.player.getInventory().getMainHandStack();

		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(mc.player.getOffHandStack());
		items.addAll(mc.player.getInventory().main);

		if (!mc.player.getOffHandStack().getItem().getTranslationKey().equals("block.minecraft.air")) {
			String lcColor = "&a";
			int lcCount = 0;

			var offhand1 = mc.player.getOffHandStack();
			for (ItemStack item : items) {
				if (offhand1.getItem().getTranslationKey().equals(item.getItem().getTranslationKey())) {
					lcCount += item.getCount();
				}
			}

			String c = String.format("%,d", lcCount);
			this.drawString(lcColor + c, (width / 2) - (mc.textRenderer.getWidth(c) / 2) - 108, height - 35, 16755200);
		}

		if (currentItem != null) {
			String color = "&f";
			int count = 0;

			if (currentItem != null && currentItem.getItem().getTranslationKey().equals("item.minecraft.bow")) {
				for (ItemStack item : items) {
					String translationKey = item.getItem().getTranslationKey();
					if (translationKey.equals("item.minecraft.arrow")
							|| translationKey.equals("item.minecraft.spectral_arrow")
							|| translationKey.equals("item.minecraft.tipped_arrow")) {
						color = "&e";
						count += item.getCount();
					}
				}
			} else {
				for (ItemStack item : items) {
					if (currentItem.getName().getString().equals(item.getName().getString())) {
						count += item.getCount();
					}
				}
			}

			if (count >= 1) {
				String c = String.format("%,d", count);

				this.drawString(color + c, (width / 2) - (mc.textRenderer.getWidth(c) / 2), height - 50 + (isCreative ? 15 : 0), 16755200);
			}
		}

		currentItem = mc.player.getInventory().getMainHandStack();

		// Armor
		i = 4;

		Iterable<ItemStack> iterable = mc.player.getArmorItems();
		ArrayList<ItemStack> list = new ArrayList<>();
		for (ItemStack itemStack : iterable) {
			list.add(itemStack);
		}
		Collections.reverse(list);

		String armorHud = "Armor Hud";

		if (potions.size() > 0) {
			i += 50;
		}

		this.drawString("&n&n" + armorHud, width - (mc.textRenderer.getWidth(armorHud) + 4), i, 16755200);
		i += 12;

		if (currentItem != null) {
			list.add(0, currentItem);

			if (this.toggleItemBreak) {
				double damage = (((currentItem.getMaxDamage() - currentItem.getDamage()) * 1.0) / currentItem.getMaxDamage()) * 100;
				if (damage <= 2.0) {
					if (mc.player.getInventory().selectedSlot == 8) {
						mc.player.getInventory().selectedSlot = 5;
					} else {
						mc.player.getInventory().selectedSlot = 8;
					}
					this.info("&a&lアイテム破壊防止の為、スロットが変更されました。");
				}
			}
		}

		for (ItemStack itemStack : list) {
			String key = itemStack.getTranslationKey();

			if (key.equals("Block.minecraft.air")) {
				continue;
			}

			String name = this.overlay.convertMinecraftId(itemStack.getTranslationKey());

			double damage = (((itemStack.getMaxDamage() - itemStack.getDamage()) * 1.0) / itemStack.getMaxDamage()) * 100;

			String percent = String.format("%.1f", damage) + "%";

			if (percent.equals("NaN%") || percent.equals("Infinity%")) {
				continue;
			}

			String color = "&f";
			if (damage <= 10.0) {
				mc.inGameHud.setOverlayMessage(this.getMutableText(ChatColor.translateAlternateColorCodes("&cBe careful of durability!")), false);

				color = "&4";
			} else if (damage <= 20.0) {
				color = "&c";
			} else if (damage <= 40.0) {
				color = "&e";
			}


			int a = mc.textRenderer.getWidth(name);
			int b = mc.textRenderer.getWidth(percent);

			this.drawString(name + "&7> " + color + percent, width - (a + 14 + b), i, 16755200);

			i += 10;
		}

		i += 10;

		Tps tps = this.tps;
		if (tps.getTpsList().size() > 0 && this.servertps) {
			String tpsHud = "TPS Hud";
			this.drawString("&n&n" + tpsHud, width - (mc.textRenderer.getWidth(tpsHud) + 4), i, 16755200);
			i += 12;

			String lastUpdate = "L U";

			int l = mc.textRenderer.getWidth(lastUpdate);
			int ll = mc.textRenderer.getWidth(tps.getLastUpdated());

			this.drawString(lastUpdate + "&7> &f" + tps.getLastUpdated(), width - (l + 14 + ll), i, 16755200);
			i += 10;

			try {
				for (TpsItem item : tps.getTpsList()) {
					int a = mc.textRenderer.getWidth(item.getTime());
					int b = mc.textRenderer.getWidth(ChatColor.stripColor(ChatColor.translateAlternateColorCodes(item.getTps())));

					this.drawString(item.getTime() + "&7> &f" + item.getTps(), width - (a + 14 + b), i, 16755200);

					i += 10;
				}
			} catch (Exception e) {
			}
		}

		if (tps.getMemoryItem() != null && this.servertps) {
			i += 12;

			String memHud = "Memory Hud";
			this.drawString("&n&n" + memHud, width - (mc.textRenderer.getWidth(memHud) + 4), i, 16755200);
			i += 12;

			MemoryItem item = tps.getMemoryItem();

			int a = mc.textRenderer.getWidth("Max");
			int b = mc.textRenderer.getWidth(item.getMax());
			this.drawString("Max&7> &f" + item.getMax(), width - (a + 14 + b), i, 16755200);
			i += 10;

			a = mc.textRenderer.getWidth("Free");
			b = mc.textRenderer.getWidth(item.getFree());
			this.drawString("Free&7> &f" + item.getFree(), width - (a + 14 + b), i, 16755200);
			i += 10;

			a = mc.textRenderer.getWidth("Used");
			b = mc.textRenderer.getWidth(item.getUsed());
			this.drawString("Used&7> &f" + item.getUsed(), width - (a + 14 + b), i, 16755200);
			i += 10;
		}
	}

	public void drawString(String text, int x, int y) {
		this.drawString(text, x, y, 0);
	}

	private MatrixStack matrixStack;

	public void drawString(String text, int x, int y, int color) {
		MinecraftClient.getInstance().textRenderer.drawWithShadow(this.matrixStack, ChatColor.translateAlternateColorCodes(text), x, y, color);
	}

	public boolean toggle() {
		this.toggle = !this.toggle;

		return this.toggle;
	}

	public boolean toggleItemBreak() {
		this.toggleItemBreak = !this.toggleItemBreak;

		this.data.set("toggle-ib", this.toggleItemBreak);
		this.saveDataConfig();

		return this.toggleItemBreak;
	}

	public boolean toggleShowIpAddress() {
		this.showIpAddress = !this.showIpAddress;

		this.data.set("toggle-ip", this.showIpAddress);
		this.saveDataConfig();

		return this.showIpAddress;
	}

	public boolean debug() {
		this.debug = !this.debug;

		return this.debug;
	}

	public boolean toggleShowTPS() {
		this.servertps = !this.servertps;

		this.data.set("server-tps", this.servertps);
		this.saveDataConfig();

		return this.servertps;
	}

	public void saveDataConfig() {
		File folder = new File("TheRinOverlay");
		File data = new File(folder, "data.yml");

		Config.saveConfig(this.data, data);
	}

	public void info(String message) {
		var mc = MinecraftClient.getInstance();
		if (mc == null) {
			return;
		}
		ClientPlayerEntity player = mc.player;
		if (player == null) {
			return;
		}
		player.sendMessage(this.getMutableText(ChatColor.translateAlternateColorCodes(this.prefix + message)), false);
	}

	public void info(MutableText text) {
		var mc = MinecraftClient.getInstance();
		if (mc == null) {
			return;
		}
		ClientPlayerEntity player = mc.player;
		if (player == null) {
			return;
		}
		player.sendMessage(text, false);
	}

	/* public void info(String message) {
		var textComponent = this.getMutableText(ChatColor.translateAlternateColorCodes(this.prefix));
		textComponent.append(message);
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		player.sendMessage(textComponent, false);
	} */

	public MutableText getMutableText(String message) {
		return MutableText.of(new LiteralTextContent(message));
	}

}
