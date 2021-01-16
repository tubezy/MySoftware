package zonnic.land.zonnicrtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zonnic.land.zonnicrtp.Utils;
import zonnic.land.zonnicrtp.ZonnicRTP;

import java.util.HashMap;
import java.util.UUID;

public class RTPCommand implements CommandExecutor {
    public static HashMap<UUID, Long> rtpcooldown = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ZonnicRTP plugin = ZonnicRTP.getPlugin();
        Utils utils = new Utils();
        int cooldownTime = plugin.getConfig().getInt("cooldown_time");

        if (command.getName().equalsIgnoreCase("rtp")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Utils.teleportiongPlayers.containsKey(p.getUniqueId())) {
                    if (rtpcooldown.containsKey(p.getUniqueId())) {
                        long secondsLeft = ((rtpcooldown.get(p.getUniqueId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                        if (secondsLeft <= 0) {
                            rtpcooldown.remove(p.getUniqueId());
                        }
                    }
                    if (!rtpcooldown.containsKey(p.getUniqueId())) {
                        utils.teleportToRandomLocation(p);
                    } else {
                        long secondsLeft = ((rtpcooldown.get(p.getUniqueId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                        p.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.RED + "You are currently on RTP cool down! Seconds Left: " + secondsLeft + " seconds.");
                    }
                } else {
                    p.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.RED + "You are already teleporting to a random location!");
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            }
        }

        return true;
    }
}
