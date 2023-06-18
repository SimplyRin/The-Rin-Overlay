package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class CustomInventoryScreen {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.options.debugEnabled) {
            return;
        }

        try {
            InventoryScreen.drawEntity(200, 100, 50,1, 5, mc.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
