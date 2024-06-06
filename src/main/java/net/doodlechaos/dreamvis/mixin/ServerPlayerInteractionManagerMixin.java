package net.doodlechaos.dreamvis.mixin;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "setGameMode", at = @At("HEAD"))
    private void onSetGameMode(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        // Call your custom event here
        DreamVis.OnGameModeChange(previousGameMode, gameMode);
    }

}
