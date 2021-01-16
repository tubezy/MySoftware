package zonnic.land.zonnicclans.clancommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;

public class QuickClanChat implements CommandExecutor {
    ZonnicClans plugin = ZonnicClans.getPlugin();
    Utils utils = new Utils();
    SQLGetters sqlget = new SQLGetters();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sqlget.playerInClan(p)) {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(" ");
                }
                String message = sb.toString();
                String bannedWordsCheck = ChatColor.stripColor(message);
                if (!utils.hasBannedWords(bannedWordsCheck)) {
                    String clan = sqlget.getClan(p);
                    utils.messageToClan(clan, ChatColor.YELLOW + "<< Clan Chat >> " + p.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.GOLD + message.trim());
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + ChatColor.LIGHT_PURPLE + "Clan = " + clan + ChatColor.GREEN + " | Clan Chat: " + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                    for (Player staff : ClanCommand.socialSpyToggled) {
                        if ((staff != p) || !(sqlget.getClan(p).equalsIgnoreCase(sqlget.getClan(staff))))
                            staff.sendMessage(ChatColor.RED + "< Clan Chat Spy > " + ChatColor.YELLOW + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Bypassing banned words is not allowed");
                }
            } else {
                p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefixInitials() + plugin.error + "You must be a player to do that.");
        }
        return true;
    }
}
