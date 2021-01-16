package zonnic.land.zonnicclans.utilities.mysqlutilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import zonnic.land.zonnicclans.clancommands.ClanCommand;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.utilities.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SQLSetters {

    ZonnicClans plugin = ZonnicClans.getPlugin();
    Utils utils = new Utils();
    SQLGetters sqlget = new SQLGetters();

    public void setPlayerSuffix(Player player, String suffix) { // zonnicCS function
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE players SET suffix=? WHERE uuid=?");
            statement.setString(1, suffix);
            statement.setString(2, String.valueOf(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deletePlayerSuffix(Player player) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE players SET suffix=? WHERE uuid=?");
            statement.setString(1, "NONE,FILLER,FILLER");
            statement.setString(2, String.valueOf(player.getUniqueId()));
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void forceAddToClan(Player player, String clanName) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO clan_members (uuid,clan,rank) VALUE (?,?,?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, clanName);
            statement.setInt(3, 0);
            statement.executeUpdate();
            ClanCommand.pendingInvitation.remove(player);

            utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + " has joined your clan forcefully!", player);
            player.sendMessage(plugin.getPrefix() + plugin.selfGood + "You have joined the " + plugin.selfGoodHighlight + clanName + " clan!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void forceKickClanMember(Player player, String clanName, String playerName) {
        List<String> allPlayersInClan = sqlget.getAllClanMembersUUID(clanName);
        String playerUUID = sqlget.getPlayerUUID(playerName);
        if (allPlayersInClan.contains(playerUUID)) {
            if (sqlget.getPlayerRank(playerUUID) != 3) {
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM clan_members WHERE uuid=?");
                    statement.setString(1, playerUUID);
                    statement.executeUpdate();

                    utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "The player " + plugin.clanAnnouncementHighlight + playerName + plugin.clanAnnouncement + " was kicked from your clan by an admin" + ".");

                    for (Player kicked : Bukkit.getOnlinePlayers()) {
                        if (kicked.getUniqueId().toString().equals(playerName))
                            kicked.sendMessage(plugin.getPrefix() + plugin.selfBad + "You have been kicked from " + plugin.selfBadHighlight + clanName + ".");
                    }
                    player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully kicked " + playerName + " from " + clanName);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot kick the leader of a clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "That player is not in that clan.");
        }
    }


    public void forceSetClanHome(Player player, String clanName) {
        int distFromSpawn = plugin.getConfig().getInt("clans.homerange");
        Location playerLoc = player.getLocation();
        int x = playerLoc.getBlockX();
        int y = playerLoc.getBlockY();
        int z = playerLoc.getBlockZ();
        if ((x >= distFromSpawn || x <= -distFromSpawn) || (z >= distFromSpawn || z <= -distFromSpawn)) {
            try {
                String formattedLocation = player.getWorld().getName() + "," + x + "," + y + "," + z + "," + playerLoc.getYaw() + "," + playerLoc.getPitch();
                PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET base=? WHERE name=?");
                statement.setString(1, formattedLocation);
                statement.setString(2, clanName);
                statement.executeUpdate();

                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "An admin has set a new clan home location!");

                player.sendMessage(plugin.getPrefix() + plugin.selfGood + clanName + "'s home was updated.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "That clan's home cannot be within " + distFromSpawn + " blocks range from spawn (0, 0)");
        }
    }

    public void forceDeleteClan(Player player, String clan) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM clans WHERE name=?");
            statement.setString(1, clan);

            List<String> clanMembers = sqlget.getAllClanMembersUUID(clan);
            for (String clanMember : clanMembers) {
                PreparedStatement remove = plugin.getConnection().prepareStatement("DELETE FROM clan_members WHERE uuid=?");
                remove.setString(1, clanMember);
                remove.executeUpdate();
                utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementBad + "Your clan was deleted by an admin.", player);
            }

            player.sendMessage(plugin.getPrefix() + plugin.selfGood + "Clan has been deleted.");
            Bukkit.broadcastMessage(plugin.getPrefixInitials() + plugin.announcement + "The clan " + plugin.announcementHighlight + clan + plugin.announcement + " has been deleted by an admin.");

            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void forceRenameClan(Player player, String clanName, String newClanName) {
        if (utils.hasSpecialCharacter(newClanName)  && !utils.hasBannedWords(newClanName)) {
            if (newClanName.length() <= plugin.getConfig().getInt("clans.max-characters")) {
                if (newClanName.length() >= plugin.getConfig().getInt("clans.min-characters")) {
                    try {
                        utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "Your clan's name has been forcefully renamed from " + plugin.clanAnnouncementHighlight + clanName + plugin.clanAnnouncement + " to " + plugin.clanAnnouncementHighlight + newClanName + plugin.clanAnnouncement + " by an admin!");
                        Bukkit.broadcastMessage(plugin.getPrefixInitials() + plugin.announcement + "Clan " + plugin.announcementHighlight + clanName + plugin.announcement + " has been forcefully renamed to " + plugin.announcementHighlight + newClanName + plugin.announcement + " by an admin!");

                        PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET name=? WHERE name=?");
                        statement.setString(1, newClanName);
                        statement.setString(2, clanName);
                        statement.executeUpdate();

                        PreparedStatement statement1 = plugin.getConnection().prepareStatement("UPDATE clan_members SET clan=? WHERE clan=?");
                        statement1.setString(1, newClanName);
                        statement1.setString(2, clanName);
                        statement1.executeUpdate();

                        player.sendMessage(plugin.getPrefix() + plugin.selfGood + clanName + "'s name was updated to " + newClanName + ".");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name does not have enough characters. Character minimum limit: " + plugin.getConfig().getInt("clans.min-characters") + " characters.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name exceeds the character limit. Character limit: " + plugin.getConfig().getInt("clans.max-characters") + " characters.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "Clan names cannot contain any special characters.");
        }
    }

    public void forceDeleteClanHome(Player player, String clan) {
        if (sqlget.clanHasHome(clan)) {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET base=? WHERE name=?");
                statement.setString(1, "None");
                statement.setString(2, clan);
                statement.executeUpdate();

                utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "An admin has deleted your clan home!");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "That clan does not have a home");
        }
    }

    public static void incrementClanKills(String clan, int amount) {
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("UPDATE clans SET kills=? WHERE name=?");
            statement.setInt(1, SQLGetters.getClanKills(clan) + amount);
            statement.setString(2, clan);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void incrementClanDeaths(String clan, int amount) {
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("UPDATE clans SET deaths=? WHERE name=?");
            statement.setInt(1, SQLGetters.getClanDeaths(clan) + amount);
            statement.setString(2, clan);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteClanHome(Player player) {
        if (sqlget.playerInClan(player)) {
            if (sqlget.getPlayerRank(player) >= 2) {
                String clanName = sqlget.getClan(player);
                if (sqlget.clanHasHome(clanName)) {
                    try {
                        PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET base=? WHERE name=?");
                        statement.setString(1, "None");
                        statement.setString(2, clanName);
                        statement.executeUpdate();

                        utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + " has deleted your clan home!");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "Your clan does not have a home.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void setNewClanLeader(Player player, String newLeader) {
        if (sqlget.playerInClan(player)) {
            String clanName = sqlget.getClan(player);
            String playerUUID = sqlget.getPlayerUUID(newLeader);
            if (clanName.equalsIgnoreCase(sqlget.getClan(playerUUID))) {
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clan_members SET rank=? WHERE uuid=?");
                    statement.setInt(1, 3);
                    statement.setString(2, playerUUID);
                    statement.executeUpdate();

                    PreparedStatement statement1 = plugin.getConnection().prepareStatement("UPDATE clan_members SET rank=? WHERE uuid=?");
                    statement1.setInt(1, 2);
                    statement1.setString(2, sqlget.getPlayerUUID(player.getName()));
                    statement1.executeUpdate();

                    Bukkit.broadcastMessage(plugin.getPrefix() + plugin.announcementHighlight + player.getName() + plugin.announcement + " gave leadership of the clan " + plugin.announcementHighlight + clanName + plugin.announcement + " to " + plugin.announcementHighlight + newLeader);
                    utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "The leadership of your clan was given to " + plugin.clanAnnouncementHighlight + newLeader + plugin.clanAnnouncement + " by " + plugin.clanAnnouncementHighlight + player.getName());

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to set new leader in clan. Error Code 19"); // Error Code 19
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "That player is not in your clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void promotePlayer(Player player, String toBePromoted) {
        if (sqlget.playerInClan(player)) {
            String clanName = sqlget.getClan(player);
            String playerUUID = sqlget.getPlayerUUID(toBePromoted);
            if (clanName.equalsIgnoreCase(sqlget.getClan(playerUUID))) {
                int playerRank = sqlget.getPlayerRank(player);
                int toBePromotedRank = sqlget.getPlayerRank(playerUUID);
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clan_members SET rank=? WHERE uuid=?");
                    statement.setString(2, playerUUID);
                    if (playerRank == 3) { // player is leader
                        if ((toBePromotedRank != 3) && (toBePromotedRank != 2)) {
                            if (toBePromotedRank == 1) {
                                statement.setInt(1, 2);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + toBePromoted + plugin.clanAnnouncement + " has been promoted to 'Co-Leader' by " + plugin.clanAnnouncementHighlight + player.getName());
                            }

                            if (toBePromotedRank == 0) {
                                statement.setInt(1, 1);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + toBePromoted + plugin.clanAnnouncement + " has been promoted to 'Moderator' by " + plugin.clanAnnouncementHighlight + player.getName());
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot promote that player.");
                        }
                    } else if (playerRank == 2) { // player is co leader
                        if ((toBePromotedRank != 2)) {
                            if (toBePromotedRank == 0) {
                                statement.setInt(1, 1);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + toBePromoted + plugin.clanAnnouncement + " has been promoted to 'Moderator' by " + plugin.clanAnnouncementHighlight + player.getName());
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot promote that player.");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to promote player. Error Code 18"); // Error Code 18
                    player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "That player is not in your clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void demotePlayer(Player player, String toBeDemoted) {
        if (sqlget.playerInClan(player)) {
            String clanName = sqlget.getClan(player);
            String playerUUID = sqlget.getPlayerUUID(toBeDemoted);
            if (clanName.equalsIgnoreCase(sqlget.getClan(playerUUID))) {
                int playerRank = sqlget.getPlayerRank(player);
                int toBeDemotedRank = sqlget.getPlayerRank(playerUUID);
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clan_members SET rank=? WHERE uuid=?");
                    statement.setString(2, playerUUID);
                    if (playerRank == 3) { // player is leader
                        if ((toBeDemotedRank <= 2) && (toBeDemotedRank != 0)) { // player is co leader, moderator, or member
                            if (toBeDemotedRank == 2) {
                                statement.setInt(1, 1);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementBadHighlight + toBeDemoted + plugin.clanAnnouncementBad + " has been demoted to 'Moderator' by " + plugin.clanAnnouncementBadHighlight + player.getName());
                            }

                            if (toBeDemotedRank == 1) {
                                statement.setInt(1, 0);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementBadHighlight + toBeDemoted + plugin.clanAnnouncementBad + " has been demoted to 'Member' by " + plugin.clanAnnouncementBadHighlight + player.getName());
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot demote that player.");
                        }
                    } else if (playerRank == 2) { // player is co leader
                        if ((toBeDemotedRank <= 1) && (toBeDemotedRank != 0)) { // player is moderator
                            if (toBeDemotedRank == 1) {
                                statement.setInt(1, 0);
                                statement.executeUpdate();
                                utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementBadHighlight + toBeDemoted + plugin.clanAnnouncementBad + " has been demoted to 'Member' by " + plugin.clanAnnouncementBadHighlight + player.getName());
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot demote that player.");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to demote player. Error Code 17"); // Error Code 17
                    player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "That player is not in your clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void renameClan(Player player, String newName) {
        if (sqlget.playerInClan(player)) {
            if (!sqlget.clanExists(newName)) {
                String clanName = sqlget.getClan(player);
                if (!clanName.equalsIgnoreCase(newName)) {
                    if (utils.hasSpecialCharacter(newName) && !utils.hasBannedWords(newName)) {
                        if (sqlget.getPlayerRank(player) >= 2) {
                            if (newName.length() <= plugin.getConfig().getInt("clans.max-characters")) {
                                if (newName.length() >= plugin.getConfig().getInt("clans.min-characters")) {
                                    try {
                                        utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "Your clan's name has been renamed from " + plugin.clanAnnouncementHighlight + clanName + plugin.clanAnnouncement + " to " + plugin.clanAnnouncementHighlight + newName + plugin.clanAnnouncement + " by " + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + "!");
                                        Bukkit.broadcastMessage(plugin.getPrefixInitials() + plugin.announcement + "Clan " + plugin.announcementHighlight + clanName + plugin.announcement + " has been renamed to " + plugin.announcementHighlight + newName + plugin.announcement + "!");

                                        PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET name=? WHERE name=?");
                                        statement.setString(1, newName);
                                        statement.setString(2, clanName);
                                        statement.executeUpdate();

                                        PreparedStatement statement1 = plugin.getConnection().prepareStatement("UPDATE clan_members SET clan=? WHERE clan=?");
                                        statement1.setString(1, newName);
                                        statement1.setString(2, clanName);
                                        statement1.executeUpdate();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to rename clan. Error Code 16"); // Error Code 16
                                        player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                                    }
                                } else {
                                    player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name does not have enough characters. Character minimum limit: " + plugin.getConfig().getInt("clans.min-characters") + " characters.");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name exceeds the character limit. Character limit: " + plugin.getConfig().getInt("clans.max-characters") + " characters.");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.error + "Clan names cannot contain any special characters.");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "The rename cannot be the same name as your current clan name.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "Theres already a clan with a name of " + newName + ".");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void addPlayerToClan(Player player, String clan) {
        if (!sqlget.playerInClan(player)) {
            if (sqlget.clanExists(clan)) {
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO clan_members (uuid,clan,rank) VALUE (?,?,?)");
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, clan);
                    statement.setInt(3, 0);
                    statement.executeUpdate();
                    ClanCommand.pendingInvitation.remove(player);

                    utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + " has joined your clan!", player);
                    player.sendMessage(plugin.getPrefix() + plugin.selfGood + "You have joined the " + plugin.selfGoodHighlight + clan + " clan!");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to add player to clan. Error Code 13"); // Error Code 13
                    player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "The clan no longer exists.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are already in a clan.");
        }
    }

    public void teleportPlayerToClanHome(Player player) {
        if (sqlget.playerInClan(player)) {
            if (sqlget.getPlayerRank(player) >= 1) {
                if (!Utils.teleportingPlayers.containsKey(player.getUniqueId())) {
                    try {
                        PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT base FROM clans WHERE name=?");
                        statement.setString(1, sqlget.getClan(player));
                        ResultSet rs = statement.executeQuery();
                        List<String> location = new ArrayList<>();
                        if (rs.next()) {

                            location = Arrays.asList(rs.getString("base").split(","));
                        }
                        if (!location.get(0).equalsIgnoreCase("none")) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Teleporting in " + (plugin.getConfig().getInt("clans.hometp-grace") + " seconds, don't move!"));
                            List<String> finalLocation = location;
                            int id = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Location baseLocation = new Location(Bukkit.getServer().getWorld(finalLocation.get(0)), Double.parseDouble(finalLocation.get(1)), Double.parseDouble(finalLocation.get(2)), Double.parseDouble(finalLocation.get(3)));
                                    baseLocation.setYaw(Float.parseFloat(finalLocation.get(4)));
                                    baseLocation.setPitch(Float.parseFloat(finalLocation.get(5)));
                                    player.teleport(baseLocation);

                                    Utils.teleportingPlayers.remove(player.getUniqueId());
                                }
                            }.runTaskLater(plugin, ((plugin.getConfig().getInt("clans.hometp-grace") + 1) * 20)).getTaskId();
                            Utils.teleportingPlayers.put(player.getUniqueId(), id);

                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.error + "Your clan does not have a clan home!");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to teleport player to clan home. Error Code 12"); // Error Code 12
                        player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "You are already teleporting to your clans home.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void setClanHome(Player player) {
        if (sqlget.playerInClan(player)) {
            if (sqlget.getPlayerRank(player) >= 2) {
                String clanName = sqlget.getClan(player);
                int distFromSpawn = plugin.getConfig().getInt("clans.homerange");
                Location playerLoc = player.getLocation();
                int x = playerLoc.getBlockX();
                int y = playerLoc.getBlockY();
                int z = playerLoc.getBlockZ();
                if ((x >= distFromSpawn || x <= -distFromSpawn) || (z >= distFromSpawn || z <= -distFromSpawn)) {
                    try {
                        String formattedLocation = player.getWorld().getName() + "," + x + "," + y + "," + z + "," + playerLoc.getYaw() + "," + playerLoc.getPitch();
                        PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE clans SET base=? WHERE name=?");
                        statement.setString(1, formattedLocation);
                        statement.setString(2, clanName);
                        statement.executeUpdate();

                        utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + " has set a new clan home location!");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to set new clan home Error Code 11"); // Error Code 11
                        player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "Your clan's home cannot be within " + distFromSpawn + " blocks range from spawn (0, 0)");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You don't have permissions from the clan leader to do that.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void kickPlayer(Player player, String toBeKicked, String playerName) {
        String clanName = sqlget.getClan(player);
        if (sqlget.playerInClan(player)) {
            List<String> allPlayersInClan = sqlget.getAllClanMembersUUID(clanName);
            if (allPlayersInClan.contains(toBeKicked)) {
                if (sqlget.getPlayerRank(player) > sqlget.getPlayerRank(toBeKicked)) {
                    try {
                        PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM clan_members WHERE uuid=?");
                        statement.setString(1, toBeKicked);
                        statement.executeUpdate();

                        utils.messageToClan(clanName, plugin.clanAnnouncementPrefix + plugin.clanAnnouncement + "The player " + plugin.clanAnnouncementHighlight + playerName + plugin.clanAnnouncement + " was kicked from your clan by " + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + ".");

                        for (Player kicked : Bukkit.getOnlinePlayers()) {
                            if (kicked.getUniqueId().toString().equals(toBeKicked))
                                kicked.sendMessage(plugin.getPrefix() + plugin.selfBad + "You have been kicked from " + plugin.selfBadHighlight + clanName + ".");
                        }
                        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully kicked " + playerName + " from the clan.");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to kick player from clan. Error Code 10.2"); // Error Code 10
                        player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot kick that player");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "That player is not in your clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void disbandClan(Player player) { // TODO: Remove all pending invites from the disbanded faction
        if (sqlget.playerInClan(player)) {
            if (sqlget.getPlayerRank(player) == 3) {
                try {
                    String clan = sqlget.getClan(player);
                    PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM clans WHERE name=?");
                    statement.setString(1, clan);

                    List<String> clanMembers = sqlget.getAllClanMembersUUID(clan);
                    for (String clanMember : clanMembers) {
                        PreparedStatement remove = plugin.getConnection().prepareStatement("DELETE FROM clan_members WHERE uuid=?");
                        remove.setString(1, clanMember);
                        remove.executeUpdate();
                        utils.messageToClan(clan, plugin.clanAnnouncementPrefix + plugin.clanAnnouncementBad + "Your clan was disbanded.", player);
                    }

                    player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Clan has been disbanded.");
                    Bukkit.broadcastMessage(plugin.getPrefixInitials() + plugin.announcement + "The clan " + plugin.announcementHighlight + clan + plugin.announcement + " has been disbanded.");

                    statement.executeUpdate();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to delete clan. Error Code 9"); // Error Code 9
                    player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You are not the leader of the clan.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void playerLeaveClan(Player player) {
        if (sqlget.playerInClan(player)) {
            if (sqlget.getPlayerRank(player) != 3) {
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM clan_members WHERE uuid=?");
                    statement.setString(1, player.getUniqueId().toString());

                    utils.messageToClan(sqlget.getClan(player), plugin.clanAnnouncementPrefix + plugin.clanAnnouncementHighlight + player.getName() + plugin.clanAnnouncement + " has left your clan.", player);

                    player.sendMessage(plugin.getPrefix() + plugin.selfGood + "You have left your clan.");
                    statement.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to remove player from clan. Error Code 8"); // Error Code 8
                    player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                }
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.error + "You cannot do this as a clan leader.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are not in a clan.");
        }
    }

    public void addPlayer(Player player) {
        if (!sqlget.playerExists(player)) {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO players (uuid, name, suffix) VALUE (?,?,?)");
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getName());
                statement.setString(3, "NONE,FILLER,FILLER");
                statement.executeUpdate();

                Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + ChatColor.GREEN + player.getName() + " has been inserted into the database.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to insert player into database. Error Code 4"); // Error Code 4
                player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
            }
        }
    }

    public void createClan(Player player, String clanName) {
        if (!sqlget.playerInClan(player)) {
            if (clanName.length() <= plugin.getConfig().getInt("clans.max-characters")) {
                if (clanName.length() >= plugin.getConfig().getInt("clans.min-characters")) {
                    if (!sqlget.clanExists(clanName)) {
                        if (utils.hasSpecialCharacter(clanName) && !utils.hasBannedWords(clanName)) {
                            try {
                                PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO clans (name, base, date, kills, deaths) VALUE (?,?,?,?,?)");
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = new Date();
                                insert.setString(1, clanName);
                                insert.setString(2, "None");
                                insert.setString(3, formatter.format(date));
                                insert.setInt(4, 0);
                                insert.setInt(5, 0);

                                PreparedStatement insert2 = plugin.getConnection().prepareStatement("INSERT INTO clan_members (uuid, clan, rank) VALUE (?,?,?)");
                                insert2.setString(1, player.getUniqueId().toString());
                                insert2.setString(2, clanName);
                                insert2.setInt(3, 3);

                                insert.executeUpdate();
                                insert2.executeUpdate();

                                Bukkit.broadcastMessage(plugin.getPrefixInitials() + plugin.announcement + "The clan " + plugin.announcementHighlight + clanName + plugin.announcement + " has been created by " + plugin.announcementHighlight + player.getName());

                            } catch (SQLException e) {
                                e.printStackTrace();
                                Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to insert clan and clan member into database. Error Code: 0"); // Error Code 0
                                player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
                            }
                        } else
                            player.sendMessage(plugin.getPrefix() + plugin.error + "Clan names cannot contain any special characters.");
                    } else
                        player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name already exists.");
                } else
                    player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name does not have enough characters. Character minimum limit: " + plugin.getConfig().getInt("clans.min-characters") + " characters.");
            } else
                player.sendMessage(plugin.getPrefix() + plugin.error + "That clan name exceeds the character limit. Character limit: " + plugin.getConfig().getInt("clans.max-characters") + " characters.");
        } else
            player.sendMessage(plugin.getPrefix() + plugin.error + "You are already in a clan.");
    }
}
