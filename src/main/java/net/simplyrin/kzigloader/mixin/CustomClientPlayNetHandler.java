package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.simplyrin.kzigloader.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class CustomClientPlayNetHandler {

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo info) {
        String message = packet.content().getString();
        Main.getInstance().getTps().onChat(message, info);
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo info) {
        if (packet instanceof CustomPayloadC2SPacket) {
            var payload = (CustomPayloadC2SPacket) packet;
            var channel = payload.getChannel();

            var message = "Namespace: " + channel.getNamespace() + ", Path: " + channel.getPath();
            Main.getInstance().info(message);
        }
    }

}
