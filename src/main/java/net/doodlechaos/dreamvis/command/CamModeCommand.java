package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import net.doodlechaos.dreamvis.CameraController;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CamModeCommand {

/*    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("camMode")
                .executes(ctx -> {
                    // Get the string name of the current CamMode enum
                    String camModeName = CameraController.GetCamMode().name();
                    ctx.getSource().sendFeedback(() -> Text.literal("Current Cam Mode: " + camModeName), false);
                    return 1;
                })
                .then(literal("Keyframes")
                        .executes(ctx -> {
                            // Set CamMode to UnityKeyframes
                            CameraController.SetCamMode(CameraController.SpectatorCamMode.Keyframes);
                            ctx.getSource().sendFeedback(() -> Text.literal("Cam Mode set to Keyframes"), false);
                            return 1;
                        }))
                .then(literal("FreeCam")
                        .executes(ctx -> {
                            // Set CamMode to MCRegular
                            CameraController.SetCamMode(CameraController.SpectatorCamMode.FreeCam);
                            ctx.getSource().sendFeedback(() -> Text.literal("Cam Mode set to FreeCam"), false);
                            return 1;
                        })));
    }*/

}
