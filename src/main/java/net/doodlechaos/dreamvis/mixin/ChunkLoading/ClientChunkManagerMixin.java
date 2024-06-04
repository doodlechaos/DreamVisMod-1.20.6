package net.doodlechaos.dreamvis.mixin.ChunkLoading;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

import java.util.function.Consumer;


@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    @Inject(method = "loadChunkFromPacket", at = @At("HEAD"))
    private void setLoadedToWorld(int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> cir) {
        //LOGGER.info("Loading chunk from packet");
    }

}
