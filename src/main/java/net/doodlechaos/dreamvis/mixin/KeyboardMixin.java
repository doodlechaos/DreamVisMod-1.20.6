package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.KeyboardInputs;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.glfw.GLFW;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {

        if(action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE)
            return;

        String keyName = InputUtil.fromKeyCode(key, scancode).getTranslationKey();
        LOGGER.info("detected keyboard action on: " + keyName);
        boolean pressed = (action == GLFW.GLFW_PRESS) ? true : false;

        KeyboardInputs.OnKeyAction(keyName, pressed);

    }
}

