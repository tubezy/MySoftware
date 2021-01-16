package zonnic.land.zonnicclans.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.clancommands.ClanCommand;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {
    
    Utils utils = new Utils();
    SQLGetters sqlget = new SQLGetters();
    ZonnicClans plugin = ZonnicClans.getPlugin();
    public static HashMap<Player, String> playerSuffixes = new HashMap<>();
    HashMap<UUID, Long> chatCooldowns = new HashMap<>();
    int chatCooldownTime = plugin.getConfig().getInt("chat.cooldown");

    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        String message = utils.removeBannedWords(e.getMessage());
        String bannedWordsCheck = ChatColor.stripColor(message);
        if (chatCooldowns.containsKey(p.getUniqueId())) {
            long secondsLeft = ((chatCooldowns.get(p.getUniqueId()) / 1000) + chatCooldownTime) - (System.currentTimeMillis() / 1000);
            if (secondsLeft <= 0) {
                chatCooldowns.remove(p.getUniqueId());
            }
        }

        if (!chatCooldowns.containsKey(p.getUniqueId())) {
            if (ClanCommand.toggledChat.contains(p.getUniqueId())) {
                if (!utils.hasBannedWords(bannedWordsCheck)) {
                    String clan = sqlget.getClan(p);
                    utils.messageToClan(clan, ChatColor.YELLOW + "<< Clan Chat >> " + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + ChatColor.LIGHT_PURPLE + "Clan = " + clan + ChatColor.GREEN + " | Clan Chat: " + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                } else {
                    p.sendMessage(ChatColor.RED + "Bypassing banned words is not allowed");
                }
                for (Player staff : ClanCommand.socialSpyToggled) {
                    if ((staff != p) || !(sqlget.getClan(p).equalsIgnoreCase(sqlget.getClan(staff))))
                        staff.sendMessage(ChatColor.RED + "< Clan Chat Spy > " + ChatColor.YELLOW + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                }
            } else {
                if (!utils.hasBannedWords(bannedWordsCheck)) {
                    String finalMessage = "";
                    String suffix = ChatColor.translateAlternateColorCodes('&', " " + sqlget.getPlayerSuffix(p));

                    // Add the clan tag
                    if (sqlget.playerInClan(p)) {
                        String clan = ChatColor.GRAY + "[" + ChatColor.GOLD + sqlget.getClan(p) + ChatColor.GRAY + "] ";
                        clan = clan.toUpperCase();
                        finalMessage = finalMessage + clan;
                    }

                    // Add the player username
                    finalMessage = finalMessage + p.getDisplayName();

                    // Add the player suffix
                    String lengthCheck = ChatColor.stripColor(suffix);
                    int suffixLength = lengthCheck.length() - 4;
                    if (suffixLength <= plugin.getConfig().getInt("suffix.character-max")) {
                        finalMessage = finalMessage + suffix;
                    }

                    // Add the message separator
                    finalMessage = finalMessage + " >> ";

                    // Add the message
                    if (p.hasPermission("zonnic.chatcolor")) {
                        finalMessage = finalMessage + ChatColor.translateAlternateColorCodes('&', message);
                    } else {
                        finalMessage = finalMessage + message;
                    }

                    // Broadcast the message
                    Bukkit.broadcastMessage(finalMessage);
                    if (!p.hasPermission("zonnicclans.chatbypass")) chatCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
                } else {
                    p.sendMessage(ChatColor.RED + "Bypassing banned words is not allowed");
                }
            }
        } else {
            long secondsLeft = ((chatCooldowns.get(p.getUniqueId()) / 1000) + chatCooldownTime) - (System.currentTimeMillis() / 1000);
            p.sendMessage(plugin.error + "You are currently on chat cool down. Seconds left: " + secondsLeft + "s");
        }
    }
}
