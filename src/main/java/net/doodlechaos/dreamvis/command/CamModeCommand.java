package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CamModeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("camMode")
                .executes(ctx -> {
                    // Get the string name of the current CamMode enum
                    String camModeName = CameraController.GetCamMode().name();
                    ctx.getSource().sendFeedback(() -> Text.literal("Current Cam Mode: " + camModeName), false);
                    return 1;
                })
                .then(literal("UnityKeyframes")
                        .executes(ctx -> {
                            // Set CamMode to UnityKeyframes
                            CameraController.SetCamMode(CameraController.CamMode.UnityKeyframes);
                            ctx.getSource().sendFeedback(() -> Text.literal("Cam Mode set to UnityKeyframes"), false);
                            return 1;
                        }))
                .then(literal("MCRegular")
                        .executes(ctx -> {
                            // Set CamMode to MCRegular
                            CameraController.SetCamMode(CameraController.CamMode.MCRegular);
                            ctx.getSource().sendFeedback(() -> Text.literal("Cam Mode set to MCRegular"), false);
                            return 1;
                        })));
    }

}
