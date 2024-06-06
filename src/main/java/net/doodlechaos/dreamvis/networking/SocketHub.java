package net.doodlechaos.dreamvis.networking;

import com.mojang.brigadier.CommandDispatcher;
import net.doodlechaos.dreamvis.CameraController;
import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.java_websocket.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static net.doodlechaos.dreamvis.DreamVis.*;

public class SocketHub {

    private static MyWebSocketClient _myWsClient;
    private static MyWebSocketServer _myWsServer;

    public static CountDownLatch ClientTickLatch;
    public static CountDownLatch ServerTickLatch;

    public SocketHub(){

        InitWebSocketServer();
        InitWebSocketClient();

    }

    public void InitWebSocketServer(){

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

    public void OnUnityMessageReceived(WebSocket conn, String msg, boolean blocking){
        LOGGER.info("RECEIVED MESSAGE FROM UNITY: " + msg);

        String command = msg;
        if(command.startsWith("/")) //The execute input doesn't take in a slash at the start of the command
            command = command.substring(1);

        if(command.startsWith("ctp")){
            HandleCustomTeleport(conn, command, blocking);
            return;
        }

        ExecuteCommandAsPlayer(conn, command, blocking);
    }

    private void HandleCustomTeleport(WebSocket conn, String msg, boolean blocking){
        CameraController.LatestCTP = msg;
        var player = DreamVis.GetServerPlayer();
        if(player == null)
            return;

        if(player.interactionManager.getGameMode() != GameMode.SPECTATOR){
            conn.send("Not in spectator mode, ignoring Custom TP");
            return;
        }
        ExecuteCommandAsPlayer(conn, msg, blocking);
    }


    //TODO: move this to somewhere else?
    private void ExecuteCommandAsPlayer(WebSocket conn, String command, boolean blocking){
        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();

        if (minecraftServer == null) {
            LOGGER.error("Minecraft server is not available.");
            conn.send("Minecraft server is not available."); //Ack
            return;
        }

        ResetLatch();

        minecraftServer.execute(() -> {
            try {
                var player = DreamVis.GetServerPlayer();

                if (player == null) {
                    LOGGER.error("No players are currently online.");
                    conn.send("No players are currently online."); //Ack
                    return;
                }
                // Set up a CountDownLatch to wait for the command completion
                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();

                var parsedCommand = dispatcher.parse(command, player.getCommandSource());

                dispatcher.execute(parsedCommand);

                LOGGER.info("Command executed successfully");

            } catch (Exception e) {
                LOGGER.error("Failed to execute command [" + command + "]: " + e.toString());
            }
        });

        //Wait for CountDownLatch, or timeout after 10 seconds
        if(blocking){
            try{
                ClientTickLatch.await(10, TimeUnit.SECONDS);
                ServerTickLatch.await(10, TimeUnit.SECONDS);
                conn.send("Latch completed"); //Ack

            } catch (Exception e){
                conn.send("Time out"); //Ack
            }
        } else{
            conn.send("Ack non-blocking command");
        }
    }

    public static void ExecuteCommandAsPlayer(String command){
        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
        if (minecraftServer == null) {
            LOGGER.error("Minecraft server is not available.");
            return;
        }

        ServerPlayerEntity player = DreamVis.GetServerPlayer();
        if (player == null) {
            LOGGER.error("No players are currently online.");
            return;
        }

        minecraftServer.execute(() -> {
            try {
                // Set up a CountDownLatch to wait for the command completion
                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();

                var parsedCommand = dispatcher.parse(command, player.getCommandSource());

                dispatcher.execute(parsedCommand);

                LOGGER.info("Command executed successfully");

            } catch (Exception e) {
                LOGGER.error("Failed to execute command [" + command + "]: " + e.toString());
            }
        });
    }

    public void SendMsgToUnity(String msg){
        if(_myWsClient == null || !_myWsClient.isOpen()){
            LOGGER.info("client isn't open, reinitializing");
            InitWebSocketClient();
        }

        // Run the waiting logic asynchronously
        CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();
            while (!_myWsClient.isOpen()) {
                if (System.currentTimeMillis() - startTime > 3000) {
                    LOGGER.error("Timeout: WebSocket client did not open within 3 seconds.");
                    return;
                }

                try {
                    Thread.sleep(100); // Sleep for a short duration before checking again
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting for WebSocket client to open: " + e.toString());
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            LOGGER.info("sending message to unity server: " + msg);

            try {
                _myWsClient.send(msg);
            } catch (Exception e) {
                LOGGER.error(e.toString());
            }
        });

    }

    public static void ResetLatch(){
        ClientTickLatch = new CountDownLatch(5);
        ServerTickLatch = new CountDownLatch(5);
    }
}
