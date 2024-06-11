package net.doodlechaos.dreamvis;

import net.doodlechaos.dreamvis.command.*;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.doodlechaos.dreamvis.events.RightClickListener;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DreamVis implements ModInitializer {
	public static final String MOD_ID = "dreamvis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int SOCKET_SERVER_PORT = 12345; // Change this to your desired port
	public static final int SOCKET_CLIENT_PORT = 54321; // Change this to your desired port
	public static final String UNITY_ROUTE_NAME = "doodle";

	public static SocketHub SocketHub;
	public static KeyboardInputs KeyboardInputs;

	public static String PrevChatMessage = "";

	private static long _lastChunkLoadTime = 0;

	private int _prevTickCompletedChunkCount = 0;

	public static boolean ScreenshotDoneFlag = false;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		MyConfig.LoadFromFile();
		SocketHub = new SocketHub();

		KeyboardInputs = new KeyboardInputs();
		registerCommands();
		registerEvents();
	}

	private void registerCommands(){
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CustomTPCommand.register(dispatcher);
			HideHudCommand.register(dispatcher);
			ResizeWindowCommand.register(dispatcher);
			ScreenshotCommand.register(dispatcher);
			SetProjectDirectoryCommand.register(dispatcher);
			SetProjectNameCommand.register(dispatcher);
			FOVCommand.register(dispatcher);
			SocketCommand.register(dispatcher);
			RecPrevMsgCommand.register(dispatcher);
			FrustumCullingCommand.register(dispatcher);
		});
	}

	private void registerEvents(){
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick2);
		ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
		ClientChunkEvents.CHUNK_LOAD.register(this::onClientChunkLoad);
		ServerChunkEvents.CHUNK_LOAD.register(this::onServerChunkLoad);

		RightClickListener.register();
	}

	private void onServerChunkLoad(ServerWorld serverWorld, WorldChunk worldChunk) {
		_lastChunkLoadTime = System.currentTimeMillis();
		//LOGGER.info("Detected server chunk load: " );
	}

	private void onClientChunkLoad(ClientWorld clientWorld, WorldChunk worldChunk) {
		_lastChunkLoadTime = System.currentTimeMillis();
		//LOGGER.info("Detected client chunk load: " );
	}


	public static void OnPlayerSendChatMessage(String msgInWorldCoords){
		if(msgInWorldCoords.startsWith("/recPrevMsg"))
			return;

		PrevChatMessage = msgInWorldCoords;
	}



	private void onEndClientTick(MinecraftClient minecraftClient) {
		if(SocketHub == null || SocketHub.ClientTickLatch == null)
			return;

		SocketHub.ClientTickLatch.countDown();
	}

	private void onEndClientTick2(MinecraftClient minecraftClient) {
		var worldRenderer = minecraftClient.worldRenderer;
		if(worldRenderer == null)
			return;

		try{
			int completedChunkCount = worldRenderer.getCompletedChunkCount();

			if(completedChunkCount != _prevTickCompletedChunkCount)
				_lastChunkLoadTime = System.currentTimeMillis();

			_prevTickCompletedChunkCount = completedChunkCount;
		} catch (Exception e){

		}

	}


	private void onEndServerTick(MinecraftServer minecraftServer) {
		if(SocketHub == null || SocketHub.ServerTickLatch == null)
			return;
		SocketHub.ServerTickLatch.countDown();
	}


	private void onServerStarted(MinecraftServer server) {
	}

	private void onServerStopped(MinecraftServer server){
		MyConfig.SaveToFile();
	}

	public static boolean ChunksDoneLoading(){
		float timeSinceLastChunkJob = System.currentTimeMillis() - _lastChunkLoadTime;
		if(timeSinceLastChunkJob > 1000
				&& MinecraftClient.getInstance().worldRenderer.isTerrainRenderComplete()){ //If no chunks were loaded in the last second, assume we are done
			return true;
		}
		//LOGGER.info("waiting for chunks to load, timeSinceLastChunkJob: " + timeSinceLastChunkJob);
		return false;
	}

	public static ServerPlayerEntity GetServerPlayer(){
		IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
		if (minecraftServer == null) {
			LOGGER.error("Minecraft server is not available.");
			return null;
		}

		var playerList = minecraftServer.getPlayerManager().getPlayerList();

		if(playerList.isEmpty())
			return null;

		return playerList.get(0);
	}

	public static void OnGameModeChange(GameMode prevGameMode, GameMode newGameMode){
		LOGGER.info("SETTING GAME MODE TO: " + newGameMode.asString());
		var player = GetServerPlayer();

		if(player == null)
			return;

		if(newGameMode == GameMode.SPECTATOR){
			// Enable flying
			player.getAbilities().flying = true;
			player.getAbilities().allowFlying = true;
			player.sendAbilitiesUpdate();
			LOGGER.info("Set cam mode to unity keyframes");

			//Snap to the correct playhead position
			CameraController.SnapToPlayheadKeyframe();
		}

		if(newGameMode == GameMode.CREATIVE){
			CameraController.RollDegrees = 0;

		}
	}
}