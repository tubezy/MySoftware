package zonnic.land.zonniccustomsuffix;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuffixCommand implements CommandExecutor {
    ZonnicCustomSuffix plugin = ZonnicCustomSuffix.getPlugin();
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
                                p.sendMessage(plugin.pluginPrefix + plugin.error + "Missing arguments. '/suffix set [new suffix]'");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                String newSuffix = "&f[" + sb.toString().trim() + "&f] ";
                                String lengthCheck = utils.removeColorTags(sb.toString().trim());
                                if (lengthCheck.length() <= plugin.getConfig().getInt("suffix.character-max")) {
                                    if (lengthCheck.length() >= plugin.getConfig().getInt("suffix.character-min")) {
                                        if (!utils.hasBannedCharacters(newSuffix)) {
                                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + p.getName() + " meta setsuffix 1 " + newSuffix);
                                            p.sendMessage(plugin.pluginPrefix + ChatColor.GREEN + "Your new suffix has been applied!");
                                            p.sendMessage(plugin.pluginPrefix + ChatColor.GREEN + "Preview: " + p.getDisplayName());
                                            p.sendMessage(plugin.pluginPrefix + ChatColor.GRAY + "If your new suffix was not applied, please contact a staff member.");
                                        } else {
                                            p.sendMessage(plugin.pluginPrefix + plugin.error + "That suffix contains illegal characters.");
                                        }
                                    } else {
                                        p.sendMessage(plugin.pluginPrefix + plugin.error + "There are too little characters in that suffix. (Minimum limit: " + plugin.getConfig().getInt("suffix.character-min") + ")");
                                    }
                                } else {
                                    p.sendMessage(plugin.pluginPrefix + plugin.error + "There are too many characters in that suffix. (Maximum limit: " + plugin.getConfig().getInt("suffix.character-max") + ")");
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + p.getName() + " meta setsuffix 1 &f");
                            p.sendMessage(plugin.pluginPrefix + ChatColor.GREEN + "Your suffix was removed.");
                            p.sendMessage(plugin.pluginPrefix + ChatColor.GREEN + "Preview: " + p.getDisplayName());
                            p.sendMessage(plugin.pluginPrefix + ChatColor.GRAY + "If your suffix was not removed, please contact a staff member.");
                        }
                    } else {
                        p.sendMessage(plugin.pluginPrefix + ChatColor.YELLOW + "Available commands:");
                        p.sendMessage(plugin.pluginPrefix + ChatColor.GOLD + "'/suffix set [suffix]' - " + ChatColor.WHITE + "Sets your suffix to what you entered!");
                        p.sendMessage(plugin.pluginPrefix + ChatColor.GOLD + "'/suffix remove' - " + ChatColor.WHITE + "Removes your suffix!");
                    }
                } else {
                    p.sendMessage(plugin.pluginPrefix + plugin.error + "You are not allowed to set a custom suffix.");
                }
            }
        }
        return true;
    }
}
