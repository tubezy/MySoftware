package zonnic.land.zonnicantithorns.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import zonnic.land.zonnicantithorns.Utils;

import java.util.List;
import java.util.Objects;

public class InventoryClickListener implements Listener {

    Utils utils = new Utils();

    @EventHandler
    public void InventoryClick(InventoryClickEvent e) {
        List itemsToBeChecked = utils.getArmorList();
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().hasItemMeta()) {
                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasEnchants()) {
                    if (itemsToBeChecked.contains(e.getCurrentItem().getType())) {
                        if (e.getCurrentItem().containsEnchantment(Enchantment.THORNS)) {
                            e.getCurrentItem().removeEnchantment(Enchantment.THORNS);
                            Player p = (Player) e.getWhoClicked();
                            p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "ZonnicAntiThorns" + ChatColor.WHITE + "]" + ChatColor.DARK_RED + " Detected thorns on armor piece. The enchantment has been destroyed.");
                        }
                    }
                }
            }
        }
    }
}
