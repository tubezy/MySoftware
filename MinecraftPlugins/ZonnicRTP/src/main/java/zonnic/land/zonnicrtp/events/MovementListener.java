package zonnic.land.zonnicrtp.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zonnic.land.zonnicrtp.Utils;

public class MovementListener implements Listener {

    Utils utils = new Utils();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e) {
        int getFromX = e.getFrom().getBlockX(); int getFromZ = e.getFrom().getBlockZ();
        int getToX = e.getTo().getBlockX(); int getToZ = e.getTo().getBlockZ();

        if (utils.teleportiongPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if ((getFromX != getToX) || (getFromZ != getToZ)) {
                Bukkit.getScheduler().cancelTask(utils.teleportiongPlayers.get(e.getPlayer().getUniqueId()));
                utils.teleportiongPlayers.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.RED + "Teleportation has been cancelled due to movement.");
            }
        }
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent e) {
        if (utils.teleportiongPlayers.containsKey(e.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().cancelTask(utils.teleportiongPlayers.get(e.getPlayer().getUniqueId()));
            utils.teleportiongPlayers.remove(e.getPlayer().getUniqueId());
        }
    }

}
