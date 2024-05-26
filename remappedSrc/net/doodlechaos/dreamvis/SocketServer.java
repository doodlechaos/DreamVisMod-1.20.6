package net.doodlechaos.dreamvis;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

public class SocketServer extends Thread {
    private final int port;
    private final MinecraftServer minecraftServer;

    public SocketServer(int port, MinecraftServer server) {
        this.port = port;
        this.minecraftServer = server;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Socket server started on port " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

                    String command = in.readLine();
                    System.out.println("Received command: " + command);

                    CountDownLatch latch = new CountDownLatch(1);

                    // Execute the command on the Minecraft server
                    minecraftServer.execute(() -> {
                        CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();
                        var parsedCommand = dispatcher.parse(command, minecraftServer.getCommandSource());
                        try {
                            dispatcher.execute(parsedCommand);
                        } catch(CommandSyntaxException e){
                            LOGGER.error(e.toString());
                        }finally {
                            // Count down the latch to indicate that the command execution is complete
                            latch.countDown();
                        }
                    });
                    //TODO: How can I wait until the command is finished executing before sending the websocket response?

                    latch.await();

                    out.println("Command executed: " + command);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
