package org.tubez.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TubezMethods {
    public static void colorConfig (Player player, String path, String oldText, String newText) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', path).replace(oldText, newText));
    }

    public static void colorConfig (Player player, String path) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', path));
    }

    public static HashSet<Material> falseBlocks = new HashSet<>();

    static {
        falseBlocks.add(Material.LAVA);
        falseBlocks.add(Material.WATER);
        falseBlocks.add(Material.MAGMA_BLOCK);
        falseBlocks.add(Material.FIRE);
    }

    public static boolean safeBlock (Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        return !(falseBlocks.contains(below.getType())) || !(block.getType().isSolid()) || !(above.getType().isSolid());
    }
}
