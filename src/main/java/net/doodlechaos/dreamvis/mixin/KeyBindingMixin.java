package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.KeyboardInputs;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.doodlechaos.dreamvis.DreamVis.LOGGER;


@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"))
    private static void onKeyPress(InputUtil.Key key, CallbackInfo ci) {
        //LOGGER.info(key.getTranslationKey());
        //KeyboardInputs.OnKeyPress(key.getTranslationKey());
    }
}
