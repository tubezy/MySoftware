package org.tomkayrp.tkbanenchants.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.tomkayrp.tkbanenchants.TubezMethods;
import org.tomkayrp.tkbanenchants.commands.banenchantsCommand;

public class OnInventoryClick implements Listener {
    @EventHandler
    public void InventoryClick (InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().hasItemMeta()) {
                if (e.getCurrentItem().getItemMeta().hasEnchants()) {
                    if (banenchantsCommand.enabled) {
                        ItemStack item = e.getCurrentItem();

                        // To make the listener look cleaner lol idfk why but whatever
                        TubezMethods.banEnchants(item, p);
                    }
                }
            }
        }
    }
}
