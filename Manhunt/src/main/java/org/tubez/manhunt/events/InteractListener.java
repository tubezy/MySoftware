package org.tubez.manhunt.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.tubez.manhunt.Manhunt;
import org.tubez.manhunt.TubezMethods;
import org.tubez.manhunt.commands.ManhuntCommand;

public class InteractListener implements Listener {
    @EventHandler
    public static void onCompassInteract (PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Player target = ManhuntCommand.speedrunner;
        if (ManhuntCommand.started) {
            if (ManhuntCommand.hunters.contains(p)) {
                if (p.getItemInHand().getType() == Material.COMPASS && p.getItemInHand().getType() != Material.AIR) {
                    p.setCompassTarget(target.getLocation());
                    TubezMethods.colorConfig(p, "Events.Compass_Right_Click", "{target}", target.getName());
                }
            }
        } else {
            e.setCancelled(true);
            TubezMethods.colorConfig(p, "Events.Game_Not_Started");
        }
    }
}
