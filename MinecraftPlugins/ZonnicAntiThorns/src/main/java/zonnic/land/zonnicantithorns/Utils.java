package zonnic.land.zonnicantithorns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public List getArmorList() { // Returns all armor pieces in a list.
        List<Material> itemsToBeChecked = new ArrayList<>();
        itemsToBeChecked.add(Material.NETHERITE_HELMET);
        itemsToBeChecked.add(Material.NETHERITE_CHESTPLATE);
        itemsToBeChecked.add(Material.NETHERITE_LEGGINGS);
        itemsToBeChecked.add(Material.NETHERITE_BOOTS);
        itemsToBeChecked.add(Material.DIAMOND_HELMET);
        itemsToBeChecked.add(Material.DIAMOND_CHESTPLATE);
        itemsToBeChecked.add(Material.DIAMOND_LEGGINGS);
        itemsToBeChecked.add(Material.DIAMOND_BOOTS);
        itemsToBeChecked.add(Material.IRON_HELMET);
        itemsToBeChecked.add(Material.IRON_CHESTPLATE);
        itemsToBeChecked.add(Material.IRON_LEGGINGS);
        itemsToBeChecked.add(Material.IRON_BOOTS);
        itemsToBeChecked.add(Material.GOLDEN_HELMET);
        itemsToBeChecked.add(Material.GOLDEN_CHESTPLATE);
        itemsToBeChecked.add(Material.GOLDEN_LEGGINGS);
        itemsToBeChecked.add(Material.GOLDEN_BOOTS);
        itemsToBeChecked.add(Material.CHAINMAIL_HELMET);
        itemsToBeChecked.add(Material.CHAINMAIL_CHESTPLATE);
        itemsToBeChecked.add(Material.CHAINMAIL_LEGGINGS);
        itemsToBeChecked.add(Material.CHAINMAIL_BOOTS);
        itemsToBeChecked.add(Material.LEATHER_HELMET);
        itemsToBeChecked.add(Material.LEATHER_CHESTPLATE);
        itemsToBeChecked.add(Material.LEATHER_LEGGINGS);
        itemsToBeChecked.add(Material.LEATHER_BOOTS);

        return itemsToBeChecked;
    }

    public ItemStack removePossibleThorns(ItemStack item, Player player) {
        if (getArmorList().contains(item.getType())) { // Checks if what is clicked is an armor piece (or book)
            if (item.getEnchantments().containsKey(Enchantment.THORNS)) {
                item.removeEnchantment(Enchantment.THORNS);
                player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "ZonnicAntiThorns" + ChatColor.WHITE + "]" + ChatColor.DARK_RED + " Detected thorns on armor piece. The enchantment has been destroyed.");
            }
        }
        return item;
    }
}
