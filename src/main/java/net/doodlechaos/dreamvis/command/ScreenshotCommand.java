package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.doodlechaos.dreamvis.DreamVis;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static net.doodlechaos.dreamvis.DreamVis.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ScreenshotCommand {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("screenshot")
                .executes(ctx -> {
                    // Handle the case where frameNumber is not provided
                    ensureLoadThenScreenshot(0);
                    return 1;
                })
                .then(argument("frameNumber", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            int frameNum = IntegerArgumentType.getInteger(ctx, "frameNumber");
                            ensureLoadThenScreenshot(frameNum);
                            return 1;
                        })));
    }

    private static void ensureLoadThenScreenshot(int frameNum) {
        ensureChunksLoaded()
                .thenRun(() -> {
                    saveScreenshot(frameNum);
                    ScreenshotDoneFlag = true;
                })
                .exceptionally(e -> {
                    LOGGER.error("Error waiting for chunks to load", e);
                    return null;
                });
    }

    private static CompletableFuture<Void> ensureChunksLoaded() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Runnable checkChunksLoaded = new Runnable() {
            @Override
            public void run() {
                if (DreamVis.ChunksDoneLoading()) {
                    future.complete(null);
                } else {
                    SocketHub.ResetLatch();
                    scheduler.schedule(this, 100, TimeUnit.MILLISECONDS);
                }
            }
        };
        scheduler.schedule(checkChunksLoaded, 100, TimeUnit.MILLISECONDS);
        return future;
    }

    private static boolean saveScreenshot(int frameNum) {
        String paddedFrameNum = String.format("%06d", frameNum); // Pad frameNum with leading zeros
        Path directoryPath = Paths.get(MyConfig.DirectoryPath);

        // Create the necessary folders if they don't exist
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                LOGGER.error("Failed to create directory: " + directoryPath, e);
                return false;
            }
        }

        try {
            File screenshotFile = directoryPath.resolve("frame_" + paddedFrameNum + ".png").toFile();
            ScreenshotRecorder.saveScreenshot(screenshotFile.getParentFile(), screenshotFile.getName(), MinecraftClient.getInstance().getFramebuffer(), simpleLogConsumer);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return false;
        }
        return true;
    }

    public static Consumer<Text> simpleLogConsumer = value -> {
        LOGGER.info(String.valueOf(value));
    };
}