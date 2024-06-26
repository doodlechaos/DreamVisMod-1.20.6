package net.doodlechaos.dreamvis;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.text.MessageFormat;

import static net.doodlechaos.dreamvis.DreamVis.*;

public class KeyboardInputs {

    private static boolean kIsPressed = false;
    private static boolean cIsPressed = false;
    private static boolean pIsPressed = false;
    private static boolean qIsPressed = false;
    private static boolean eIsPressed = false;
    private static boolean leftIsPressed = false;
    private static boolean rightIsPressed = false;
    private static boolean leftCtrlIsPressed = false;

    private static boolean kWasPressed = false;
    private static boolean cWasPressed = false;
    private static boolean pWasPressed = false;
    //private static boolean qWasPressed = false;
    //private static boolean eWasPressed = false;



    public KeyboardInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientEndTick);
    }

    //Sets instantly every frame the key is held down
    public static void OnKeyAction(String code, boolean pressed){

        //if(code.equals("key.keyboard.c") && pressed)
        //    onCKeyPress();
        if(code.equals("key.keyboard.r") && pressed)
            onRKeyPress();
        if(code.equals("key.keyboard.q"))
            qIsPressed = pressed;
        else if(code.equals("key.keyboard.e")){
            var currScreen = MinecraftClient.getInstance().currentScreen;
            var player = GetServerPlayer();

            if(player == null)
                return;

            if(player.interactionManager.getGameMode() != GameMode.SPECTATOR
                    && pressed
                    && (currScreen == null || currScreen instanceof InventoryScreen || currScreen instanceof CreativeInventoryScreen)){
                        togglePlayerInventory();
                        return;
            }
            if(currScreen == null)
                eIsPressed = pressed;
        }
        else if(code.equals("key.keyboard.k") && pressed)
            onKKeyPress();
        else if(code.equals("key.keyboard.p") && pressed)
            onPKeyPress();
        else if(code.equals("key.keyboard.left"))
            leftIsPressed = pressed;
        else if(code.equals("key.keyboard.right"))
            rightIsPressed = pressed;
        else if(code.equals("key.keyboard.left.control"))
            leftCtrlIsPressed = pressed;
        else if(code.equals("key.keyboard.down") && pressed)
            CameraController.SnapToPlayheadKeyframe();
    }

    private void onClientEndTick(MinecraftClient minecraftClient) {

        if(qIsPressed)
            onQKeyHeld(leftCtrlIsPressed);

        if(eIsPressed)
            onEKeyHeld(leftCtrlIsPressed);

        if(rightIsPressed)
            onRightKeyHeld(leftCtrlIsPressed);

        if(leftIsPressed)
            onLeftKeyHeld(leftCtrlIsPressed);

    }


    private static void onKKeyPress() {
        return; //TODO
/*        LOGGER.info("K KEY DETECTED");
        Vec3d playerPos = getPlayerPos();
        Vec3d playerRot = getPlayerEulerAngles();
        String message = MessageFormat.format("KEYPRESS=k {0} {1} {2} {3} {4} {5}", playerPos.x, playerPos.y, playerPos.z, playerRot.x, playerRot.y, playerRot.z);
        SocketHub.SendMsgToUnity(message);*/
    }

    public static void onRKeyPress(){
        DreamVis.SocketHub.SendMsgToUnity("Marker=" + DreamVis.PrevChatMessage);
        var player = DreamVis.GetServerPlayer();
        if(player == null)
            return;

        player.sendMessage(Text.of("Created marker in unity for: " + PrevChatMessage));
    }

    private static void onLeftKeyHeld(boolean withShift) {
        LOGGER.info("LEFT ARROW KEY DETECTED");
        if(withShift)
            SocketHub.SendMsgToUnity("KEYPRESS=left+shift");
        else
            SocketHub.SendMsgToUnity("KEYPRESS=left");
    }

    private static void onRightKeyHeld(boolean withShift) {
        LOGGER.info("RIGHT ARROW KEY DETECTED");
        if(withShift)
            SocketHub.SendMsgToUnity("KEYPRESS=right+shift");
        else
            SocketHub.SendMsgToUnity("KEYPRESS=right");
    }

    private static void onPKeyPress() {
        LOGGER.info("P KEY DETECTED");
        SocketHub.SendMsgToUnity("KEYPRESS=p");
    }

    private static Vec3d getPlayerPos(){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null)
            return Vec3d.ZERO;

        return MinecraftClient.getInstance().player.getPos();
    }

    private static Vec3d getPlayerEulerAngles(){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return Vec3d.ZERO;

        float pitch = client.player.getPitch(); // Rotation around the x-axis
        float yaw = client.player.getYaw(); // Rotation around the y-axis
        float roll = CameraController.RollDegrees; // Rotation around the z-axis, typically not used in Minecraft
        return new Vec3d(pitch, yaw, roll);
    }

    private static void onQKeyHeld(boolean withShift){
        LOGGER.info("Q KEY DETECTED");
        if(SocketHub == null)
            return;

        CameraController.RollDegrees += (float) ((withShift) ? 3.5 : 0.6);
    }

    private static void onEKeyHeld(boolean withShift){
        LOGGER.info("E KEY DETECTED");
        CameraController.RollDegrees -= (float) ((withShift) ? 3.5 : 0.6);
    }

    public static void togglePlayerInventory() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {

            if (client.currentScreen != null && (client.currentScreen instanceof InventoryScreen || client.currentScreen instanceof CreativeInventoryScreen)){
                LOGGER.info("setting screen to null");
                client.player.closeHandledScreen();
            }
            else{
                client.setScreen(new InventoryScreen(client.player));
                LOGGER.info("setting screen to inventory screen");

            }
            if(client.currentScreen != null)
                LOGGER.info("curr screen title: " + client.currentScreen.getClass());
            else
                LOGGER.info("curr screen title is null");
        }
    }
}
