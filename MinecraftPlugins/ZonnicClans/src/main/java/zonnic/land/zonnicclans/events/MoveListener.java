package zonnic.land.zonnicclans.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zonnic.land.zonnicclans.clancommands.ClanCommand;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.ZonnicClans;

import java.util.Objects;

public class MoveListener implements Listener {
    Utils utils = new Utils();
    ZonnicClans plugin = ZonnicClans.getPlugin();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e) {
        if (Utils.teleportingPlayers.containsKey(e.getPlayer().getUniqueId())) {
            int getFromX = e.getFrom().getBlockX(); int getFromZ = e.getFrom().getBlockZ();
            int getToX = Objects.requireNonNull(e.getTo()).getBlockX(); int getToZ = e.getTo().getBlockZ();
            if ((getFromX != getToX) || (getFromZ != getToZ)) {
                Bukkit.getScheduler().cancelTask(Utils.teleportingPlayers.get(e.getPlayer().getUniqueId()));
                Utils.teleportingPlayers.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.RED + "Teleportation has been cancelled due to movement.");
            }
        }
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent e) {
        if (ClanCommand.pendingInvitation.containsKey(e.getPlayer())) {
            utils.messageToClan(ClanCommand.pendingInvitation.get(e.getPlayer()), plugin.getPrefixInitials() + ChatColor.RED + e.getPlayer().getName() + "'s invitation was cancelled due to them disconnecting.");
            ClanCommand.pendingInvitation.remove(e.getPlayer());
        }
        if (Utils.teleportingPlayers.containsKey(e.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().cancelTask(Utils.teleportingPlayers.get(e.getPlayer().getUniqueId()));
            Utils.teleportingPlayers.remove(e.getPlayer().getUniqueId());
        }
    }
}
