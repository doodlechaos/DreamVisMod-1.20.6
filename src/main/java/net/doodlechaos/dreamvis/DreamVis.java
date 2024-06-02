package net.doodlechaos.dreamvis;

import net.doodlechaos.dreamvis.command.*;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DreamVis implements ModInitializer {
	public static final String MOD_ID = "dreamvis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int SOCKET_SERVER_PORT = 12345; // Change this to your desired port
	public static final int SOCKET_CLIENT_PORT = 54321; // Change this to your desired port
	public static final String UNITY_ROUTE_NAME = "doodle";

	public static boolean HUD_HIDDEN = false;

	public static float RollDegrees = 0;
	public static double MyFOV = 70;

	public static SocketHub SocketHub;
	public static KeyboardInputs KeyboardInputs;

	public enum CamMode {UnityKeyframes, MCRegular}
	public static CamMode CurrCamMode = CamMode.MCRegular;

	public static String PrevChatMessage = "";

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		MyConfig.LoadFromFile();

		KeyboardInputs = new KeyboardInputs();
		RegisterCommands();
		RegisterEvents();
	}

	private void RegisterCommands(){
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
		});
	}

	private void RegisterEvents(){
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
	}

	public static void OnPlayerSendChatMessage(String msgInWorldCoords){
		if(msgInWorldCoords.startsWith("/recPrevMsg"))
			return;

		PrevChatMessage = msgInWorldCoords;
	}



	private void onEndClientTick(MinecraftClient minecraftClient) {
		if(SocketHub == null || SocketHub.CountDownLatch == null)
			return;
		SocketHub.CountDownLatch.countDown();
	}

	private void onServerStarted(MinecraftServer server) {
		SocketHub = new SocketHub();
	}

	private void onServerStopped(MinecraftServer server){
		MyConfig.SaveToFile();
	}



}