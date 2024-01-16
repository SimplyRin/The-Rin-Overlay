package net.simplyrin.kzigloader.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.simplyrin.kzigloader.Main;
import net.simplyrin.kzigloader.utils.ChatColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by SimplyRin on 2019/11/25.
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
public class ClientCommandHandler {

	private Main instance;

	public ClientCommandHandler(Main instance) {
		this.instance = instance;

		this.onCommand();
	}

	public void onCommand() {
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("rin")
					.then(ClientCommandManager.literal("toggle")
							.executes(context -> {
								boolean toggle = this.instance.toggle();
								this.info("&bHUD 表示&7: " + (toggle ? "&a有効" : "&c無効") + "&b.");
								return 1;
							})
					).then(ClientCommandManager.literal("toggle-ib")
							.executes(context -> {
								boolean toggle = this.instance.toggleItemBreak();
								this.info("&bアイテム破壊防止切り替え&7: " + (toggle ? "&a有効" : "&c無効") + "&b");
								return 1;
							})
					).then(ClientCommandManager.literal("toggle-ip")
							.executes(context -> {
								boolean toggle = this.instance.toggleShowIpAddress();
								this.info("&b接続サーバー IP アドレス表示&7: " + (toggle ? "&a有効" : "&c無効") + "&b");
								return 1;
							})
					).then(ClientCommandManager.literal("toggle-pe")
							.executes(context -> {
								boolean toggle = this.instance.togglePlayerEntity();
								this.info("&bプレイヤーエンティティー表示&7: " + (toggle ? "&a有効" : "&c無効") + "&b");
								return 1;
							})
					).then(ClientCommandManager.literal("armor")
							.executes(context -> {
								var mc = MinecraftClient.getInstance();
								Iterable<ItemStack> iterable = mc.player.getArmorItems();
								ArrayList<ItemStack> list = new ArrayList<>();
								for (ItemStack itemStack : iterable) {
									list.add(itemStack);
								}
								Collections.reverse(list);

								for (ItemStack itemStack : list) {
									// String name = I18n.format(itemStack.getTranslationKey());
									// String.format("%.1f", ((used * 1.0) / total) * 100)
									// String percent = String.format("%.1f", ((itemStack.getDamage() * 1.0) / itemStack.getMaxDamage()) * 100) + "%";

									String percent = String.format("%.1f", (((itemStack.getMaxDamage() - itemStack.getDamage()) * 1.0) / itemStack.getMaxDamage()) * 100) + "%";
									this.info("Name: " + itemStack.getTranslationKey() + ", Damage: " + percent);
								}

								return 1;
							})
					).then(ClientCommandManager.literal("debug")
							.executes(context -> {
								boolean debug = this.instance.debug();
								this.info("&bデバッグモード&7: " + (debug ? "&a有効" : "&c無効") + "&b.");
								return 1;
							})
					).then(ClientCommandManager.literal("tps")
							.executes(context -> {
								boolean tps = this.instance.toggleShowTPS();
								this.info("&bサーバー TPS 表示&7: " + (tps ? "&a有効" : "&c無効") + "&b.");
								return 1;
							})
					).then(ClientCommandManager.literal("license")
							.executes(context -> {
								List<OslItem> items = new ArrayList<>();

								items.add(new OslItem("BungeeCord", "BSD 3-Clause \"New\" or \"Revised\" License", "https://github.com/SpigotMC/BungeeCord/blob/master/LICENSE"));
								items.add(new OslItem("snakeyaml", "Apache License 2.0", "https://bitbucket.org/asomov/snakeyaml/src/default/LICENSE.txt"));
								items.add(new OslItem("Config", "Apache License 2.0", "https://github.com/SimplyRin/Config/blob/master/LICENSE.md"));
								items.add(new OslItem("ThreadPool", "Apache License 2.0", "https://github.com/SimplyRin/ThreadPool/blob/master/LICENSE.md"));
								items.add(new OslItem("HttpClient", "Apache License 2.0", "https://github.com/SimplyRin/HttpClient/blob/master/LICENSE.md"));
								items.add(new OslItem("jOOR", "Apache License 2.0", "https://github.com/jOOQ/jOOR/blob/master/README.md"));

								for (OslItem item : items) {
									var textComponent = this.instance.getMutableText(ChatColor.translateAlternateColorCodes("&b" + item.name + " &7(" + item.license + ") " + item.url));
									// Style style = textComponent.getStyle();
									// style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, item.url));
									// textComponent.setStyle(style);
									this.instance.info(textComponent.getString());
								}
								return 1;
							})
					).executes((context -> {
						this.printHelp(dispatcher.getRoot().getName());
						return 1;
					})));
		}));
	}

	public void printHelp(String label) {
		this.info("&m------------------------------------");
		this.info("&6TheRinOverlay Mod v" + Main.getVersion());
		this.info("&e" + label + " toggle &7: &bオーバーレイの表示/非表示切り替え");
		this.info("&e" + label + " toggle-ib &7: &bアイテム破壊防止オン/オフの切り替え");
		this.info("&e" + label + " toggle-ip &7: &bオーバーレイへの IP 表示オン/オフの切り替え");
		this.info("&e" + label + " toggle-pe &7: &bプレイヤーエンティティ 表示オン/オフの切り替え");
		this.info("&e" + label + " server-tps &7: &bSpigotサーバー上での TPS を画面上に表示します(要OP)");
		this.info("&e" + label + " license &7: &bオープンソースライセンスの表示");
		this.info("&m------------------------------------");
	}

	public void info(String message) {
		this.instance.info(message);
	}

	@Data
	@AllArgsConstructor
	public class OslItem {
		private String name;
		private String license;
		private String url;
	}

}