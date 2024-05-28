package net.doodlechaos.dreamvis.mixin.hud;

import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin  {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci){
        if(DreamVis.HUD_HIDDEN){
            ci.cancel();
        }
    }
}