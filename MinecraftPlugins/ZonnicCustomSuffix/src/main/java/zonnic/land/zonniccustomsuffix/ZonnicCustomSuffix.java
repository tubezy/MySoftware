package zonnic.land.zonniccustomsuffix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZonnicCustomSuffix extends JavaPlugin {

    public static ZonnicCustomSuffix plugin;
    FileConfiguration config = getConfig();

    public String pluginPrefix = ChatColor.DARK_PURPLE + "[" + ChatColor.WHITE + "ZonnicCS" + ChatColor.DARK_PURPLE + "] ";

    public ChatColor error = ChatColor.RED;

    @Override
    public void onEnable() {
        plugin = this;
        setupConfig();

        // Commands
        Objects.requireNonNull(this.getCommand("suffix")).setExecutor(new SuffixCommand());

        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.GREEN + "ZonnicCustomSuffix v1.0 has started giving cool suffixes! by tubez");
    }

    void setupConfig() {
        config.options().copyDefaults(true);
        saveDefaultConfig();
    }

    public static ZonnicCustomSuffix getPlugin() {
        return plugin;
    }
}
