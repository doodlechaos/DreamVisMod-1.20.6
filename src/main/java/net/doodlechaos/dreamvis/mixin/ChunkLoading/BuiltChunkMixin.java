package net.doodlechaos.dreamvis.mixin.ChunkLoading;

import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class BuiltChunkMixin {

    @Inject(method = "<init>", at = @At("TAIL")) //This never stop polling
    private void onUpload(CallbackInfo ci) {
        LOGGER.info("Inside constructor of built chunk");

    }

}
