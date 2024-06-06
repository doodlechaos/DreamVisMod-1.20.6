package net.doodlechaos.dreamvis;

import net.doodlechaos.dreamvis.networking.SocketHub;

public class CameraController {

    private static boolean HUD_HIDDEN = false;

    public static float RollDegrees = 0;
    public static double MyFOV = 70;

    public static boolean FrustumCulling = true;
    public static String LatestCTP = "";


    public static boolean IsHudHidden() {return HUD_HIDDEN;}

    public static void SetHudHidden(boolean hidden){
        HUD_HIDDEN = hidden;
    }
    public static void SnapToPlayheadKeyframe(){
        SocketHub.ExecuteCommandAsPlayer(LatestCTP);
    }
}
