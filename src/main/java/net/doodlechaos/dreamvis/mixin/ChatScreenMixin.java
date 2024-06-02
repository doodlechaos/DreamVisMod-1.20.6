package net.doodlechaos.dreamvis.mixin;

import net.doodlechaos.dreamvis.DreamVis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.MessageFormat;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage(Ljava/lang/String;Z)V", at = @At("TAIL"))
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        System.out.println("Chat message: " + chatText);

        MinecraftClient client = MinecraftClient.getInstance();

        boolean floorRelativeCoordsToInt = chatText.startsWith("/setblock");

        // Convert tilde relative coordinates to world coordinates
        String processedMsg = convertTildeCoordinates(chatText, floorRelativeCoordsToInt, client);

        // Handle WorldEdit coordinates
        processedMsg = handleWorldEditCommands(processedMsg, client);

        DreamVis.OnPlayerSendChatMessage(processedMsg);
    }


    private static String convertTildeCoordinates(String msg, boolean roundRelativeCoordsToInt, MinecraftClient client) {
        if (client.player == null) return msg;

        double baseX = client.player.getX();
        double baseY = client.player.getY();
        double baseZ = client.player.getZ();

        String[] parts = msg.split(" ");
        int coordinate = 0;
        for (int i = 0; i < parts.length; i++) {

            if (parts[i].startsWith("~")) {
                // Determine if the coordinate is X, Y, or Z by context
                double baseCoordinate = 0;
                switch (coordinate) {
                    case 0: // X coordinate
                        baseCoordinate = baseX;
                        break;
                    case 1: // Y coordinate
                        baseCoordinate = baseY;
                        break;
                    case 2: // Z coordinate
                        baseCoordinate = baseZ;
                        break;
                        //TODO: Also do pitch and yaw?
                }

                parts[i] = roundRelativeCoordsToInt ? String.valueOf(parseCoordinateToInt(baseCoordinate, parts[i])) : String.valueOf(parseCoordinateToDouble(baseCoordinate, parts[i]));
                coordinate++;
            }
        }
        return String.join(" ", parts);
    }

    private static double parseCoordinateToDouble(double base, String input) {
        if (input.startsWith("~")) {
            if (input.length() == 1) {
                return base;
            } else {
                return base + Double.parseDouble(input.substring(1));
            }
        } else {
            return Double.parseDouble(input);
        }
    }

    private static int parseCoordinateToInt(double base, String input) {
        if (input.startsWith("~")) {
            if (input.length() == 1) {
                return (int) Math.floor(base);
            } else {
                return (int) Math.floor(base + Double.parseDouble(input.substring(1)));
            }
        } else {
            return (int) Math.floor(Double.parseDouble(input));
        }
    }

    private static String handleWorldEditCommands(String input, MinecraftClient client){
        double baseX = client.player.getX();
        double baseY = client.player.getY();
        double baseZ = client.player.getZ();

        if(input.trim().equals("//pos1")){
            return MessageFormat.format("//pos1 {0},{1},{2}", (int)Math.floor(baseX), (int)Math.floor(baseY), (int)Math.floor(baseZ));
        }
        if(input.trim().equals("//pos2")){
            return MessageFormat.format("//pos2 {0},{1},{2}", (int)Math.floor(baseX), (int)Math.floor(baseY), (int)Math.floor(baseZ));
        }
        return input;
    }
}
