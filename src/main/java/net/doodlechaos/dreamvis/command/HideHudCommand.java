package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HideHudCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("hideHud")
            .then(argument("hide", BoolArgumentType.bool())
                .executes(context -> {
                    try{
                        boolean hide = BoolArgumentType.getBool(context, "hide");

                        MinecraftClient.getInstance().options.hudHidden = hide;
                        CameraController.SetHudHidden(hide);

                        return 1;
                    } catch (Exception e){
                        LOGGER.info("Failed hide hud: " + e);

                    } finally {
                        return 1;
                    }

                })));
    }
}
