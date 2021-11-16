package arnaria.notifacaitonlib;

import mrnavastar.sqlib.api.DataContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.UUID;

import static arnaria.notifacaitonlib.NotificationLib.onlinePlayers;
import static arnaria.notifacaitonlib.NotificationLib.playerMessages;

public class NotificationManager {

    public static void send(UUID uuid, String message, String type) {
        send(uuid, new LiteralText(message).formatted(), type);
    }

    public static void send(UUID uuid, MutableText message, String type) {
        PlayerEntity player = onlinePlayers.get(uuid);
        MutableText Notification;
                switch (type) {
            case "ACHIEVEMENT" -> Notification = message.formatted(Formatting.AQUA);
            case "INFO" -> Notification = message.formatted(Formatting.ITALIC).formatted(Formatting.GREEN);
            case "EVENT" -> Notification = message.formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE);
            case "WARN" -> Notification = message.formatted(Formatting.GOLD).formatted(Formatting.ITALIC);
            case "ERROR" -> Notification = message.formatted(Formatting.DARK_RED).formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE);
            case "ENDER" -> Notification = message.formatted(Formatting.UNDERLINE).formatted(Formatting.STRIKETHROUGH).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE).formatted(Formatting.OBFUSCATED);
            default -> Notification = message;
        }
        if (player != null) {
                player.sendMessage(Notification, false);
        }
        else {
            DataContainer PlayerData = playerMessages.get(uuid);
            int NotificationCount = PlayerData.getInt("NotificationCount") + 1;
            PlayerData.put(String.valueOf(NotificationCount), Notification);
            PlayerData.put("NotificationCount", NotificationCount);
        }
    }


    public static ArrayList<MutableText> getNotifications(UUID uuid) {
        DataContainer PlayerData = playerMessages.get(uuid);
        int NotificationCount = PlayerData.getInt("NotificationCount");
        ArrayList<MutableText> Notifications = new ArrayList<>();
        for (int i = 0; i < NotificationCount; i++) {
            Notifications.add(PlayerData.getMutableText(String.valueOf(i)));
        }
        PlayerData.put("NotificationCount", 0);
        return Notifications;
    }
}
