package net.doodlechaos.dreamvis;

import net.doodlechaos.dreamvis.command.*;
import net.doodlechaos.dreamvis.config.MyConfig;
import net.doodlechaos.dreamvis.networking.SocketHub;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.MinecraftServer;

import org.lwjgl.glfw.GLFW;
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

	//private static KeyBinding KkeyBinding;
	//private static boolean KwasPressed = false;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		MyConfig.LoadFromFile();

		// Create the key binding
/*		KkeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.mymod.k", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard
				GLFW.GLFW_KEY_K, // The keycode of the key
				"category.mymod.test" // The translation key of the keybinding's category
		));*/
		// Register the client tick event to check the key press

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
		});
	}

	private void RegisterEvents(){
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
/*		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (KkeyBinding.isPressed()) {
				if (!KwasPressed) {
					onKKeyPress();
					KwasPressed = true;
				}
			} else {
				KwasPressed = false;
			}
		});*/
	}

/*	private void onKKeyPress() {
		LOGGER.info("K KEY DETECTED");
		SocketHub.SendMsgToUnity("KEYPRESS=k");
	}*/

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