package arnaria.notifacaitonmanager;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "Notification_Manager")
public class Settings implements ConfigData {
    @Comment("Please choose the file path for you SQLite database")
    public String SQLITE_DIRECTORY = "/path/to/folder";

    public String DATABASE_NAME = "Notification Manager";
}
