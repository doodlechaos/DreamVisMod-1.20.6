package net.doodlechaos.dreamvis.mixin.hud;

import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, float tickDelta, CallbackInfo ci){
        if(DreamVis.HUD_HIDDEN){
            ci.cancel();
        }
    }
}