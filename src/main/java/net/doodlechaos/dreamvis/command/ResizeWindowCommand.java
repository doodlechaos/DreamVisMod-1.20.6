package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.server.command.ServerCommandSource;
import org.lwjgl.glfw.GLFW;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ResizeWindowCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("resizeWindow")
                .then(argument("width", IntegerArgumentType.integer(0))
                        .then(argument("height", IntegerArgumentType.integer(0))
                                .executes(ctx -> {

                                    int width = IntegerArgumentType.getInteger(ctx, "width");
                                    int height = IntegerArgumentType.getInteger(ctx, "height");
                                    long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();

                                    GLFW.glfwSetWindowSizeLimits(windowHandle, 100, 100, 5000, 5000);
                                    GLFW.glfwSetWindowSize(windowHandle, width, height);
                                    return 1;
                                }))));
    }

}
