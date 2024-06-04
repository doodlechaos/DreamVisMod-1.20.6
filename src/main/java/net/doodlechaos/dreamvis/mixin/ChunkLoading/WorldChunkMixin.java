package net.doodlechaos.dreamvis.mixin.ChunkLoading;

import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Inject(method = "setLoadedToWorld", at = @At("HEAD"))
    private void setLoadedToWorld(boolean loadedToWorld, CallbackInfo ci) {
        //LOGGER.info("SET LOADED TO WORLD: " + loadedToWorld); This is out of sync with sodium
    }

}
