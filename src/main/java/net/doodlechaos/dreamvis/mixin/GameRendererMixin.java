package net.doodlechaos.dreamvis.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.GameRenderer;

import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;loadProjectionMatrix(Lorg/joml/Matrix4f;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void setblockEntity(BlockEntity blockEntity, CallbackInfo ci){

    }
}