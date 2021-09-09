package arnaria.notifacaitonmanager;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "Notification Manager")
public class Settings implements ConfigData {
    @Comment("Please choose the file path for you SQLite database")
    public static String SQLITE_DIRECTORY = "C:\\Database";

    public static String DATABASE_NAME = "Notification Manager";
}
