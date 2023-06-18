package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.simplyrin.kzigloader.Main;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class CustomClientPlayerEntiry {

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void sendChatMessagePacket(String message, Text preview, CallbackInfo info) {
        if (message != null) {
            Main.getInstance().getClientCommandHandler().onChat(message, info);
        }
    }

    /*@Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    public void sendCommand(ChatMessageSigner signer, String command, @Nullable Text preview, CallbackInfo info) {
        // Main.getInstance().info("[CMD] " + command);

        Main.getInstance().getClientCommandHandler().onChat("/" + command, info);
    }*/

}
