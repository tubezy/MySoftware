package org.tomkayrp.tkrpgproximitychat;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Utils {

    public static List<Entity> nearbyPlayers(Player p, int range) {
        List<Entity> near = new ArrayList<>();
        for (Entity e : Bukkit.getOnlinePlayers()) {
            if (e instanceof Player) {
                if (!(e == p)) {
                    if (e.getWorld().equals(p.getWorld())) {
                        if (e.getLocation().distance(p.getLocation()) <= range) {
                            near.add(e);
                        }
                    }
                }
            }
        }
        return near;
    }


    public static void colorConfig(Player p, String path) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKRPGProximityChat.plugin.config.getString(path))));
    }

    public static void chatHandler(Player p, String message) {
        String prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKRPGProximityChat.plugin.getConfig().getString("chat.proximity_chat.prefix")).replace("{player}", p.getDisplayName()));
        String finalMessage = prefix + " " + message;;
        int range = TKRPGProximityChat.plugin.getConfig().getInt("range") + 1;
        if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
            if (Utils.nearbyPlayers(p, range).size() != 0) {

                for (Entity player : Utils.nearbyPlayers(p, range)) {
                    if (TKRPGProximityChat.plugin.getConfig().getBoolean("enable_distance") && !p.hasPermission("tkproximitychat.hidedistance")) {
                        player.sendMessage(finalMessage);
                    } else {
                        player.sendMessage(finalMessage);
                    }
                }
                p.sendMessage(finalMessage);
            } else {
                Bukkit.getConsoleSender().sendMessage(finalMessage);
                p.sendMessage(finalMessage);
                Utils.colorConfig(p, "chat.proximity_chat.no_one_around");
            }
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("tkproximitychat.spy") && (staff != p)) {
                    if (!Utils.nearbyPlayers(staff, range).contains(p)) {
                        String spyPrefix = Objects.requireNonNull(TKRPGProximityChat.plugin.getConfig().getString("chat.proximity_chat.spy_prefix")).replace("{player}", p.getName());
                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', spyPrefix) + " " + message);
                    }
                }
            }
        } else {
            Utils.colorConfig(p, "chat.proximity_chat.gm_spectator");
        }
    }
}
