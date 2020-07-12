package org.tomkayrp.tkbanenchants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomkayrp.tkbanenchants.commands.banenchantsCommand;
import org.tomkayrp.tkbanenchants.events.OnInventoryClick;

public final class TKBanEnchants extends JavaPlugin {
    FileConfiguration config = this.getConfig();
    public void loadConfig() {
        config.options().copyDefaults(true);
        saveConfig();
    }

    public static TKBanEnchants plugin;

    @Override
    public void onEnable() {
        plugin = this;
        loadConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[ TKBanEnchants ] " + ChatColor.GREEN + "TkBanEnchants v2.3 Started! By Tubez");

        this.getCommand("banenchants").setExecutor(new banenchantsCommand());
        this.getServer().getPluginManager().registerEvents(new OnInventoryClick(), this);
    }
}
