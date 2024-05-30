package net.doodlechaos.dreamvis;

import net.doodlechaos.dreamvis.command.*;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.doodlechaos.dreamvis.networking.MyWebSocketServer;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		MyConfig.LoadFromFile();


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
		});
	}

	private void RegisterEvents(){
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
		ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);
		ServerMessageEvents.COMMAND_MESSAGE.register(this::onCommandMessage);
		ServerTickEvents.END_WORLD_TICK.register(this::onEndWorldTick);
		ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
	}

	private void onEndServerTick(MinecraftServer minecraftServer) {
		//LOGGER.info("server tick. Task count: " + minecraftServer.getTaskCount());

//		if(SocketHub.CountDownLatch == null)
	//		return;
		//SocketHub.CountDownLatch.countDown();
	}

	private void onEndWorldTick(ServerWorld serverWorld) {
		//LOGGER.info("world tick");
	}

	private void onChatMessage(SignedMessage signedMessage, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters) {
		String msg = signedMessage.getContent().getString();
		LOGGER.info("on chat message: " + msg);
		CheckForDoneSignal(msg);
	}
	private void onCommandMessage(SignedMessage signedMessage, ServerCommandSource serverCommandSource, MessageType.Parameters parameters) {
		String msg = signedMessage.getContent().getString();
		LOGGER.info("on command message: " + msg);
		CheckForDoneSignal(msg);
	}

	private void CheckForDoneSignal(String mcChatMsg){
		//if(mcChatMsg.equals("DONE")){
		//	SocketHub.SendMsgToUnity(mcChatMsg);
		//}
	}

	private void onServerStarted(MinecraftServer server) {

		SocketHub = new SocketHub();

	}

	private void onServerStopped(MinecraftServer server){
		MyConfig.SaveToFile();
	}
}