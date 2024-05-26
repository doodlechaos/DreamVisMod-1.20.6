package net.doodlechaos.dreamvis;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DreamVis implements ModInitializer {
	public static final String MOD_ID = "dreamvis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final int SOCKET_PORT = 12345; // Change this to your desired port

	public static float RollDegrees = 0;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CustomTPCommand.register(dispatcher);
		});
	}

	private void onServerStarted(MinecraftServer server) {
		SocketServer socketServer = new SocketServer(SOCKET_PORT, server);
		socketServer.start();
	}
}