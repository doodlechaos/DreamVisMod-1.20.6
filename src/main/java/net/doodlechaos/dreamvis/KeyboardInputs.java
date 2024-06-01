package net.doodlechaos.dreamvis;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

import java.text.MessageFormat;

import static net.doodlechaos.dreamvis.DreamVis.*;

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
        Vec3d playerPos = getPlayerPos();
        Vec3d playerRot = getPlayerEulerAngles();
        String message = MessageFormat.format("KEYPRESS=k {0} {1} {2} {3} {4} {5}", playerPos.x, playerPos.y, playerPos.z, playerRot.x, playerRot.y, playerRot.z);
        SocketHub.SendMsgToUnity(message);
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
        var playerList = MinecraftClient.getInstance().getServer().getPlayerManager().getPlayerList();

        var player = playerList.getFirst();

        if(player == null)
            return;

        int nextOrdinal = (CurrCamMode.ordinal() + 1) % CurrCamMode.values().length;
        CurrCamMode = CurrCamMode.values()[nextOrdinal];

        if(CurrCamMode == CamMode.UnityKeyframes){
            player.changeGameMode(GameMode.SPECTATOR);

            // Enable flying
            player.getAbilities().flying = true;
            player.getAbilities().allowFlying = true;
            player.sendAbilitiesUpdate();
        }

        MinecraftClient.getInstance().player.sendMessage(Text.literal("Camera mode set to: " + CurrCamMode), false);
    }

    private void onPKeyPress() {
        LOGGER.info("P KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=p");
    }

    private Vec3d getPlayerPos(){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null)
            return Vec3d.ZERO;

        return MinecraftClient.getInstance().player.getPos();
    }

    private Vec3d getPlayerEulerAngles(){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return Vec3d.ZERO;

        float pitch = client.player.getPitch(); // Rotation around the x-axis
        float yaw = client.player.getYaw(); // Rotation around the y-axis
        float roll = DreamVis.RollDegrees; // Rotation around the z-axis, typically not used in Minecraft
        return new Vec3d(pitch, yaw, roll);
    }
}
