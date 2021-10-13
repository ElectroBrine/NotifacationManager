package arnaria.notifacaitonlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class NotificationLib implements ModInitializer {
    public static final HashMap<UUID, PlayerEntity> onlinePlayers = new HashMap<>();
    public static Settings settings;
    public static Table playerMessages;

    @Override
    public void onInitialize() {
        AutoConfig.register(Settings.class, JanksonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        boolean validConfig = !Objects.equals(settings.SQLITE_DIRECTORY, "/path/to/folder");
        if (validConfig) {
            log("Notifying our Managers");
            SQLiteDatabase database = new SQLiteDatabase(settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);
            ServerLifecycleEvents.SERVER_STARTED.register(server -> playerMessages = database.createTable("Notifications"));


            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                PlayerEntity player = handler.getPlayer();
                onlinePlayers.put(player.getUuid(), player);
                if (playerMessages.contains(player.getUuid())) {
                    JsonArray Notifications = NotificationManager.getNotifications(player.getUuid());
                    if (Notifications != null) {
                        for (JsonElement json : Notifications) {
                            JsonObject Notification = json.getAsJsonObject();
                            JsonElement message = new JsonParser().parse(String.valueOf(Notification.get("message")));
                            NotificationManager.send(player.getUuid(), message.getAsString(), Notification.get("type").getAsString());
                        }
                    }
                } else {
                    DataContainer dataContainer = playerMessages.createDataContainer(player.getUuid());
                    dataContainer.put("Notifications", new JsonArray());
                }
            });

            ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
                PlayerEntity player = handler.getPlayer();
                onlinePlayers.remove(player.getUuid());
                NotificationManager.send(player.getUuid(), "This is a test message", NotificationTypes.INFO);
            }));
        }
        else log("Please put in a valid file path");
    }
    private static void log(String message) {
        LogManager.getLogger().log(Level.INFO, "[Notification Manager] " + message);
    }
}