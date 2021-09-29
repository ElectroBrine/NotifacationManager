package arnaria.notifacaitonlib;

import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.UUID;

public class NotificationManager {
    private static Table playerMessages;
    private static final HashMap<UUID, PlayerEntity> onlinePlayers = new HashMap<>();

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
            DataContainer PlayerData = playerMessages.get(uuid.toString());

            NbtList Notifications = (NbtList) PlayerData.getNbt("notifications");
            NbtCompound Notification = new NbtCompound();
            Notification.put("message", (NbtElement) message);
            Notification.putString("type", type);
            Notifications.add(Notification);

            PlayerData.put("Notifications", Notifications);
        }
    }

    public static NbtList getNotifications(UUID uuid) {
        DataContainer PlayerData = playerMessages.get(uuid.toString());
        NbtList Notifications = (NbtList) PlayerData.getNbt("notifications");
        return Notifications;

    }
}
