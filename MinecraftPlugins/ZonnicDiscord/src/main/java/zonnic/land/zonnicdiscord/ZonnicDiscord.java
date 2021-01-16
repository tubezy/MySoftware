package zonnic.land.zonnicdiscord;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZonnicDiscord extends JavaPlugin {
    public static ZonnicDiscord plugin;
    FileConfiguration config = getConfig();
    void setupConfig() {
        config.options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        setupConfig();

        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Command());
    }
}
