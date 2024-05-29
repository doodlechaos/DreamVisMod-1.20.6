package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SocketCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("socket")
                .executes(ctx -> {
                    // If no argument is provided, print the current directoryPath
                    ServerCommandSource source = ctx.getSource();
                    source.sendFeedback(() -> Text.literal("TODO: Restarting socket: "), false);

                    return 1;
                }));
    }

}
