package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class CustomClientPlayerEntiry {

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void sendChatMessagePacket(String command, CallbackInfo info) {
        if (command != null) {
            // Main.getInstance().getClientCommandHandler().onChat(command, info);
        }
    }

    /*@Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    public void sendCommand(ChatMessageSigner signer, String command, @Nullable Text preview, CallbackInfo info) {
        // Main.getInstance().info("[CMD] " + command);

        Main.getInstance().getClientCommandHandler().onChat("/" + command, info);
    }*/

}
