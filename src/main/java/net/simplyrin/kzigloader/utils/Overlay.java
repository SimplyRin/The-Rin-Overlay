package net.simplyrin.kzigloader.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.util.math.BlockPos;
import net.simplyrin.kzigloader.Main;

public class Overlay {

	private Main instance;

	public Overlay(Main instance) {
		this.instance = instance;
	}

	private boolean hideIp = false;

	public String autoReplace(String text) {
		for (Type type : Type.values()) {
			try {
				text = text.replace(type.getKey(), this.getItem(type));
			} catch (Exception e) {
			}
		}
		return text;
	}

	public String getItem(Type type) {
		MinecraftClient mc = MinecraftClient.getInstance();

		switch (type) {
			case FPS:
				return String.valueOf(MinecraftClient.getInstance().fpsDebugString.split(" ")[0]);
			case CPS:
				break;
			case PING:
				if (mc.getCurrentServerEntry() != null) {
					int latency = 0;

					ClientPlayNetworkHandler clientPlayNetworkHandler = mc.player.networkHandler;
					Collection<PlayerListEntry> list = clientPlayNetworkHandler.getPlayerList();
					for (PlayerListEntry player : list) {
						if (player.getProfile().getId().toString().equals(mc.player.getGameProfile().getId().toString())) {
							latency = player.getLatency();
						}
					}

					return latency + " ms";
					// return String.valueOf(mc.getCurrentServerEntry().getPlayerInfo(mc.player.getUniqueID()).getResponseTime());
				} else {
					return "0";
				}
			case X:
				return String.valueOf(String.format("%.1f", mc.getCameraEntity().getPos().getX()));
			case Y:
				return String.valueOf(String.format("%.1f", mc.getCameraEntity().getPos().getY()));
			case Z:
				return String.valueOf(String.format("%.1f", mc.getCameraEntity().getPos().getZ()));
			case IP:
				if (mc.getCurrentServerEntry() != null) {
					if (!this.instance.isShowIpAddress()) {
						return "Multiplayer";
					} else {
						return mc.getCurrentServerEntry().address.trim().replaceAll("[.]_v1_([1-9]|[0-9][0-9]|[0-9][0-9]_[0-9])[.]viafabric", "").trim();
					}
				} else {
					return "Singleplayer";
				}
			case FACING:
				String facing = mc.player.getHorizontalFacing().getName();
				facing = facing.substring(0, 1).toUpperCase() + facing.substring(1).toLowerCase();
				facing = facing.trim();
				if (facing.equals("East")) {
					facing += " &6X+";
				} else if (facing.equals("South")) {
					facing += " &6Z+";
				} else if (facing.equals("West")) {
					facing += " &6X-";
				} else if (facing.equals("North")) {
					facing += " &6Z-";
				}
				return facing;
			case PLAYERS:
				if (mc.getCurrentServerEntry() != null) {
					return String.valueOf(mc.player.networkHandler.getPlayerList().size());
				} else {
					return "1";
				}
			case EMPTY:
				return "";
			case DATE:
				SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
				return date.format(new Date());
			case TIME:
				SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
				return time.format(new Date());
			case MEMORY:
				long total = Runtime.getRuntime().totalMemory();
				long current = Runtime.getRuntime().freeMemory();
				long used = total - current;

				String ramT = "(" + this.translateByteMemory(used) + "/" + this.translateByteMemory(total) + ")";

				return String.format("%.1f", ((used * 1.0) / total) * 100) + "% " + ramT;
			case SPEED:
				break;
			case POINT_LOCATION:
				break;
			case BIOME:
				BlockPos blockPos = mc.getCameraEntity().getBlockPos();

				var pos = mc.world.getBiome(blockPos);

				var value = (String) pos.getKeyOrValue().map((biomeKey) -> {
					return biomeKey.getValue().toString();
				}, (biome_) -> {
					return biome_;
				});

				return this.convertMinecraftId(value);
		case WORLD_TIME:
			var world = mc.world;
			return world.getTimeOfDay() + "";
		}

		return "Unknown";
	}

	public List<String> getPotions() {
		MinecraftClient mc = MinecraftClient.getInstance();

		List<String> list = new ArrayList<>();
		for (StatusEffectInstance effect : mc.player.getActiveStatusEffects().values()) {
			list.add(this.convertMinecraftId(effect.getTranslationKey()) + " " + effect.getAmplifier() + " - "
					+ this.convertDuration(effect));
		}

		return list;
	}

	public String convertMinecraftId(String name) {
		name = name.replace("effect.minecraft.", "");
		name = name.replace("item.minecraft.", "");
		name = name.replace("block.minecraft.", "");
		name = name.replace("Block.minecraft.", "");
		name = name.replace("minecraft:", "");
		name = name.replace("_", " ");

		String output = "";

		for (String args : name.split(" ")) {
			output += args.substring(0, 1).toUpperCase() + args.substring(1).toLowerCase() + " ";
		}

		return output.trim();
	}

	public String convertDuration(StatusEffectInstance effect) {
		MinecraftClient mc = MinecraftClient.getInstance();

		return StatusEffectUtil.getDurationText(effect, 1.0F, mc.world.getTickManager().getTickRate()).getString();
	}

	public static String ticksToElapsedTime(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i = i % 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	public String translateByteMemory(long memory) {
		long n = 1024L;
        String s = "";

		double kb = memory / n;
		double mb = kb / n;
		double gb = mb / n;
		double tb = gb / n;

		if (memory < n) {
			s = memory + " Bytes";
		} else if (memory >= n && memory < (n * n)) {
			s =  String.format("%.1f", kb) + " KB";
		} else if (memory >= (n * n) && memory < (n * n * n)) {
			s = String.format("%.1f", mb) + " MB";
		} else if (memory >= (n * n * n) && memory < (n * n * n * n)) {
			s = String.format("%.1f", gb) + " GB";
		} else if (memory >= (n * n * n * n)) {
			s = String.format("%.1f", tb) + " TB";
		}

		return s;
	}

	public enum Type {
		FPS("%FPS"), CPS("%CPS"), PING("%PING"), X("%X"), Y("%Y"), Z("%Z"), IP("%IP"), FACING("%FACING"),
			PLAYERS("%PLAYERS"), EMPTY("%EMPTY"), DATE("%DATE"), TIME("%TIME"), MEMORY("%MEMORY"), SPEED("%SPEED"),
			POINT_LOCATION("%POINT_LOCATION"), BIOME("%BIOME"), WORLD_TIME("%WORLD_TIME");

		private String key;

		Type(String key) {
			this.key = key;
		}

		public String getKey() {
			return this.key;
		}
	}

	/* private int wave = 0;

	@SubscribeEvent
    public void onScreenshot(ScreenshotEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if (this.wave == 1) {
			return;
		}

		this.hideIp = true;

		if (this.wave == 0) {
			event.setResultMessage(new StringTextComponent(""));
			event.setCanceled(true);
			this.wave = 1;

			ThreadPool.run(() -> {
				try {
					Thread.sleep(5);
				} catch (Exception e) {
				}

				mc.execute(() -> {
					ScreenShotHelper.saveScreenshot(mc.gameDir, mc.mainWindow.getFramebufferWidth(), mc.mainWindow.getFramebufferHeight(), mc.getFramebuffer(), (message) -> {
						mc.execute(() -> {
							mc.ingameGUI.getChatGUI().printChatMessage(message);
						});
					});
				});
			});
		}

		ThreadPool.run(() -> {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			this.hideIp = false;
			this.wave = 0;
		});
	} */

}
