package net.doodlechaos.dreamvis;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.doodlechaos.dreamvis.DreamVis.SocketHub;

public class KeyboardInputs {

    private static KeyBinding kKeyBinding;
    private static KeyBinding leftKeyBinding;
    private static KeyBinding rightKeyBinding;
    private static KeyBinding cKeyBinding;
    private static KeyBinding pKeyBinding;

    private boolean kWasPressed = false;
    private boolean cWasPressed = false;
    private boolean pWasPressed = false;


    public KeyboardInputs(){
        RegisterKeyBindings();
        RegisterEvents();
    }

    public void RegisterKeyBindings(){
        kKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.k",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.mymod.keys"
        ));
        leftKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.left",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT,
                "category.mymod.keys"
        ));
        rightKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.right",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT,
                "category.mymod.keys"
        ));
        cKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.c",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.mymod.keys"
        ));
        pKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.p",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.mymod.keys"
        ));
    }

    public void RegisterEvents(){
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
    }

    private void onEndClientTick(MinecraftClient minecraftClient) {
        if (kKeyBinding.isPressed()) {
            if (!kWasPressed) {
                onKKeyPress();
                kWasPressed = true;
            }
        } else {
            kWasPressed = false;
        }

        if (cKeyBinding.isPressed()) {
            if (!cWasPressed) {
                onCKeyPress();
                cWasPressed = true;
            }
        } else {
            cWasPressed = false;
        }

        if (pKeyBinding.isPressed()) {
            if (!pWasPressed) {
                onPKeyPress();
                pWasPressed = true;
            }
        } else {
            pWasPressed = false;;
        }

        // Continuous detection for other keys
        if (leftKeyBinding.isPressed()) {
            onLeftKeyPress();
        }
        if (rightKeyBinding.isPressed()) {
            onRightKeyPress();
        }

    }
    private void onKKeyPress() {
        LOGGER.info("K KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=k");
    }

    private void onLeftKeyPress() {
        LOGGER.info("LEFT ARROW KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=left");
    }

    private void onRightKeyPress() {
        LOGGER.info("RIGHT ARROW KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=right");
    }

    private void onCKeyPress() {
        LOGGER.info("C KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=c");
    }

    private void onPKeyPress() {
        LOGGER.info("P KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=p");
    }
}
