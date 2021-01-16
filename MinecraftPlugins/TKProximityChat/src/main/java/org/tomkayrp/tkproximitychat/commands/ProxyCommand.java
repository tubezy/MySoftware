package org.tomkayrp.tkproximitychat.commands;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tomkayrp.tkproximitychat.TKProximityChat;
import org.tomkayrp.tkproximitychat.Utils;

import java.util.List;
import java.util.Objects;

public class ProxyCommand implements CommandExecutor {
    public static List<String> proxyOn = TKProximityChat.plugin.getConfig().getStringList("proxy_on_players");
    public static List<String> spyOn = TKProximityChat.plugin.getConfig().getStringList("spy_on_players");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        proxyOn = TKProximityChat.plugin.getConfig().getStringList("proxy_on_players");
        spyOn = TKProximityChat.plugin.getConfig().getStringList("spy_on_players");
        if (command.getName().equalsIgnoreCase("proxy")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1 || args.length == 2) {
                    if (args[0].equalsIgnoreCase("on")) {
                        if (!proxyOn.contains(p.getUniqueId().toString())) {
                            proxyOn.add(p.getUniqueId().toString());
                            TKProximityChat.plugin.getConfig().set("proxy_on_players", proxyOn);
                            Utils.colorConfig(p, "chat.enable.proximity_chat_enable_message");
                        } else {
                            Utils.colorConfig(p, "chat.enable.proximity_chat_already_enabled_message");
                        }
                    } else if (args[0].equalsIgnoreCase("off")) {
                        if (proxyOn.contains(p.getUniqueId().toString())) {
                            proxyOn.remove(p.getUniqueId().toString());
                            TKProximityChat.plugin.getConfig().set("proxy_on_players", proxyOn);
                            Utils.colorConfig(p, "chat.disable.proximity_chat_disable_message");
                        } else {
                            Utils.colorConfig(p, "chat.disable.proximity_chat_already_disabled_message");
                        }
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        if( p.hasPermission("tkproxmimtychat.reload")) {
                            TKProximityChat.plugin.reloadConfig();
                            Utils.colorConfig(p, "chat.other.reload_message");
                        }
                    } else if (args[0].equalsIgnoreCase("spy")) {
                        if (p.hasPermission("tkproximitychat.spy")) {
                            if (!spyOn.contains(p.getUniqueId().toString())) {
                                spyOn.add(p.getUniqueId().toString());
                                TKProximityChat.plugin.getConfig().set("spy_on_players", spyOn);
                                Utils.colorConfig(p, "chat.enable.proximity_chat_spy_mode_enable_message");
                            } else {
                                spyOn.remove(p.getUniqueId().toString());
                                TKProximityChat.plugin.getConfig().set("spy_on_players", spyOn);
                                Utils.colorConfig(p, "chat.disable.proximity_chat_spy_mode_disable_message");
                            }
                        } else {
                            Utils.colorConfig(p, "chat.other.no_permission");
                        }
                    } else if (args[0].equalsIgnoreCase("range")) {
                        if (p.hasPermission("tkproximitychat.range")) {
                            if (args.length == 2) {
                                if (StringUtils.isNumeric(args[1])) {
                                    int previousRange = TKProximityChat.plugin.getConfig().getInt("range");
                                    int newRange = Integer.parseInt(args[1]);
                                    TKProximityChat.plugin.getConfig().set("range", newRange);
                                    TKProximityChat.plugin.saveConfig();
                                    String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(TKProximityChat.plugin.getConfig().getString("chat.other.new_range_message")));
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
                    List<String> help = TKProximityChat.plugin.getConfig().getStringList("chat.other.help");
                    List<String> help_staff = TKProximityChat.plugin.getConfig().getStringList("chat.other.help_staff");
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
                Bukkit.getConsoleSender().sendMessage(TKProximityChat.starter + ChatColor.RED + "You must be a player to execute this command.");
            }
        }
        return true;
    }
}
