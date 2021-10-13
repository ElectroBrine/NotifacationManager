package arnaria.notifacaitonlib;

import com.google.gson.JsonArray;
import mrnavastar.sqlib.api.DataContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import net.minecraft.util.Formatting;

import java.util.UUID;

import static arnaria.notifacaitonlib.NotificationLib.onlinePlayers;
import static arnaria.notifacaitonlib.NotificationLib.playerMessages;

public class NotificationManager {

    public static void send(UUID uuid, String message, String type) {
        send(uuid, new LiteralText(message).formatted(), type);
    }

    public static void send(UUID uuid, MutableText message, String type) {
        PlayerEntity player = onlinePlayers.get(uuid);
        if (player != null) {
            switch (type) {
                case "ACHIEVEMENT" -> player.sendMessage(message.formatted(Formatting.AQUA), false);
                case "INFO" -> player.sendMessage(message.formatted(Formatting.ITALIC).formatted(Formatting.GREEN), false);
                case "EVENT" -> player.sendMessage(message.formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE), false);
                case "WARN" -> player.sendMessage(message.formatted(Formatting.GOLD).formatted(Formatting.ITALIC), false);
                case "ERROR" -> player.sendMessage(message.formatted(Formatting.DARK_RED).formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE), false);
                case "ENDER" -> player.sendMessage(message.formatted(Formatting.UNDERLINE).formatted(Formatting.STRIKETHROUGH).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE).formatted(Formatting.OBFUSCATED), false);
                default -> player.sendMessage(message, false);
            }
        }
        else {

            DataContainer PlayerData = playerMessages.get(uuid);
            JsonArray Notifications = (JsonArray) PlayerData.getJson("Notifications");
            JsonArray Notification = new JsonArray();
            Notification.add(message.toString());
            Notification.add(type);
            Notifications.add(Notification);


            PlayerData.put("Notifications", Notifications);
        }
    }
                /*
            NbtList Notifications = (NbtList) PlayerData.getNbt("Notifications");
            System.out.println(Notifications + " send");
            if (Notifications.isEmpty()) System.out.println("BAD!");
            NbtCompound Notification = new NbtCompound();
            NbtCompound nbtMessage = (NbtCompound) JsonToNBT
            Notification.put("message", message.toString());
            Notification.putString("type", type);
            Notifications.add(Notification);

             */

    public static JsonArray getNotifications(UUID uuid) {
        DataContainer PlayerData = playerMessages.get(uuid);
        return (JsonArray) PlayerData.getJson("Notifications");
    }
}
