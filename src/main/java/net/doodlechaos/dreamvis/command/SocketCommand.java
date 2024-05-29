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
                .then(argument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {

                            String msg = StringArgumentType.getString(ctx, "msg");
                            DreamVis.SocketHub.SendMsgToUnity(msg);

                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(() -> Text.literal("sending message to Unity: " + msg), false);

                    return 1;
                })));
    }

}
