package net.doodlechaos.dreamvis.networking;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.doodlechaos.dreamvis.DreamVis.*;

public class SocketHub {

    private static MyWebSocketClient _myWsClient;
    private static MyWebSocketServer _myWsServer;

    public CountDownLatch CountDownLatch;

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
        if(CurrCamMode == CamMode.MCRegular && msg.startsWith("ctp")){
            conn.send("Camera mode in minecraft isn't set to unity keyframes. Ignoring Messaget");
            return;
        }
        String command = msg;
        if(command.startsWith("/")) //The execute input doesn't take in a slash at the start of the command
            command = command.substring(1);
        else
            return;

        ExecuteCommandAsPlayer(conn, command, blocking);
    }


    //TODO: move this to somewhere else?
    private void ExecuteCommandAsPlayer(WebSocket conn, String command, boolean blocking){
        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
        List<ServerPlayerEntity> playerList = minecraftServer.getPlayerManager().getPlayerList();
        if (playerList.isEmpty()){
            LOGGER.error("Player list is empty");
            conn.send("Player list is empty"); //Ack
            return;
        }

        ServerPlayerEntity player = minecraftServer.getPlayerManager().getPlayerList().get(0);

        if (minecraftServer == null) {
            LOGGER.error("Minecraft server is not available.");
            conn.send("Minecraft server is not available."); //Ack
            return;
        }
        CountDownLatch = new CountDownLatch(5);

        minecraftServer.execute(() -> {
            try {
                // Get the nearest player to the server's default position
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

        //TODO: Wait for CountDownLatch, or timeout after 10 seconds
        if(blocking){
            try{
                boolean completed = CountDownLatch.await(10, TimeUnit.SECONDS);
                conn.send("Latch completed"); //Ack

            } catch (Exception e){
                conn.send("Time out"); //Ack
            }
        } else{
            conn.send("Ack non-blocking command");
        }


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
