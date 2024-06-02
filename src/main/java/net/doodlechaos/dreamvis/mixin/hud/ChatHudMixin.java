package net.doodlechaos.dreamvis.mixin.hud;

import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin  {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci){
        if(CameraController.IsHudHidden()){
            ci.cancel();
        }
    }

}