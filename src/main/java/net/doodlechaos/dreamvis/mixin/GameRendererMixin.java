package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld(FJ)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/util/math/Vec3d;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void myRenderWorldMod(float tickDelta, long limitTime, CallbackInfo ci, boolean bl, Camera camera, Entity entity, double d, Matrix4f matrix4f, MatrixStack matrixStack,
                                  float f, float g, Matrix4f matrix4f2) {

        float pitchRadians = camera.getPitch() * 0.017453292F;
        float yawRadians = camera.getYaw() * 0.017453292F + 3.1415927F;
        float rollRadians = DreamVis.RollDegrees * 0.017453292F;

        // Reset the matrix to identity
        matrix4f2.identity();

        // Apply roll rotation first to ensure it's in the camera's local coordinate space
        matrix4f2.rotateZ(-rollRadians);

        // Apply pitch and yaw rotations
        matrix4f2.rotateX(pitchRadians).rotateY(yawRadians);
    }

    @Inject(at = @At("TAIL"), method = "getFov", cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(DreamVis.MyFOV);
    }
}