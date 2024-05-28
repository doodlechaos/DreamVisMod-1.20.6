package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetProjectDirectoryCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("projectDirectory")
                .executes(ctx -> {
                    // If no argument is provided, print the current directoryPath
                    ServerCommandSource source = ctx.getSource();
                    source.sendFeedback(() -> Text.literal("Current project directory: " + MyConfig.DirectoryPath), false);
                    return 1;
                })
                .then(argument("directoryPath", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String directoryPath = StringArgumentType.getString(ctx, "directoryPath");
                            MyConfig.DirectoryPath = directoryPath;

                            // Reply to the player with the new directoryPath
                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(() -> Text.literal("Project directory set to: " + directoryPath), false);

                            return 1;
                        })));
    }

}
