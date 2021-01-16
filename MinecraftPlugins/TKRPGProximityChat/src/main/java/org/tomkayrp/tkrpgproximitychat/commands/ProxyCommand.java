package org.tomkayrp.tkrpgproximitychat.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tomkayrp.tkrpgproximitychat.TKRPGProximityChat;
import org.tomkayrp.tkrpgproximitychat.Utils;

import java.util.List;
import java.util.Objects;

public class ProxyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("proxy")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1 || args.length == 2) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if( p.hasPermission("tkproxmimtychat.reload")) {
                            TKRPGProximityChat.plugin.reloadConfig();
                            Utils.colorConfig(p, "chat.other.reload_message");
                        }
                    } else if (args[0].equalsIgnoreCase("range")) {
                        if (p.hasPermission("tkproximitychat.range")) {
                            if (args.length == 2) {
                                if (StringUtils.isNumeric(args[1])) {
                                    int previousRange = TKRPGProximityChat.plugin.getConfig().getInt("range");
                                    int newRange = Integer.parseInt(args[1]);
                                    TKRPGProximityChat.plugin.getConfig().set("range", newRange);
                                    TKRPGProximityChat.plugin.saveConfig();
                                    String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKRPGProximityChat.plugin.getConfig().getString("chat.other.new_range_message")));
                                    String finalMessage = message.replaceAll("\\{newrange}", String.valueOf(newRange)).replaceAll("\\{oldrange}", String.valueOf(previousRange));
                                    p.sendMessage(finalMessage);
                                } else {
                                    Utils.colorConfig(p, "chat.other.error_message");
                                }
                            } else {
                                Utils.colorConfig(p, "chat.other.error_message");
                            }
                        } else {
                            Utils.colorConfig(p, "chat.other.no_permission");
                        }
                    }
                } else if (args.length == 0) {
                    List<String> help = TKRPGProximityChat.plugin.getConfig().getStringList("chat.other.help");
                    List<String> help_staff = TKRPGProximityChat.plugin.getConfig().getStringList("chat.other.help_staff");
                    for (String message : help) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    if (p.hasPermission("tkproximitychat.fullhelp")) {
                        for (String message: help_staff) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    Utils.colorConfig(p, "chat.other.error_message");
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(TKRPGProximityChat.starter + ChatColor.RED + "You must be a player to execute this command.");
            }
        }
        return true;
    }
}
