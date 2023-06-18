package net.simplyrin.kzigloader.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.simplyrin.kzigloader.Main;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by SimplyRin on 2019/12/06.
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
public class Tps {

	private Main instance;

	private List<TpsItem> asyncTpsList;
	private MemoryItem memoryItem;

	private String lastUpdated;

	public Tps(Main instance) {
		this.instance = instance;
		this.asyncTpsList = new ArrayList<>();

		this.update();
	}

	public void update() {
		ThreadPool.run(() -> {
			while (true) {
				MinecraftClient mc = MinecraftClient.getInstance();
				if (mc.player != null && this.instance.isServertps()) {
					mc.player.sendCommand("tps");
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		});
	}

	// @SubscribeEvent
	public boolean onChat(String message, CallbackInfo info) {
		if (!this.instance.isServertps()) {
			return true;
		}

		if (message.equals("I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.")) {
			info.cancel();
			this.instance.info("&6TPS Checker disabled due to lack of permissions.");
			this.instance.toggleShowTPS(); // turn off
			return true;
		}

		if (message.startsWith("Current Memory Usage:") && message.endsWith(")")) {
			info.cancel();
			this.memoryHud(message);
			return true;
		}

		if (!message.startsWith("TPS from last 1m, 5m, 15m:")) {
			return true;
		}

		info.cancel();

		int i = 0;
		this.getTpsList().clear();
		for (String tps : message.split(":")[1].trim().split(",")) {
			String t = tps.trim().replace("*", "");
			String format = this.format(Double.valueOf(t));

			String time = "";
			if (i == 0) {
				time = "1m";
			} else if (i == 1) {
				time = "5m";
			} else if (i == 2) {
				time = "15m";
			}

			this.getTpsList().add(new TpsItem(time, format + tps.trim()));
			i++;
		}

		this.lastUpdated = new SimpleDateFormat("HH:mm:ss").format(new Date());
		return true;
	}

	public synchronized List<TpsItem> getTpsList() {
		return this.asyncTpsList;
	}

	public void memoryHud(String message) {
		String used = "Unknown";
		if (message.contains("/")) {
			try {
				used = message.split("mb")[0].split("Usage:")[1].split(Pattern.quote("/"))[0].trim();
				// System.out.println("Used: " + used);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String max = "Unknown";
		if (message.contains("Max:")) {
			max = message.split("Max:")[1].replace(")", "").replace("mb", "").trim();
		}

		String free = "Unknown";
		if (!used.equals("Unknown") && !max.equals("Unknown")) {
			try {
				free = (Integer.valueOf(max) - Integer.valueOf(used)) + "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.memoryItem = new MemoryItem(max + " MB", used + " MB", free + " MB");
	}

	public String format(double tps) {
		if (tps > 20.0) {
			return "&a*";
		}
		if (tps > 18.0) {
			return "&a";
		}
		if (tps > 16.0) {
			return "&e";
		}
		return "&c";
    }

	@Data
	@AllArgsConstructor
	public class TpsItem {
		private String time;
		private String tps;
	}

	@Data
	@AllArgsConstructor
	public class MemoryItem {
		private String max;
		private String used;
		private String free;
	}

}
