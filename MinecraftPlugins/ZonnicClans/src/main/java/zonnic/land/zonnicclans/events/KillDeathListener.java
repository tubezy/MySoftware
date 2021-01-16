package zonnic.land.zonnicclans.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KillDeathListener implements Listener {
    SQLGetters sqlget = new SQLGetters();
    ZonnicClans plugin = ZonnicClans.getPlugin();
    Utils utils = new Utils();
    ThreadLocalRandom random = ThreadLocalRandom.current();
    public static HashMap<String, Integer> kills = new HashMap<>();
    public static HashMap<String, Integer> deaths = new HashMap<>();
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
        Player dead = e.getEntity().getPlayer();
        Player killer = e.getEntity().getKiller();
        if (dead != null) {
            if (sqlget.playerInClan(dead)) {
                String deadClan = sqlget.getClan(dead);
                if (!deaths.containsKey(deadClan)) {
                    deaths.put(deadClan, 1);
                } else {
                    deaths.put(deadClan, deaths.get(deadClan) + 1);
                }
            }
        }

        if (killer != null) {
            if (sqlget.playerInClan(killer)) {
                String killerClan = sqlget.getClan(killer);
                if (!kills.containsKey(killerClan)) {
                    kills.put(killerClan, 1);
                } else {
                    kills.put(killerClan, kills.get(killerClan) + 1);
                }
            }
            int randomNumber = random.nextInt(0, 100);
            int percentage = plugin.getConfig().getInt("clans.head-drop-percentage");
            if (percentage > 100) percentage = 100;
            if (percentage >= randomNumber)
                e.getDrops().add(utils.getPlayerHead(dead));

            String displayName;
            if (!killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("")) {
                displayName = killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            } else {
                displayName = utils.toTitleCase(killer.getInventory().getItemInMainHand().getType().toString().replace("_", " ").toLowerCase());
            }

            List<String> deathMessagesByPlayer = plugin.getConfig().getStringList("clans.death-messages-by-player");
            e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessagesByPlayer.get(random.nextInt(0, deathMessagesByPlayer.size()))
                    .replace("%player%", dead.getName())
                    .replace("%killer%", killer.getName())
                    .replace("%weapon%", displayName)));
        } else {
            List<String> deathMessagesBySelf = plugin.getConfig().getStringList("clans.death-messages-by-self");
            e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessagesBySelf.get(random.nextInt(0, deathMessagesBySelf.size()))
                    .replace("%player%", dead.getName())));
        }
    }
}
