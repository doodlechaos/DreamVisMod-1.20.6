package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.DreamVis;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FOVCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fov")
                .executes(ctx -> {
                    // If no argument is provided, print the current directoryPath
                    ServerCommandSource source = ctx.getSource();
                    source.sendFeedback(() -> Text.literal("Current FOV: " + CameraController.MyFOV), false);
                    return 1;
                })
                .then(argument("value", DoubleArgumentType.doubleArg())
                        .executes(ctx -> {

                                double fov = DoubleArgumentType.getDouble(ctx, "value");

                                CameraController.MyFOV = fov;

                                return 1;
                        })));
    }

}
