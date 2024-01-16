package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeInventoryScreen.class)
public class CustomInventoryScreen {

    static {
        MinecraftClient mc = MinecraftClient.getInstance();

        mc.player.getActiveStatusEffects();
    }

}
