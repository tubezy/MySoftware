package org.tomkayrp.tkproximitychat;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.tomkayrp.tkproximitychat.commands.ProxyCommand;

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

    public static int playerDistance(Player p, Player p2) {
        Location playerLocation = p.getLocation();
        return (int) playerLocation.distance(p2.getLocation());
    }

    public static void colorConfig(Player p, String path) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKProximityChat.plugin.config.getString(path))));
    }

    public static void chatHandler(Player p, String message) {
        String finalMessage;
        int range = TKProximityChat.plugin.getConfig().getInt("range") + 1;
        String prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKProximityChat.plugin.getConfig().getString("chat.proximity_chat.prefix")).replace("{player}", p.getDisplayName()));
        if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
            if (Utils.nearbyPlayers(p, range).size() != 0) {
                for (Entity player : Utils.nearbyPlayers(p, range)) {
                    int distance = Utils.playerDistance(p, (Player) player);
                    String playerName = p.getDisplayName();
                    String distancePrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKProximityChat.plugin.getConfig().getString("chat.proximity_chat.distance_prefix")).replaceAll("\\{distance}", String.valueOf(distance)).replaceAll("\\{player}", playerName));
                    if (TKProximityChat.plugin.getConfig().getBoolean("enable_distance") && !p.hasPermission("tkproximitychat.hidedistance")) {
                        finalMessage = distancePrefix + " " + message;
                        player.sendMessage(finalMessage);
                    } else {
                        finalMessage = prefix + " " + message;
                        player.sendMessage(finalMessage);
                    }
                }
                finalMessage = prefix + " " + message;
                p.sendMessage(finalMessage);
            } else {
                finalMessage = prefix + " " + message;
                Bukkit.getConsoleSender().sendMessage(finalMessage);
                p.sendMessage(finalMessage);
                Utils.colorConfig(p, "chat.proximity_chat.no_one_around");
            }
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("tkproximitychat.spy")) {
                    if (ProxyCommand.spyOn.contains(staff.getUniqueId().toString())) {
                        String spyPrefix = Objects.requireNonNull(TKProximityChat.plugin.getConfig().getString("chat.proximity_chat.spy_prefix")).replace("{player}", p.getName());
                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', spyPrefix) + " " + message);
                    }
                }
            }
        } else {
            Utils.colorConfig(p, "chat.proximity_chat.gm_spectator");
        }
    }
}
