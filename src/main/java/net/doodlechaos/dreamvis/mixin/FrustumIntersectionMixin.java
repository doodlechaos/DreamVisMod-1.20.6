package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.CameraController;
import org.joml.FrustumIntersection;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrustumIntersection.class)
public class FrustumIntersectionMixin {

    @Inject(method = "testPoint(FFF)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectTestPoint(float x, float y, float z, CallbackInfoReturnable<Boolean> cir) {
        if (!CameraController.FrustumCulling) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "testSphere(FFFF)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectTestSphere(float x, float y, float z, float r, CallbackInfoReturnable<Boolean> cir) {
        if (!CameraController.FrustumCulling) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "intersectSphere(FFFF)I", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectIntersectSphere(float x, float y, float z, float r, CallbackInfoReturnable<Integer> cir) {
        if (!CameraController.FrustumCulling) {
            cir.setReturnValue(FrustumIntersection.INSIDE);
        }
    }

    @Inject(method = "testAab(FFFFFF)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectTestAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable<Boolean> cir) {
        if (!CameraController.FrustumCulling) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "intersectAab(FFFFFF)I", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectIntersectAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable<Integer> cir) {
        if (!CameraController.FrustumCulling) {
            cir.setReturnValue(FrustumIntersection.INSIDE);
        }
    }
}