package net.doodlechaos.dreamvis.networking;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.doodlechaos.dreamvis.DreamVis.*;

public class SocketHub {

    private static MyWebSocketClient _myWsClient;
    private static MyWebSocketServer _myWsServer;

    public SocketHub(){

        InitWebSocketServer();
        InitWebSocketClient();

    }

    private void InitWebSocketServer(){

        try{
            if(_myWsServer != null){
                _myWsServer.stop();
            }

            _myWsServer = new MyWebSocketServer(this, SOCKET_SERVER_PORT);
            _myWsServer.start();
            LOGGER.info("WebSocketServer started on port: " + _myWsServer.getPort());
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
    }

    private void InitWebSocketClient() {
        new Thread(() -> {
            try {
                String URI_String = "ws://127.0.0.1:" + SOCKET_CLIENT_PORT + "/" + UNITY_ROUTE_NAME;
                URI uri = new URI(URI_String);
                LOGGER.info("Before attempting to connect client on URI: " + uri.toString());

                _myWsClient = new MyWebSocketClient(uri);

                LOGGER.info("Client draft: " + _myWsClient.getDraft().toString());
                // This should ideally be a blocking connect call to ensure connection is established before proceeding
                if (_myWsClient.connectBlocking()) {
                    LOGGER.info("Client connected on URI: " + uri.toString());
                } else {
                    LOGGER.error("Client failed to connect on URI: " + uri.toString());
                }

                //_myWsClient.send("message from inside another thread");

            } catch (URISyntaxException e) {
                LOGGER.error("URI Syntax Error: " + e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.error("Connection interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restore the interrupted status
            } catch (Exception e) {
                LOGGER.error("Failed to connect: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void OnUnityMessageReceived(String msg){
        LOGGER.info("RECEIVED MESSAGE FROM UNITY: " + msg);

        ExecuteCommandAsPlayer(msg);
    }

    //TODO: move this to somewhere else?
    private void ExecuteCommandAsPlayer(String command){
        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
        List<ServerPlayerEntity> playerList = minecraftServer.getPlayerManager().getPlayerList();
        if (playerList.isEmpty())
            return;

        ServerPlayerEntity player = minecraftServer.getPlayerManager().getPlayerList().get(0);

        if (minecraftServer == null) {
            LOGGER.error("Minecraft server is not available.");
            return;
        }

        minecraftServer.execute(() -> {
            try {
                // Get the nearest player to the server's default position
                if (player == null) {
                    LOGGER.error("No players are currently online.");
                    return;
                }

                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();
                ServerCommandSource playerCommandSource = player.getCommandSource();

                var parsedCommand = dispatcher.parse(command, playerCommandSource);
                dispatcher.execute(parsedCommand);
            } catch (CommandSyntaxException e) {
                LOGGER.error("Failed to execute command [" + command + "]: " + e.toString());
            }
        });
    }

    private void ExecuteCommandAsServer(String command){
        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
        // Execute the command on the Minecraft server
        minecraftServer.execute(() -> {
            try {
                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();
                var parsedCommand = dispatcher.parse(command, minecraftServer.getCommandSource());
                dispatcher.execute(parsedCommand);
            } catch (CommandSyntaxException e) {
                LOGGER.error("Failed to execute command [" + command + "]" + e.toString());
            } finally {
                // Count down the latch to indicate that the command execution is complete
                //latch.countDown();
                LOGGER.info("Latch counted down for command: " + command);
            }
        });
    }

    public void SendMsgToUnity(String msg){
        if(_myWsClient == null || !_myWsClient.isOpen()){
            LOGGER.info("client isn't open, reinitializing");
            InitWebSocketClient();
        }
        LOGGER.info("sending message to unity server: " + msg);

        try{
            _myWsClient.send(msg);
        }catch (Exception e){
            LOGGER.error(e.toString());
        }

    }
}
