package net.doodlechaos.dreamvis;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
//import net.minecraft.util.math.Vec3f;

import java.util.function.Supplier;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;
import static net.doodlechaos.dreamvis.DreamVis.RollDegrees;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
public class CustomTPCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ctp")
                .then(argument("xpos", FloatArgumentType.floatArg())
                        .then(argument("ypos", FloatArgumentType.floatArg())
                                .then(argument("zpos", FloatArgumentType.floatArg())
                                        .then(argument("xrot", FloatArgumentType.floatArg())
                                                .then(argument("yrot", FloatArgumentType.floatArg())
                                                        .then(argument("zrot", FloatArgumentType.floatArg())
                                                                .executes(context -> {
                                                                    try{
                                                                        ServerCommandSource source = context.getSource();
                                                                        ServerPlayerEntity player = source.getPlayer();
                                                                        float xpos = FloatArgumentType.getFloat(context, "xpos");
                                                                        float ypos = FloatArgumentType.getFloat(context, "ypos");
                                                                        float zpos = FloatArgumentType.getFloat(context, "zpos");
                                                                        float xrot = FloatArgumentType.getFloat(context, "xrot");
                                                                        float yrot = FloatArgumentType.getFloat(context, "yrot");
                                                                        float zrot = FloatArgumentType.getFloat(context, "zrot");

                                                                        player.teleport(source.getWorld(), xpos, ypos, zpos, yrot, xrot);

                                                                        RollDegrees = zrot;

                                                                        LOGGER.info("Finished ctp");

                                                                        return 1;
                                                                    } catch (Exception e){
                                                                        LOGGER.info("Failed ctp: " + e);

                                                                    } finally {
                                                                        return 1;
                                                                    }

                                                                }))))))));
    }
}