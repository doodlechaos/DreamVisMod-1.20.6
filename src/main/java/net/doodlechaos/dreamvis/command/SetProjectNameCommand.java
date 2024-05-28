package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetProjectNameCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("projectName")
                .executes(ctx -> {
                    // If no argument is provided, print the current directoryPath
                    ServerCommandSource source = ctx.getSource();
                    source.sendFeedback(() -> Text.literal("Current project name: " + MyConfig.ProjectName), false);
                    return 1;
                })
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            MyConfig.ProjectName = name;

                            // Reply to the player with the new directoryPath
                            ServerCommandSource source = ctx.getSource();
                            source.sendFeedback(() -> Text.literal("Project name set to: " + name), false);

                            return 1;
                        })));
    }

}
