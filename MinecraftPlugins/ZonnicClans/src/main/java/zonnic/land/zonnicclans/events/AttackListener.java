package zonnic.land.zonnicclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.clancommands.ClanCommand;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;

public class AttackListener implements Listener {
    SQLGetters sqlget = new SQLGetters();
    ZonnicClans plugin = ZonnicClans.getPlugin();
    @EventHandler
    public void PlayerAttack (EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                String playerClan = sqlget.getClan(player);
                String damagerClan = sqlget.getClan(damager);
                if (playerClan.equals(damagerClan)) {
                    if (!ClanCommand.toggledClanPvp.contains(playerClan)) {
                        e.setCancelled(true);
                        damager.sendMessage(plugin.getPrefixInitials() + plugin.error + "You cannot hurt your clan member.");
                    }
                }
            }
        }
    }
}
