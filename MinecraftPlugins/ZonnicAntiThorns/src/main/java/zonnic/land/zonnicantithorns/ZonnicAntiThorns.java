package zonnic.land.zonnicantithorns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import zonnic.land.zonnicantithorns.events.InteractEvent;
import zonnic.land.zonnicantithorns.events.InventoryClickListener;
import zonnic.land.zonnicantithorns.events.ItemPickupListener;

public final class ZonnicAntiThorns extends JavaPlugin {

    static ZonnicAntiThorns plugin;
    Utils utils = new Utils();
    FileConfiguration config = getConfig();
    private void setupConfig() {
        config.options().copyDefaults(true);
        saveConfig();
    }

    String pluginVersion = "1.0";
    String pluginName = "ZonnicAntiThorns";

    @Override
    public void onEnable() {
        plugin = this;
        setupConfig();
        // Commands
        // Events
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemPickupListener(), this);
        this.getServer().getPluginManager().registerEvents(new InteractEvent(), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + pluginName + ChatColor.WHITE + "] " + ChatColor.RED + "ZonnicAntiThorns v" + pluginVersion + " is ready to destroy thorns. (by tubez)");

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getInventory().getHelmet() != null) p.getInventory().setHelmet(utils.removePossibleThorns(p.getInventory().getHelmet(), p));
                if (p.getInventory().getChestplate() != null) p.getInventory().setChestplate(utils.removePossibleThorns(p.getInventory().getChestplate(), p));
                if (p.getInventory().getLeggings() != null) p.getInventory().setLeggings(utils.removePossibleThorns(p.getInventory().getLeggings(), p));
                if (p.getInventory().getBoots() != null) p.getInventory().setBoots(utils.removePossibleThorns(p.getInventory().getBoots(), p));
            }
        }, 0L, (config.getInt("time_between_checks") * 20));
    }
}
