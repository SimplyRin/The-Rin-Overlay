package net.simplyrin.kzigloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.simplyrin.kzigloader.utils.ChatColor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class CustomLivingRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(LivingEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!(entity instanceof ClientPlayerEntity)) {
            return;
        }

        ClientPlayerEntity player = (ClientPlayerEntity) entity;

        String uniqueId = player.getGameProfile().getId().toString();
        if (!uniqueId.equals(mc.player.getGameProfile().getId().toString())) {
            return;
        }

        this.renderNametag(entity, matrixStack, vertexConsumerProvider, i);
        // this.renderPlayerHud(entity, matrixStack);
    }

    protected void renderNametag(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.getActiveStatusEffects() != null) {
            boolean skip = false;

            for (var entry : entity.getActiveStatusEffects().entrySet()) {
                if (entry.getKey().getTranslationKey().contains("invisibility")) {
                    skip = true;
                }
            }

            if (skip) {
                return;
            }
        }



        MinecraftClient mc = MinecraftClient.getInstance();

        Quaternionf rotation = mc.getEntityRenderDispatcher().getRotation();

        Text text = entity.getDisplayName();

        boolean bl = !entity.isSneaky();
        float f = entity.getHeight() + 0.5F;
        int i = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(0.0D, f, 0.0D);
        matrices.multiply(rotation);
        matrices.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float g = (MinecraftClient.getInstance()).options.getTextBackgroundOpacity(0.25F);
        int j = (int)(g * 255.0F) << 24;
        TextRenderer textRenderer = mc.textRenderer;
        float h = (-textRenderer.getWidth(ChatColor.stripColor(ChatColor.translateAlternateColorCodes(text.getString()))) / 2);
        textRenderer.draw(text, h, i, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, j, light);
        if (bl) {
            textRenderer.draw(text, h, i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
        matrices.pop();
    }

    /* protected void renderPlayerHud(LivingEntity entity, MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.options.debugEnabled || mc.player == null || mc.world == null || mc.inGameHud != null) {
            return;
        }

        try {
            drawEntityToScreen(200, 50, 30,1, 5, entity, matrices);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawEntityToScreen(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, MatrixStack matrixStack) {
        float f = (float) Math.atan((double)(mouseX / 40.0F));
        float g = (float) Math.atan((double)(mouseY / 40.0F));
        matrixStack.push();
        matrixStack.translate((double)x, (double)y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        Quaternionf quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternionf quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    } */

}
