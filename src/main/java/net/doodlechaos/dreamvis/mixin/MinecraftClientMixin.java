package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.DreamVis;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(boolean tick, CallbackInfo ci) {

        if(DreamVis.LOGGER == null)
            return;

        if(DreamVis.SocketHub == null)
            return;

        if(DreamVis.SocketHub.CountDownLatch == null)
            return;

        DreamVis.SocketHub.CountDownLatch.countDown();
        DreamVis.LOGGER.info("Counting down latch in client");
    }
}
