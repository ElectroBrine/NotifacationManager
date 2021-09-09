package arnaria.notifacaitonmanager;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.SqlTypes;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.util.Database;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class NotificationManager implements ModInitializer {
    private static Table playerMessages;
    private static int count = 0;
    private static final HashMap<UUID, PlayerEntity> onlinePlayers = new HashMap<>();
    public static Settings settings;

    @Override
    public void onInitialize() {
        AutoConfig.register(Settings.class, JanksonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        boolean validConfig = !Objects.equals(Settings.SQLITE_DIRECTORY, "/path/to/folder");
        if (validConfig) {
            Database.TYPE = SqlTypes.SQLITE;
            Database.DATABASE_NAME = Settings.DATABASE_NAME;
            Database.SQLITE_DIRECTORY = Settings.SQLITE_DIRECTORY;
            Database.init();
        }
        else System.out.println("Please change the file path in the config");

        playerMessages = new Table("Notifications");
        count = playerMessages.getIds().size();



        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ArrayList<DataContainer> deleteList = new ArrayList<>();
            onlinePlayers.put(handler.getPlayer().getUuid(), handler.getPlayer());

            for(DataContainer Notification : playerMessages.getDataContainers()) {
                if (Notification.getUuid("player").equals(handler.getPlayer().getUuid())) {
                    send(handler.getPlayer().getUuid(), Notification.getMutableText("message"), Notification.getString("type"));
                    deleteList.add(Notification);
                }
            }
            for(DataContainer notification : deleteList) playerMessages.drop(notification);
        });

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            onlinePlayers.remove(handler.getPlayer().getUuid());
        }));
    }

    public static void send(UUID uuid, String message, String type) {
        send(uuid, new LiteralText(message).formatted(), type);
    }

    public static void send(UUID uuid, MutableText message, String type) {
        PlayerEntity player = onlinePlayers.get(uuid);
        if (player != null) {
            switch (type) {
                case "ACHIEVEMENT" -> player.sendMessage(message.formatted(Formatting.AQUA), false);
                case "WARN" -> player.sendMessage(message.formatted(Formatting.GOLD).formatted(Formatting.ITALIC), false);
                case "ERROR" -> player.sendMessage(message.formatted(Formatting.DARK_RED).formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE), false);
                case "ENDER" -> player.sendMessage(message.formatted(Formatting.UNDERLINE).formatted(Formatting.STRIKETHROUGH).formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE).formatted(Formatting.OBFUSCATED), false);
                default -> player.sendMessage(message, false);
            }
        }
        else {
            DataContainer Notification = new DataContainer(String.valueOf(count));
            playerMessages.put(Notification);

            Notification.put("message", message);
            Notification.put("player", uuid);
            Notification.put("type", type);
            count++;
        }
    }
}