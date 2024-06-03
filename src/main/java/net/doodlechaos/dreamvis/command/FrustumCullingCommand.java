package net.doodlechaos.dreamvis.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.doodlechaos.dreamvis.CameraController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FrustumCullingCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("frustumCulling")
                        .executes(ctx -> {
                            try{

                                String camModeName = CameraController.GetCamMode().name();
                                ctx.getSource().sendFeedback(() -> Text.literal("Frustum Culling is currently: " + ((CameraController.FrustumCulling) ? "enabled" : "disabled")), false);
                                return 1;

                            } catch (Exception e){
                                LOGGER.info("Failed enable frustum culling: " + e);

                            } finally {
                                return 1;
                            }

                        })
                .then(literal("enable")
                        .executes(context -> {
                            try{

                                CameraController.FrustumCulling = true;

                                return 1;
                            } catch (Exception e){
                                LOGGER.info("Failed enable frustum culling: " + e);

                            } finally {
                                return 1;
                            }

                        }))
                .then(literal("disable")
                        .executes(context -> {
                            try{

                                CameraController.FrustumCulling = false;

                                return 1;
                            } catch (Exception e){
                                LOGGER.info("Failed disable frustum culling: " + e);

                            } finally {
                                return 1;
                            }

                        })));
    }
}
