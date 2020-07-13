package org.tubez.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tubez.manhunt.Manhunt;
import org.tubez.manhunt.TubezMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManhuntCommand implements CommandExecutor {

    public static List<Player> hunters = new ArrayList<>();
    public static Player speedrunner = null;
    public static boolean started = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("manhunt")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ItemStack compass = new ItemStack(Material.COMPASS);
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("hunter")) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if (p.hasPermission("manhunt.hunter.add")) {
                                if (args.length == 2) {
                                    Player target = Bukkit.getPlayerExact(args[1]);
                                    if (target != null) {
                                        if (!hunters.contains(target)) {
                                            hunters.add(target);
                                            // Message: Added target to hunters
                                            TubezMethods.colorConfig(p, "Command.Sender.Added_Hunter", "{target}", target.getName());
                                            // Message: you are now a hunter
                                            TubezMethods.colorConfig(p, "Command.Target.Added_Hunter");
                                            p.getInventory().addItem(compass);
                                        } else {
                                            // Message: Target already in hunters
                                            TubezMethods.colorConfig(p, "Command.Sender.Already_Hunter");
                                        }
                                    } else {
                                        // Message: Target does not exist
                                        TubezMethods.colorConfig(p, "Command.Sender.Target_Non_Existant");
                                    }
                                } else {
                                    // Message: missing arguments
                                    TubezMethods.colorConfig(p, "Command.Sender.Missing_Arguments");
                                }
                            } else {
                                // Message: no permissions
                                TubezMethods.colorConfig(p, "Command.Sender.No_Permission");
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            if (p.hasPermission("manhunt.hunter.remove")) {
                                if (args.length == 2) {
                                    Player target = Bukkit.getPlayerExact(args[1]);
                                    if (target != null) {
                                        if (hunters.contains(target)) {
                                            hunters.remove(target);
                                            // Message: Removed target from hunters
                                            TubezMethods.colorConfig(p,"Command.Sender.No_Longer_Hunter", "{target}", target.getName());
                                            // Message: you are no longer a hunter
                                            TubezMethods.colorConfig(p,"Command.Target.No_Longer_Hunter");
                                            p.getInventory().remove(compass);
                                        } else {
                                            // Message: target is not in hunters
                                            TubezMethods.colorConfig(p,"Command.Sender.Not_Hunter", "{target}", target.getName());
                                        }
                                    } else {
                                        // Message: target does not exist
                                        p.sendMessage("");
                                    }
                                } else {
                                    // Message: missing arguments
                                    p.sendMessage("");
                                }
                            } else {
                                // Message: no permissions
                                TubezMethods.colorConfig(p, "Command.Sender.No_Permission");
                            }
                            } else {
                            // Message: unknown arguments
                            p.sendMessage("");
                        }
                        } else if (args[0].equalsIgnoreCase("speedrunner")) {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (p.hasPermission("manhunt.speedrunner.add")) {
                                    if (args.length == 2) {
                                        Player target = Bukkit.getPlayerExact(args[1]);
                                        if (target != null) {
                                            if (speedrunner == null) {
                                                speedrunner = target;
                                                // Message: target is now speedrunner.
                                                p.sendMessage("");
                                                // Message: you are now a speedrunner
                                                target.sendMessage("");
                                            } else {
                                                // Message: someone else is already the speedrunner.
                                                p.sendMessage("");
                                            }
                                        } else {
                                            // Message: target does not exist
                                            p.sendMessage("");
                                        }
                                    } else {
                                        // Message: missing arguments.
                                        p.sendMessage("");
                                    }
                                } else {
                                    // Message: no permissions
                                    TubezMethods.colorConfig(p, "Command.Sender.No_Permission");
                                }
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (p.hasPermission("manhunt.speedrunner.remove")) {
                                    if (args.length == 2) {
                                        Player target = Bukkit.getPlayerExact(args[1]);
                                        if (target != null) {
                                            if(speedrunner == target) {
                                                speedrunner = null;
                                                // Message: target is no longer speedrunner.
                                                TubezMethods.colorConfig(p,"Command.Sender.No_Longer_Speedrunner", "{target}", target.getName());
                                                // Message: you are no longer a speedrunner
                                                TubezMethods.colorConfig(target, "Command.Target.No_Longer_Speedrunner");
                                            } else {
                                                // Message: target is not a speedrunner.
                                                TubezMethods.colorConfig(p,"Command.Sender.Not_Speedrunner", "{target}", target.getName());
                                            }
                                        } else {
                                            // Message: target does not exist.
                                            TubezMethods.colorConfig(p, "Command.Sender.Target_Non_Existant");
                                        }
                                    } else {
                                        // Message: Missing arguments
                                        TubezMethods.colorConfig(p, "Command.Sender.Missing_Arguments");
                                    }
                                } else {
                                    // Message: No permission
                                    TubezMethods.colorConfig(p, "Command.Sender.No_Permission");
                                }
                            } else {

                            }
                        } else if (args[0].equalsIgnoreCase("reload")) {
                            if (p.hasPermission("manhunt.reload")) {
                                // Message: Reloading config
                                TubezMethods.colorConfig(p, "Command.Sender.Reload_Before");
                                Manhunt.plugin.reloadConfig();
                                // Message: Reloaded config
                                TubezMethods.colorConfig(p, "Command.Sender.Reload_After");
                            } else {
                                // Message: No permission
                                TubezMethods.colorConfig(p, "Command.Sender.No_Permission");
                            }
                        } else if (args[0].equalsIgnoreCase("start")) {
                            if (p.hasPermission("manhunt.start")) {
                                if (!started) {
                                    if (speedrunner != null && hunters.size() > 0) {
                                        Random randomCoord = new Random();
                                        int x = randomCoord.nextInt(1000), z = randomCoord.nextInt(1000);
                                        int ysr = 0;
                                        int maxDistanceHunter = 20;
                                        int minDistanceHunter = 10;
                                        Location randomLocationSR = new Location(p.getWorld(), x, ysr ,z);
                                        ysr = randomLocationSR.getWorld().getHighestBlockYAt(randomLocationSR);
                                        randomLocationSR.setY(ysr);
                                        while (!TubezMethods.safeBlock(randomLocationSR)) {
                                            randomLocationSR = new Location(p.getWorld(), x, ysr ,z);
                                            ysr = randomLocationSR.getWorld().getHighestBlockYAt(randomLocationSR);
                                            randomLocationSR.setY(ysr);
                                        }
                                        speedrunner.teleport(randomLocationSR);
                                        for (Player hunter : hunters) {
                                            int randaddx = randomCoord.nextInt(maxDistanceHunter + 1 - minDistanceHunter) + minDistanceHunter;
                                            int randaddz = randomCoord.nextInt(maxDistanceHunter + 1 - minDistanceHunter) + minDistanceHunter;
                                            int XHunter = x + randaddx, ZHunter = z + randaddz;
                                            int yh = 0;
                                            Location randomLocationH = new Location(p.getWorld(), XHunter, yh, ZHunter);
                                            yh = randomLocationH.getWorld().getHighestBlockYAt(randomLocationSR);
                                            randomLocationH.setY(yh);
                                            while (!TubezMethods.safeBlock(randomLocationH)) {
                                                randomLocationH = new Location(p.getWorld(), XHunter, yh, ZHunter);
                                                yh = randomLocationH.getWorld().getHighestBlockYAt(randomLocationSR);
                                                randomLocationH.setY(yh);
                                            }
                                            hunter.teleport(randomLocationH);
                                        }
                                        started = true;
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "Command.All.Game_Start").replace("{speedrunner}", speedrunner.getName()));
                                    } else {
                                        if (speedrunner == null) {
                                            TubezMethods.colorConfig(p, "Command.Sender.No_Speedrunner_Start");
                                        } else {
                                            TubezMethods.colorConfig(p, "Command.Sender.No_Hunter_Start");
                                        }
                                    }
                                } else {
                                    TubezMethods.colorConfig(p, "Command.Sender.Game_Already_Started");
                                }
                            }
                        }
                    } else {
                        List<String> helpList = Manhunt.plugin.getConfig().getStringList("Command.Sender.Help");
                        for (String message : helpList) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(Manhunt.soutIntro + ChatColor.RED + "You must be a player to execute this command.");
                }
            }
        return true;
    }

}
