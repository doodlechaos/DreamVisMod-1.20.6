package net.doodlechaos.dreamvis.networking;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class MyWebSocketServer extends WebSocketServer {

    private SocketHub _parentHub;

    public MyWebSocketServer(SocketHub hub, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));

        _parentHub = hub;
    }

    public MyWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public MyWebSocketServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to my Minecraft MyWebSocket server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        LOGGER.info(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        LOGGER.info(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        handleOnMessage(conn, message);
///       broadcast(message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        handleOnMessage(conn, message.toString());
    }

    private void handleOnMessage(WebSocket conn, String message){
        //broadcast(message);
        LOGGER.info(conn + ": " + message);
        _parentHub.OnUnityMessageReceived(conn, message);
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        LOGGER.info("My WebSocketServer started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}