package net.doodlechaos.dreamvis;

import com.mojang.brigadier.CommandDispatcher;
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

                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println("Received command: " + command);

                        if (command == null) {
                            out.println("Command is null. Skipping.");
                            continue;
                        }

                        // Create a final variable for use in the lambda expression
                        final String finalCommand = command;
                        CountDownLatch latch = new CountDownLatch(1);

                        // Execute the command on the Minecraft server
                        minecraftServer.execute(() -> {
                            try {
                                CommandDispatcher<ServerCommandSource> dispatcher = minecraftServer.getCommandManager().getDispatcher();
                                var parsedCommand = dispatcher.parse(finalCommand, minecraftServer.getCommandSource());
                                dispatcher.execute(parsedCommand);
                            } catch (CommandSyntaxException e) {
                                LOGGER.error("Failed to execute command [" + finalCommand + "]" + e.toString());
                            } finally {
                                // Count down the latch to indicate that the command execution is complete
                                latch.countDown();
                                LOGGER.info("Latch counted down for command: " + finalCommand);
                            }
                        });

                        latch.await();

                        out.println("Command executed: " + finalCommand);
                    }

                } catch (Exception e) {
                    LOGGER.error("Failed while reading the incoming command and trying to execute it: " + e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to start socket server: " + e.toString());
            e.printStackTrace();
        }
    }
}