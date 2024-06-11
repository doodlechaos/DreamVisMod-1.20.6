package net.doodlechaos.dreamvis.events;
import net.doodlechaos.dreamvis.DreamVis;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import java.util.Map;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

public class RightClickListener {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                Item heldItem = player.getStackInHand(hand).getItem();
                if (heldItem == Items.STONE_AXE) {
                    BlockPos pos = hitResult.getBlockPos();
                    BlockState blockState = world.getBlockState(pos);
                    String blockName = getBlockNameWithProperties(blockState);
                    String cmd = String.format("/setblock %d %d %d %s destroy", pos.getX(), pos.getY(), pos.getZ(), blockName);

                    // Send the command to the player
                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) player).sendMessage(Text.of(cmd), false);
                    }

                    // Optionally, print to the server log
                    LOGGER.info("Generated setblock command: " + cmd);
                    DreamVis.PrevChatMessage = cmd;
                    return ActionResult.CONSUME;

                }
            }
            return ActionResult.PASS;
        });
    }

    private static String getBlockNameWithProperties(BlockState state) {
        // Get the block's registry name
        String blockName = Registries.BLOCK.getId(state.getBlock()).toString();

        // Build the block state string
        StringBuilder stateString = new StringBuilder();
        if (!state.getEntries().isEmpty()) {
            stateString.append("[");
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
                if (stateString.length() > 1) {
                    stateString.append(",");
                }
                stateString.append(entry.getKey().getName()).append("=").append(getPropertyValue(entry.getKey(), entry.getValue()));
            }
            stateString.append("]");
        }
        return blockName + stateString;
    }

    private static <T extends Comparable<T>> String getPropertyValue(Property<T> property, Comparable<?> value) {
        return property.name((T) value);
    }
}
