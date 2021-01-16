package org.tomkayrp.tkproximitychat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomkayrp.tkproximitychat.commands.PcCommand;
import org.tomkayrp.tkproximitychat.commands.ProxyCommand;
import org.tomkayrp.tkproximitychat.events.ChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.boss.BarColor.BLUE;
import static org.bukkit.boss.BarStyle.SOLID;

public final class TKProximityChat extends JavaPlugin {

    public static TKProximityChat plugin; // Instance

    FileConfiguration config = getConfig(); // config.yml
    private void loadConfig() {
        config.options().copyDefaults(true);
        this.saveConfig();
    }

    public static String starter = ChatColor.DARK_AQUA + "[ TKProximityChat ] ";
    public static String version = "v1.3";

    @Override
    public void onEnable() {
        plugin = this; // Set instance to the plugin
        loadConfig(); // loadConfig method
        Bukkit.getConsoleSender().sendMessage(starter + ChatColor.GOLD + "Started TKProximityChat " + version + "! By TK Dev Team.");

        // Commands
        Objects.requireNonNull(this.getCommand("pc")).setExecutor(new PcCommand());
        Objects.requireNonNull(this.getCommand("proxy")).setExecutor(new ProxyCommand());

        // Events
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);

        // Tasks
        int range = plugin.getConfig().getInt("range") + 1;
        final BossBar bar = Bukkit.createBossBar("Someone is nearby...", BLUE, SOLID);
        List<Player> hasBar = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers())
                if (!player.hasPermission("tkproximitychat.admin")) {
                    Location location = player.getLocation();
                    for (Player other : Bukkit.getOnlinePlayers())
                        if (other.getWorld().getPlayers().contains(player)) {
                            if (other != player && !(other.hasPermission("tkproximitychat.admin")) && other.getLocation().distance(location) < range) {
                                if (!hasBar.contains(player)) {
                                    bar.addPlayer(player);
                                    hasBar.add(player);
                                }
                            } else if (other.getLocation().distance(location) > range) {
                                if (hasBar.contains(player)) {
                                    bar.removePlayer(player);
                                    hasBar.remove(player);
                                }
                            }
                        }
                }
        }, 0L, 100L);
    }
}
