package net.doodlechaos.dreamvis.networking;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;

import java.net.URI;
import java.net.URISyntaxException;
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
        try {
            String URI_String = "ws://localhost:" + SOCKET_CLIENT_PORT + "/" + UNITY_ROUTE_NAME;
            URI uri = new URI(URI_String);
            LOGGER.info("Before attempting to connect client on URI: " + uri.toString());

            _myWsClient = new MyWebSocketClient(uri);

            TimeUnit.SECONDS.sleep(1);

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
    }

    public void OnUnityMessageReceived(String msg){
        LOGGER.info("RECEIVED MESSAGE FROM UNITY: " + msg);

        final String finalCommand = msg;

        IntegratedServer minecraftServer = MinecraftClient.getInstance().getServer();
        // Execute the command on the Minecraft server
        minecraftServer.execute(() -> {
            try {
                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();
                var parsedCommand = dispatcher.parse(msg, minecraftServer.getCommandSource());
                dispatcher.execute(parsedCommand);
            } catch (CommandSyntaxException e) {
                LOGGER.error("Failed to execute command [" + finalCommand + "]" + e.toString());
            } finally {
                // Count down the latch to indicate that the command execution is complete
                //latch.countDown();
                LOGGER.info("Latch counted down for command: " + finalCommand);
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
            // Adding a delay to ensure server is ready
            TimeUnit.SECONDS.sleep(1);
            //TODO: Do I have to wait for the connection to finish before calling send(msg)?
            _myWsClient.send(msg);
        }catch (Exception e){
            LOGGER.error(e.toString());
        }

    }
}
