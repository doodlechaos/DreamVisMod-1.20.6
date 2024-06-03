package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SocketCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("socket")
                .then(literal("initServer")
                        .executes(ctx -> {
                            // Execute the specific command to restart the server
                            DreamVis.SocketHub.InitWebSocketServer();

                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(() -> Text.literal("Initializing socket server..."), false);

                            return 1;
                        }))
                .then(argument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String msg = StringArgumentType.getString(ctx, "msg");

                            // If the message is not "restartServer", send it to Unity
                            DreamVis.SocketHub.SendMsgToUnity(msg);

                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(() -> Text.literal("Sending message to Unity: " + msg), false);

                            return 1;
                        })));
    }
}
