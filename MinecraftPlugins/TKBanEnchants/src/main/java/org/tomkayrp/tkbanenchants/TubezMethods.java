package org.tomkayrp.tkbanenchants;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.tomkayrp.tkbanenchants.commands.banenchantsCommand;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.enchantments.Enchantment.*;

public class TubezMethods {
    public static void banEnchants(ItemStack item, Player p) {
        int levelsReturned = 0;
        int configLevelsReturned = TKBanEnchants.plugin.getConfig().getInt("Chat.Non_Op.Reward_Amount") - 1;
        boolean message = false;
        boolean disabledbypass = p.hasPermission("tkbanenchants.bypassban") && banenchantsCommand.DisabledBypassPlayers.contains(p);
        if (!p.hasPermission("tkbanenchants.bypassban") || disabledbypass) {
            // This is fucking stupid I can't use a retarded switch statement because its a fucking string list in the config
            // these are the four main enchantments banned for the tk server
            if (item != null) {
                List<Enchantment> actualEnchants = new ArrayList<>();
                List<String> configEnchants = TKBanEnchants.plugin.getConfig().getStringList("Banned_Enchants");
                addEnchants(actualEnchants);
                // o well for loop works, but looping through enchantments is stupid lol
                for(int i = 0; i < 37; i++) {
                    if (configEnchants.contains(configEnchants.get(i))) {
                        if (item.getEnchantments().containsKey(actualEnchants.get(i))) {
                            item.removeEnchantment(actualEnchants.get(i));
                            message = true;
                            levelsReturned = levelsReturned + configLevelsReturned;
                        }
                    }
                }

                if (TKBanEnchants.plugin.getConfig().getBoolean("Chat.Non_Op.Enable_Enchant_Block_Message")) {
                    if (message) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Non_Op.Enchant_Block_Message")));
                        p.giveExpLevels(levelsReturned);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Non_Op.Reward_Message").replace("{reward_amount}", String.valueOf(levelsReturned))));
                        if (TKBanEnchants.plugin.getConfig().getBoolean("Chat.Non_Op.Enable_Enchant_Block_Sound_Effect")) {
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 100, -5);
                        }

                        if(p.getGameMode().equals(GameMode.CREATIVE)) {
                            if (TKBanEnchants.plugin.getConfig().getBoolean("Chat.Op.Enable_Creative_Mode_Error_Message")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Creative_Mode_Error_Message")));
                            }
                        }
                    }
                }
            }

        } else {
            if (TKBanEnchants.plugin.getConfig().getBoolean("Chat.Op.Enable_Enchant_Bypass_Block_Message")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Enchant_Bypass_Block_Message")));
                if (TKBanEnchants.plugin.getConfig().getBoolean("Chat.Op.Enable_Enchant_Bypass_Block_Sound_Effect")) {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 5);
                }
            }
        }
    }
    // DUN FKN BULLY ME FOR THIS LMFAOOOOOO
    public static void addEnchants(List<Enchantment> e) {
        e.add(FROST_WALKER); e.add(LOOT_BONUS_BLOCKS); e.add(CHANNELING); e.add(RIPTIDE); e.add(PROTECTION_ENVIRONMENTAL); e.add(PROTECTION_FALL);
        e.add(PROTECTION_FIRE); e.add(PROTECTION_EXPLOSIONS); e.add(PROTECTION_PROJECTILE); e.add(OXYGEN); e.add(WATER_WORKER); e.add(THORNS); e.add(DEPTH_STRIDER);
        e.add(BINDING_CURSE); e.add(DAMAGE_ALL); e.add(DAMAGE_UNDEAD); e.add(DAMAGE_ARTHROPODS); e.add(KNOCKBACK); e.add(FIRE_ASPECT); e.add(LOOT_BONUS_MOBS);
        e.add(SWEEPING_EDGE); e.add(DIG_SPEED); e.add(SILK_TOUCH); e.add(DURABILITY); e.add(ARROW_DAMAGE); e.add(ARROW_KNOCKBACK); e.add(ARROW_FIRE);
        e.add(ARROW_INFINITE); e.add(LUCK); e.add(LURE); e.add(LOYALTY); e.add(IMPALING); e.add(MULTISHOT); e.add(QUICK_CHARGE);
        e.add(PIERCING); e.add(MENDING); e.add(VANISHING_CURSE);
    }

}
