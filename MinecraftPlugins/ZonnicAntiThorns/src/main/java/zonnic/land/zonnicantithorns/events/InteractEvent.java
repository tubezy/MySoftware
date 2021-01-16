package zonnic.land.zonnicantithorns.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import zonnic.land.zonnicantithorns.Utils;

public class InteractEvent implements Listener {
    Utils utils = new Utils();
    @EventHandler
    public void OnInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            utils.removePossibleThorns(p.getInventory().getItemInMainHand(), p);
            utils.removePossibleThorns(p.getInventory().getItemInOffHand(), p); // why not
        }
    }

}
