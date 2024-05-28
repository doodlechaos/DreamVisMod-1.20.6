package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ScreenshotCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("screenshot")
                .then(argument("frameNumber", IntegerArgumentType.integer())
                        .executes(ctx -> {

                            int frameNum = IntegerArgumentType.getInteger(ctx, "frameNumber");
                            String paddedFrameNum = String.format("%06d", frameNum); // Pad frameNum with leading zeros

                            Path directoryPath = Paths.get(MyConfig.DirectoryPath);

                            // Create the necessary folders if they don't exist
                            if (!Files.exists(directoryPath)) {
                                try {
                                    Files.createDirectories(directoryPath);
                                } catch (IOException e) {
                                    LOGGER.error("Failed to create directory: " + directoryPath, e);
                                    return 0;
                                }
                            }

                            try{
                                File screenshotFile = directoryPath.resolve("frame_" + paddedFrameNum + ".png").toFile();
                                ScreenshotRecorder.saveScreenshot(screenshotFile.getParentFile(), screenshotFile.getName(), MinecraftClient.getInstance().getFramebuffer(), simpleLogConsumer);
                            }catch (Exception e){
                                LOGGER.error(e.toString());
                            }
                            return 1;
                        })));
    }

    public static Consumer<Text> simpleLogConsumer = value -> {
        LOGGER.info(String.valueOf(value));
    };
}
