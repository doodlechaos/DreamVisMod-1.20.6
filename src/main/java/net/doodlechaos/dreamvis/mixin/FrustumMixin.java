//package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.CameraController;
import net.fabricmc.fabric.mixin.rendering.data.client.ChunkRendererRegionMixin;
import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//WARNING: THIS IS OVERWRITTEN BY SODIUM
/*@Mixin(Frustum.class)
public class FrustumMixin {
    @Inject(method = "isVisible*", at = @At("HEAD"), cancellable = true)
    public void isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<Boolean> cir) {
        if(CameraController.FrustumCulling){
            return;
        }
        cir.setReturnValue(true);
    }
}*/
