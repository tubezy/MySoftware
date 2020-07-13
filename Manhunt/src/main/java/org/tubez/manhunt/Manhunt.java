package org.tubez.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.tubez.manhunt.commands.ManhuntCommand;
import org.tubez.manhunt.events.BreakBlockListener;
import org.tubez.manhunt.events.InteractListener;
import org.tubez.manhunt.events.PlaceBlockListener;

public final class Manhunt extends JavaPlugin {
    public static Plugin plugin;
    public static String pluginVersion = "v1.0";
    public static String soutIntro = ChatColor.YELLOW + "[ Manhunt " + pluginVersion + " ] ";

    FileConfiguration config = getConfig();
    public void loadConfig() {
        config.options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        loadConfig();
        Bukkit.getConsoleSender().sendMessage(soutIntro + ChatColor.GREEN + "Started Manhunt Plugin! By Tubez");

        this.getCommand("manhunt").setExecutor(new ManhuntCommand());

        this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new BreakBlockListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlaceBlockListener(), this);
    }
}
