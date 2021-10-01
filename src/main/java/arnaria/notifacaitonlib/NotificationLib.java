package arnaria.notifacaitonlib;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class NotificationLib implements ModInitializer {
    private static Table playerMessages;
    private static final HashMap<UUID, PlayerEntity> onlinePlayers = new HashMap<>();
    public static Settings settings;

    @Override
    public void onInitialize() {
        AutoConfig.register(Settings.class, JanksonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();



        boolean validConfig = !Objects.equals(settings.SQLITE_DIRECTORY, "/path/to/folder");
        if (validConfig) {
            log(Level.INFO, "Notifying our Managers");
            SQLiteDatabase database = new SQLiteDatabase(settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);
            database.createTable("Notifications");

            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                PlayerEntity player = handler.getPlayer();
                onlinePlayers.put(player.getUuid(), player);
                if (playerMessages.contains(player.getUuidAsString())) {
                    DataContainer PlayerData = playerMessages.get(player.getUuidAsString());
                    NbtList Notifications = (NbtList) PlayerData.getNbt("notifications");
                    while (Notifications.iterator().hasNext()) {
                        NbtCompound Notification = (NbtCompound) Notifications.iterator().next();
                        MutableText Message = (MutableText) Notification.get("message");
                        NotificationManager.send(player.getUuid(), Message, Notification.getString("type"));
                    }
                    playerMessages.createDataContainer(player.getUuidAsString());

                } else {
                    playerMessages.createDataContainer(player.getUuidAsString());
                }
            });

            ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
                PlayerEntity player = handler.getPlayer();
                onlinePlayers.remove(player.getUuid());
            }));
        }
        else log(Level.INFO, "Please put in a valid file path");
    }
    private static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[Notification Manager] " + message);
    }
}