package zonnic.land.zonnicantithorns.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import zonnic.land.zonnicantithorns.Utils;

public class ItemPickupListener implements Listener {
    Utils utils = new Utils();
    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        ItemStack itemPickedUp = e.getItem().getItemStack();
        utils.removePossibleThorns(itemPickedUp, p);
    }

}
