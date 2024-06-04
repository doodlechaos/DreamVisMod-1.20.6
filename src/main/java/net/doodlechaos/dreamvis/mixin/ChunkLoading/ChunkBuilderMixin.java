
package net.doodlechaos.dreamvis.mixin.ChunkLoading;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin {

    @Inject(method = "upload", at = @At("HEAD")) //This never stop polling
    private void onUpload(CallbackInfo ci) {
        //LOGGER.info("ChunkBuilder is uploading");

    }

    @Inject(method = "isEmpty", at = @At("TAIL")) //This never activated
    private void isEmpty(CallbackInfoReturnable<Boolean> cir) {
/*        if(cir.getReturnValue() != true){
            LOGGER.info("chunkbuilder is not empty");
        }*/
    }

    @Inject(method = "send", at = @At("HEAD")) //This is never called
    private void onSend(CallbackInfo ci) {
        //LOGGER.info("ChunkBuilder is using SEND");

    }

    @Inject(method = "getChunksToUpload", at = @At("TAIL")) //Never called
    private void onGetChunksToUpload(CallbackInfoReturnable<Integer> cir) {
        //LOGGER.info("inside on get chunks to upload: " + cir.getReturnValue());

    }



}

