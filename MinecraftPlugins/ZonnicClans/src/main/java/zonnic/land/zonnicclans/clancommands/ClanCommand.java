package zonnic.land.zonnicclans.clancommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.utilities.Utils;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLSetters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClanCommand implements CommandExecutor {
    Utils utils = new Utils();
    ZonnicClans plugin = ZonnicClans.getPlugin();
    SQLGetters sqlget = new SQLGetters();
    SQLSetters sqlset = new SQLSetters();
    public static HashMap<Player, String> pendingInvitation = new HashMap<>(); //Player, Clan
    public static List<Player> socialSpyToggled = new ArrayList<>();
    public static List<UUID> toggledChat = new ArrayList<>();
    public static List<String> toggledClanPvp = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clan")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length != 0) {
                    switch (args[0]) {
                        case "create":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan create [clan name]'");
                            else if (args.length == 2) {
                                String clanName = args[1];
                                sqlset.createClan(p, clanName);
                            } else
                                p.sendMessage(plugin.getPrefixInitials() + plugin.error + "Too many arguments. '/clan create [clan name]'");
                            break;

                        case "leave":
                            sqlset.playerLeaveClan(p);
                            break;

                        case "disband":
                            sqlset.disbandClan(p);
                            break;

                        case "kick":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan kick [player name]'");
                            else if (args.length == 2) {
                                String toBeKicked = args[1];
                                if (sqlget.playerExists(toBeKicked))
                                    sqlset.kickPlayer(p, sqlget.getPlayerUUID(toBeKicked), toBeKicked);
                                else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That player does not exist");

                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan kick [player name]'");
                            break;

                        case "home":
                            sqlset.teleportPlayerToClanHome(p);
                            break;

                        case "sethome":
                            sqlset.setClanHome(p);
                            break;

                        case "invite":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan invite [player name]");
                            else if (args.length == 2) {
                                if (sqlget.playerInClan(p)) {
                                    if (sqlget.getPlayerRank(p) >= 2) {
                                        if (utils.getAmountOfMembers(sqlget.getClan(p)) <= plugin.getConfig().getInt("clans.max-players-in-clan")) {
                                            Player invited = Bukkit.getPlayerExact(args[1]);
                                            if (invited != null && invited.isOnline()) {
                                                String clan = sqlget.getClan(p);
                                                if (!sqlget.playerInClan(invited)) {
                                                    if (!(pendingInvitation.containsKey(invited) && pendingInvitation.get(invited).equals(clan))) {
                                                        p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Sent invitation request to " + invited.getName() + ChatColor.GREEN + "!");
                                                        TextComponent accept = new TextComponent(plugin.getPrefix() + ChatColor.AQUA + "'/clan accept'" + ChatColor.DARK_AQUA + " to accept the invitation " + ChatColor.WHITE + "[" + ChatColor.GREEN + "Click to accept" + ChatColor.WHITE + "]");
                                                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));
                                                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click me to accept your clan invitation!")));
                                                        TextComponent deny = new TextComponent(plugin.getPrefix() + ChatColor.AQUA + "'/clan deny'" + ChatColor.DARK_AQUA + " to deny the invitation " + ChatColor.WHITE + "[" + ChatColor.RED + "Click to deny" + ChatColor.WHITE + "]");
                                                        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan deny"));
                                                        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "Click me to deny your clan invitation!")));

                                                        invited.sendMessage(plugin.getPrefix() + ChatColor.BLUE + "You have invited to " + ChatColor.AQUA + clan + ChatColor.BLUE + " by " + ChatColor.AQUA + p.getName());
                                                        invited.spigot().sendMessage(accept);
                                                        invited.spigot().sendMessage(deny);

                                                        pendingInvitation.remove(invited);
                                                        pendingInvitation.put(invited, clan);

                                                        utils.messageToClan(clan, plugin.getPrefixInitials() + ChatColor.AQUA + invited.getName() + " has been invited to the clan by " + p.getName() + "!");
                                                    } else
                                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That player is already invited.");
                                                } else
                                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That player is already in a clan.");
                                            } else
                                                p.sendMessage(plugin.getPrefix() + plugin.error + "That player is not online.");
                                        } else
                                            p.sendMessage(plugin.getPrefix() + plugin.error + "There are too many players in the clan.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan invite [player name]");
                            break;

                        case "accept":
                            if (pendingInvitation.containsKey(p)) {
                                sqlset.addPlayerToClan(p, pendingInvitation.get(p));
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "You do not have any pending invites");
                            break;

                        case "deny":
                            if (pendingInvitation.containsKey(p)) {
                                p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Invitation has been denied.");
                                pendingInvitation.remove(p);
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "You do not have any pending invites");
                            break;

                        case "uninvite":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan uninvite [player name]'");
                            else if (args.length == 2) {
                                Player toUnInvite = Bukkit.getPlayerExact(args[1]);
                                if (toUnInvite != null && sqlget.playerExists(toUnInvite.getName())) {
                                    if (pendingInvitation.containsKey(toUnInvite)) {
                                        if (pendingInvitation.get(toUnInvite).equals(sqlget.getClan(p))) {
                                            pendingInvitation.remove(toUnInvite);
                                            p.sendMessage(plugin.getPrefix() + plugin.selfGood + toUnInvite.getName() + " has been uninvited.");
                                        } else
                                            p.sendMessage(plugin.getPrefix() + plugin.error + "You have not invited that player.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "You have not invited that player.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That player is not online");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan uninvite [player name]");
                            break;

                        case "rename":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan rename [new name]'");
                            else if (args.length == 2) {
                                String newName = args[1];
                                sqlset.renameClan(p, newName);
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan name [new name'");
                            break;

                        case "chat":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "You must implement text in your clan chat message. '/clan chat [message]'");
                            else {
                                if (sqlget.playerInClan(p)) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 1; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }
                                    String message = sb.toString();
                                    String bannedWordsCheck = ChatColor.stripColor(message);
                                    if (!utils.hasBannedWords(bannedWordsCheck)) {
                                        String clan = sqlget.getClan(p);
                                        utils.messageToClan(clan, ChatColor.GRAY + "<< Clan Chat >> " + p.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.GOLD + message.trim());
                                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + ChatColor.LIGHT_PURPLE + "Clan = " + clan + ChatColor.GREEN + " | Clan Chat: " + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                                        for (Player staff : ClanCommand.socialSpyToggled) {
                                            if ((staff != p) || !(sqlget.getClan(p).equalsIgnoreCase(sqlget.getClan(staff))))
                                                staff.sendMessage(ChatColor.RED + "< Clan Chat Spy > " + ChatColor.YELLOW + p.getDisplayName() + ": " + ChatColor.GOLD + message);
                                        }
                                    } else
                                        p.sendMessage(ChatColor.RED + "Bypassing banned words is not allowed");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                            }
                            break;

                        case "togglechat":
                            if (sqlget.playerInClan(p)) {
                                if (!toggledChat.contains(p.getUniqueId())) {
                                    toggledChat.add(p.getUniqueId());
                                    p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Team chat has been toggled on!");
                                } else
                                    toggledChat.remove(p.getUniqueId());
                                p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Team chat has been toggled " + ChatColor.RED + "off!");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                            break;

                        case "promote":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan promote [player name]'");
                            else if (args.length == 2) {
                                String toPromote = args[1];
                                if (sqlget.playerExists(toPromote)) {
                                    sqlset.promotePlayer(p, toPromote);
                                } else {
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That player does not exist.");
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan promote [player name]'");
                            break;

                        case "demote":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan demote [player name]'");
                            else if (args.length == 2) {
                                String toDemote = args[1];
                                if (sqlget.playerExists(toDemote)) {
                                    sqlset.demotePlayer(p, toDemote);
                                } else {
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That player does not exist.");
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan demote [player name]'");
                            break;

                        case "leader":
                            if (args.length == 1)
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan leader [player name]'");
                            else if (args.length == 2) {
                                String newLeader = args[1];
                                if (sqlget.playerExists(newLeader)) {
                                    sqlset.setNewClanLeader(p, newLeader);
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan leader [player name]'");
                            break;

                        case "who":
                            if (args.length == 1) {
                                if (sqlget.playerInClan(p)) {
                                    utils.returnClanMembers(p, sqlget.getClan(p));
                                } else p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                            } else if (args.length == 2) {
                                String clanName = args[1];
                                if (sqlget.clanExists(clanName)) {
                                    utils.returnClanMembers(p, clanName);
                                } else {
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan who [clan name]'");
                            break;

                        case "stats":
                            if (args.length == 1) {
                                if (sqlget.playerInClan(p)) {
                                    utils.returnClanStats(p, sqlget.getClan(p));
                                } else p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                            } else if (args.length == 2) {
                                String clanName = args[1];
                                if (sqlget.clanExists(clanName)) {
                                    utils.returnClanStats(p, clanName);
                                } else {
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan who [clan name]'");
                            break;

                        case "top":
                            if (args.length == 1) {
                                utils.returnTopClans(p);
                            } else {
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan top'");
                            }
                            break;

                        case "delhome":
                        case "deletehome":
                            if (args.length == 1) {
                                sqlset.deleteClanHome(p);
                            } else {
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan delhome'");
                            }
                            break;

                        case "pvp":
                            if (args.length == 1) {
                                if (sqlget.playerInClan(p)) {
                                    String clan = sqlget.getClan(p);
                                    if (sqlget.getPlayerRank(p) >= 2) {
                                        if (toggledClanPvp.contains(clan)) {
                                            toggledClanPvp.remove(clan);
                                            utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "Clan pvp has been turned" + ChatColor.RED + " off" + plugin.clanAnnouncement + " by " + plugin.clanAnnouncementHighlight + p.getName());
                                        } else {
                                            toggledClanPvp.add(clan);
                                            utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "Clan pvp has been turned" + ChatColor.GREEN + " on" + plugin.clanAnnouncement + " by " + plugin.clanAnnouncementHighlight + p.getName());
                                        }
                                    } else {
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
                                    }
                                } else {
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
                                }
                            }
                            break;

                        case "forcerename":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1 || args.length == 2)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan forcerename [clan name] [new clan name]'");
                                else if (args.length == 3) {
                                    String clanName = args[1];
                                    String newClanName = args[2];
                                    if (sqlget.clanExists(clanName)) {
                                        sqlset.forceRenameClan(p, clanName, newClanName);
                                        p.sendMessage(plugin.getPrefix() + plugin.selfGood + "The clan has been renamed.");
                                    } else {
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                    }
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan forcerename [clan name] [new clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "forcedelete":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan forcedelete [clan name]'");
                                else if (args.length == 2) {
                                    String clanName = args[1];
                                    if (sqlget.clanExists(clanName)) {
                                        sqlset.forceDeleteClan(p, clanName);
                                        p.sendMessage(plugin.getPrefix() + plugin.selfGood + "The clan has been deleted.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan forcedelete [clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "forcedelhome":
                        case "forcedeletehome":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan forcedelhome [clan name]'");
                                else if (args.length == 2) {
                                    String clanName = args[1];
                                    if (sqlget.clanExists(clanName)) {
                                        sqlset.forceDeleteClanHome(p, clanName);
                                        p.sendMessage(plugin.getPrefix() + plugin.selfGood + "The clan's home has been deleted.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan forcedelhome [clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "forcesethome":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments '/clan forcesethome [clan name]'");
                                else if (args.length == 2) {
                                    String clanName = args[1];
                                    if (sqlget.clanExists(clanName)) {
                                        sqlset.forceSetClanHome(p, clanName);
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments '/clan forcesethome [clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "forcekick":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1 || args.length == 2)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan forcekick [player name] [clan name]'");
                                else if (args.length == 3) {
                                    String playerName = args[1];
                                    String clanName = args[2];
                                    if (sqlget.clanExists(clanName)) {
                                        if (sqlget.playerExists(playerName)) {
                                            sqlset.forceKickClanMember(p, clanName, playerName);
                                        } else
                                            p.sendMessage(plugin.getPrefix() + plugin.error + "That player does not exist.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan forcekick [player name] [clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");

                            break;

                        case "forcejoin":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (args.length == 1)
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Missing arguments. '/clan forcejoin [clan name]'");
                                else if (args.length == 2) {
                                    String clanName = args[1];
                                    if (sqlget.clanExists(clanName)) {
                                        if (utils.getAmountOfMembers(clanName) <= plugin.getConfig().getInt("clans.max-players-in-clan")) {
                                            sqlset.forceAddToClan(p, clanName);
                                        } else
                                            p.sendMessage(plugin.getPrefix() + plugin.error + "That clan is full.");
                                    } else
                                        p.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not exist.");
                                } else
                                    p.sendMessage(plugin.getPrefix() + plugin.error + "Too many arguments. '/clan forcejoin [clan name]'");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "socialspy":
                            if (p.hasPermission("zonnicclans.admin")) {
                                if (socialSpyToggled.contains(p)) {
                                    socialSpyToggled.remove(p);
                                    p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Social spy has been turned " + ChatColor.RED + "off");
                                } else {
                                    socialSpyToggled.add(p);
                                    p.sendMessage(plugin.getPrefix() + plugin.selfGood + "Social spy has been turned " + ChatColor.GREEN + "on");
                                }
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "reload":
                            if (p.hasPermission("zonnicclans.admin")) {
                                plugin.reloadConfig();
                                p.sendMessage(plugin.getPrefix() + plugin.selfGood + "The config has been reloaded!");
                            } else
                                p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                            break;

                        case "help":
                            if (!(args.length <= 1  )) {
                                switch (args[1]) {
                                    case "1":
                                        if (p.hasPermission("zonnicclans.admin"))
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 1/6]");
                                        else
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 1/5]");

                                        p.sendMessage(ChatColor.YELLOW + "'/clan help [page]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Help menu for zonnic clans");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan create [clan name]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Create a new clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan invite [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Invite a player to your clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan uninvite [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Removes invitation from the invited player");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan leave' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Leaves your current clan");
                                        break;
                                    case "2":
                                        if (p.hasPermission("zonnicclans.admin"))
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 2/6]");
                                        else
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 2/5]");

                                        p.sendMessage(ChatColor.YELLOW + "'/clan chat [message]' OR '/cc [message] " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Send a message to your clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan togglechat' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Toggles sending a message to your clan every time you speak in chat on/off");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan accept' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Accepts an invitation from a clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan deny' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Denies an invitation from a clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan promote [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Promotes a player in your clan");
                                        break;
                                    case "3":
                                        if (p.hasPermission("zonnicclans.admin"))
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 3/6]");
                                        else
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 3/5]");

                                        p.sendMessage(ChatColor.YELLOW + "'/clan demote [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Demotes a player in your clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan kick [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Kicks a player from your clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan sethome' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Sets the clan home to your current location");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan home' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Teleports you to the clan home");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan delhome' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Deletes your clan home");
                                        break;
                                    case "4":
                                        if (p.hasPermission("zonnicclans.admin"))
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 4/6]");
                                        else
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 4/5]");

                                        p.sendMessage(ChatColor.YELLOW + "'/clan disband' " + ChatColor.LIGHT_PURPLE + "-" + ChatColor.WHITE + " Deletes your clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan rename [new clan name]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Renames your clan to the new input name");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan leader [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Sets the clan leader to the player that was input");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan who [player]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + "Returns all members in the clan");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan stats [clan]' " + ChatColor.BLUE + "-" + ChatColor.WHITE + "Returns a clan's statistics");
                                        break;
                                    case "5":
                                        if (p.hasPermission("zonnicclans.admin"))
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + " Help menu " + ChatColor.GRAY + "[Page 5/6]");
                                        else
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + " Help menu " + ChatColor.GRAY + "[Page 5/5]");

                                        p.sendMessage(ChatColor.YELLOW + "'/clan top' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Returns the clans with most kills");
                                        p.sendMessage(ChatColor.YELLOW + "'/clan pvp' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Enables clan pvp for your clan");
                                        break;
                                    default:
                                        if (p.hasPermission("zonnicclans.admin")) {
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.RED + "Help menu (ADMIN COMMANDS) " + ChatColor.GRAY + "[Page 6/6]");

                                            p.sendMessage(ChatColor.DARK_RED + "'/clan forcerename [clan name] [new clan name]' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Forcefully renames a clans name");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan forcekick [player] [clan name]' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Forcefully kicks a player from their clan");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan forcejoin [clan name]' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Forcefully adds you to the clan");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan forcesethome [clan name]' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Forcefully sets a clan's home to your location");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan forcedelhome [clan name]' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Forcefully deletes a clan's home");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan socialspy' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Spy on clan exclusive chat messages.");
                                            p.sendMessage(ChatColor.DARK_RED + "'/clan reload' " + ChatColor.YELLOW + "-" + ChatColor.RED + " Reloads the config");
                                        } else {
                                            p.sendMessage(ChatColor.GRAY + "Zonnic" + ChatColor.WHITE + "Clans " + ChatColor.BLUE + "Help menu " + ChatColor.GRAY + "[Page 5/5]");

                                            p.sendMessage(ChatColor.YELLOW + "'/clan top' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Returns the clans with most kills");
                                            p.sendMessage(ChatColor.YELLOW + "'/clan pvp' " + ChatColor.BLUE + "-" + ChatColor.WHITE + " Enables clan pvp for your clan");
                                        }
                                }
                            } else {
                                Bukkit.dispatchCommand(p, "clan help 1");
                            }
                            break;

                        default:
                            p.sendMessage(plugin.getPrefix() + plugin.error + "Unknown command.");
                    }
                } else {
                    p.sendMessage(ChatColor.WHITE + "Zonnic " + ChatColor.GRAY + "Clans " + plugin.version + ChatColor.DARK_GRAY + " (by tubez)");
                    p.sendMessage(ChatColor.YELLOW + "Type '/clan help' for help!");
                }
            } else
                Bukkit.getConsoleSender().sendMessage(plugin.getPrefixInitials() + plugin.error + "You must be a player to do that.");
        }
        return true;
    }
}
