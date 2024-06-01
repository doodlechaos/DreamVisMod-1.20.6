package net.doodlechaos.dreamvis.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    private float cameraY;
    @Shadow
    private float lastCameraY;
    @Shadow
    private Entity focusedEntity;

    @Inject(method = "updateEyeHeight", at = @At("HEAD"), cancellable = true)
    public void updateEyeHeight(CallbackInfo ci) {

        GameMode gm = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();

        if(gm == null){
            return;
        }
        if(gm != GameMode.SPECTATOR){
            return;
        }

        //Otherwise we are in spectator mode, so make the camera unadjusted to player head
        if (this.focusedEntity != null) {
            this.cameraY = 0;
            this.lastCameraY = 0;
            //System.out.println("cancelling camera y: " + gm.getName());
            ci.cancel();
        }
    }
}
