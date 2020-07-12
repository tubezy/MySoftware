package org.tomkayrp.tkbanenchants.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tomkayrp.tkbanenchants.TKBanEnchants;

import java.util.ArrayList;
import java.util.List;

public class banenchantsCommand implements CommandExecutor {

    public static boolean enabled = true;
    public static List<Player> DisabledBypassPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("banenchants")) {
            if (args.length == 1) {
                if (sender.hasPermission("tkbanenchants.onoff") || sender.isOp()) {
                    if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("on")) {
                        if (enabled) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Already_Enabled_Message")));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Enable_Message")));
                            enabled = true;
                        }
                    } else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("off")) {
                        if (!enabled) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Already_Disabled_Message")));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Disable_Message")));
                            enabled = false;
                        }
                    } else if(args[0].equalsIgnoreCase("bypass")) {
                        if(sender instanceof Player) {
                            Player p = (Player) sender;
                            if (p.hasPermission("tkbanenchants.bypass")) {
                                if (!DisabledBypassPlayers.contains(p)) {
                                    DisabledBypassPlayers.add(p);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Disable_Enchant_Bypass_Block_Message")));
                                } else if (DisabledBypassPlayers.contains(p)) {
                                    DisabledBypassPlayers.remove(p);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Enable_Enchant_Bypass_Block_Message")));
                                }
                            }
                        } else {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[ TKBanEnchants ] " + ChatColor.RED + "You must be a player to execute this command.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Incorrect_Arguments")));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Non_Op.No_Permission")));
                }

            } else if (args.length == 0) {
                if (sender.hasPermission("tkbanenchants.help") || sender.isOp()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Help_0")));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Help_1")));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Help_2")));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Help_3")));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Non_Op.No_Permission")));
                }
            } else {
                if (sender.hasPermission("tkbanenchants.onoff") || sender.isOp()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Op.Command.Too_Many_Arguments")));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TKBanEnchants.plugin.getConfig().getString("Chat.Non_Op.No_Permission")));
                }
            }
        }
        return true;
    }
}
