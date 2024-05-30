package net.doodlechaos.dreamvis.mixin.hud;

import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
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

/*    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onAddMessage(Text message, CallbackInfo ci) {
        // This is where you handle the new chat message
        //MinecraftClient.getInstance().player.sendMessage(Text.of("New chat message received: " + message.getString()), false);
    }*/

}