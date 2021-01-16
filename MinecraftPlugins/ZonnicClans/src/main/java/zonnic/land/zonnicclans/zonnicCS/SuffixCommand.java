package zonnic.land.zonnicclans.zonnicCS;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.events.ChatListener;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLSetters;

public class SuffixCommand implements CommandExecutor {
    ZonnicClans plugin = ZonnicClans.getPlugin();
    SQLSetters sqlset = new SQLSetters();
    SQLGetters sqlget = new SQLGetters();
    Utils utils = new Utils();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("suffix")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("zonnicCS.allow")) {
                    if (args.length != 0) {
                        if (args[0].equalsIgnoreCase("set")) {
                            if (args.length == 1) {
                                p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "Missing arguments. '/suffix set [new suffix]'");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                String newSuffix = "&f[" + sb.toString().trim() + "&f] ";
                                String lengthCheck = utils.removeColorTags(sb.toString().trim());
                                if (lengthCheck.length() <= plugin.getConfig().getInt("suffix.character-max")) {
                                    if (lengthCheck.length() >= plugin.getConfig().getInt("suffix.character-min")) {
                                        if (!utils.hasSpecialCharacter(newSuffix) && !utils.hasBannedWords(newSuffix)) {
                                            sqlset.setPlayerSuffix(p, newSuffix);
                                            p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GREEN + "Your new suffix has been applied!");
                                            p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GREEN + "Preview: " + p.getDisplayName() + " " + ChatColor.translateAlternateColorCodes('&', newSuffix));
                                            ChatListener.playerSuffixes.put(p, newSuffix);
                                        } else {
                                            p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "That suffix contains illegal characters.");
                                        }
                                    } else {
                                        p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "There are too little characters in that suffix. (Minimum limit: " + plugin.getConfig().getInt("suffix.character-min") + ")");
                                    }
                                } else {
                                    p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "There are too many characters in that suffix. (Maximum limit: " + plugin.getConfig().getInt("suffix.character-max") + ")");
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (!sqlget.getPlayerSuffix(p).equals("NONE,FILLER")) {
                                sqlset.deletePlayerSuffix(p);
                                p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GREEN + "Your suffix was removed!");
                                p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GREEN + "Preview: " + p.getDisplayName());
                                ChatListener.playerSuffixes.remove(p);
                            } else {
                                p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "You do not have a suffix.");
                            }
                        }
                    } else {
                        p.sendMessage(plugin.suffixPluginPrefix + ChatColor.YELLOW + "Available commands:");
                        p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GOLD + "'/suffix set [suffix]' - " + ChatColor.WHITE + "Sets your suffix to what you entered!");
                        p.sendMessage(plugin.suffixPluginPrefix + ChatColor.GOLD + "'/suffix remove' - " + ChatColor.WHITE + "Removes your suffix!");
                    }
                } else {
                    p.sendMessage(plugin.suffixPluginPrefix + plugin.error + "You are not allowed to set a custom suffix.");
                }
            }
        }
        return true;
    }
}