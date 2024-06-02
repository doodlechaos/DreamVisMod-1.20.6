package net.doodlechaos.dreamvis;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.GameMode;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

public class CameraController {

    private static boolean HUD_HIDDEN = false;

    public static float RollDegrees = 0;
    public static double MyFOV = 70;

    public enum CamMode {UnityKeyframes, MCRegular}
    private static CamMode CurrCamMode = CamMode.MCRegular;

    public static boolean IsHudHidden() {return HUD_HIDDEN;}
    public static CamMode GetCamMode() {return CurrCamMode;}

    public static void SetHudHidden(boolean hidden){
        HUD_HIDDEN = hidden;
    }

    public static void SetCamMode(CamMode mode){
        CurrCamMode = mode;

        var playerList = MinecraftClient.getInstance().getServer().getPlayerManager().getPlayerList();
        var player = playerList.getFirst();
        if(player == null){
            LOGGER.error("Failed to find player in set cam mode");
            return;
        }

        if(mode == CameraController.CamMode.UnityKeyframes){
            player.changeGameMode(GameMode.SPECTATOR);

            // Enable flying
            player.getAbilities().flying = true;
            player.getAbilities().allowFlying = true;
            player.sendAbilitiesUpdate();
            LOGGER.info("Set cam mode to unity keyframes");
        }

        if(mode == CamMode.MCRegular){
            player.changeGameMode(GameMode.CREATIVE);
            SetHudHidden(false);
            LOGGER.info("Set cam mode to mc regular");
        }
    }
}
