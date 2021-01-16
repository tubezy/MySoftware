package zonnic.land.zonnicrtp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import zonnic.land.zonnicrtp.commands.RTPCommand;
import zonnic.land.zonnicrtp.events.MovementListener;

import java.util.Objects;

public final class ZonnicRTP extends JavaPlugin {

    static ZonnicRTP plugin;
    String pluginVersion = "1.1";
    String name = "ZonnicRTP";
    String pluginName = ChatColor.translateAlternateColorCodes('&', name);

    FileConfiguration config = this.getConfig();
    void setupConfig() {
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        setupConfig();
        // Commands
        Objects.requireNonNull(this.getCommand("rtp")).setExecutor(new RTPCommand());

        // Events
        this.getServer().getPluginManager().registerEvents(new MovementListener(), this);

        System.out.println(ChatColor.AQUA + "||=----------------=||");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + pluginName + ChatColor.WHITE + "] " + ChatColor.GOLD + "Started " + pluginName + " " + pluginVersion + "! (By tubez)");
        System.out.println(ChatColor.AQUA + "||=----------------=||");
    }

    public static ZonnicRTP getPlugin() { return plugin; }
}
