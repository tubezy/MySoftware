package org.tomkayrp.tkrpgproximitychat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomkayrp.tkrpgproximitychat.commands.ProxyCommand;
import org.tomkayrp.tkrpgproximitychat.events.ChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.boss.BarColor.BLUE;
import static org.bukkit.boss.BarStyle.SOLID;

public final class TKRPGProximityChat extends JavaPlugin {

    public static TKRPGProximityChat plugin; // Instance

    FileConfiguration config = getConfig(); // config.yml
    private void loadConfig() {
        config.options().copyDefaults(true);
        this.saveConfig();
    }

    public static String starter = ChatColor.DARK_AQUA + "[ TKRPGProximityChat ] ";
    public static String version = "v1.3";

    @Override
    public void onEnable() {
        plugin = this; // Set instance to the plugin
        loadConfig(); // loadConfig method
        Bukkit.getConsoleSender().sendMessage(starter + ChatColor.GOLD + "Started TKRPGProximityChat " + version + "! By TK Dev Team.");

        // Commands
        Objects.requireNonNull(this.getCommand("proxy")).setExecutor(new ProxyCommand());

        // Events
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }
}
