package net.doodlechaos.dreamvis.mixin.ChunkLoading;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "isRenderingReady", at = @At("TAIL"))
    private void setLoadedToWorld(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            //LOGGER.info("Detected chunk not loaded to world", pos);
        }
    }

}