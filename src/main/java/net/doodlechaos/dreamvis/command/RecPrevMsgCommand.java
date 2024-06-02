package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.literal;

public class RecPrevMsgCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("recPrevMsg")
            .executes(ctx -> {
                try{
                    // Reply to the player with the new directoryPath
                    ServerCommandSource source = ctx.getSource();

                    DreamVis.SocketHub.SendMsgToUnity("Marker=" + DreamVis.PrevChatMessage);

                    source.sendFeedback(() -> Text.literal("Creating Unity marker for [" + DreamVis.PrevChatMessage + "]"), false);

                    return 1;
                } catch (Exception e){
                    LOGGER.info("Failed hide hud: " + e);

                } finally {
                    return 1;
                }

            }));
    }

}
