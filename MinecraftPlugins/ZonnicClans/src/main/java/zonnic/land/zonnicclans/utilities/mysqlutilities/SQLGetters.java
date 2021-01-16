package zonnic.land.zonnicclans.utilities.mysqlutilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zonnic.land.zonnicclans.ZonnicClans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGetters {

    ZonnicClans plugin = ZonnicClans.getPlugin();

    public String getPlayerSuffix(Player player) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
            statement.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getString("suffix");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "NONE,FILLER";
    }

    public boolean clanHasHome(String clan) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            statement.setString(1, clan);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if (rs.getString("base") != null) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static List<String> getTopClans() {
        List<String> topClans = new ArrayList<>();
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("SELECT * FROM clans ORDER BY kills DESC LIMIT 10");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                topClans.add(rs.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return topClans;
    }

    public static List<String> getAllClans() {
        List<String> allClans = new ArrayList<>();
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("SELECT * FROM clans");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) allClans.add(rs.getString("name"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allClans;
    }

    public static int getClanKills(String clan) {
        int clanKills = 0;
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            statement.setString(1, clan);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) clanKills = rs.getInt("kills");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return clanKills;
    }

    public static int getClanDeaths(String clan) {
        int clanDeaths = 0;
        try {
            PreparedStatement statement = ZonnicClans.plugin.getConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            statement.setString(1, clan);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) clanDeaths = rs.getInt("deaths");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return clanDeaths;
    }

    public String getDateCreated(String clanName) {
        String dateCreated = "";
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            statement.setString(1, clanName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) dateCreated = rs.getString("date");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dateCreated;
    }

    public String getLeader(String clanName) {
        String leader = "";
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE clan=? AND rank=?");
            statement.setString(1, clanName);
            statement.setInt(2, 3);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) leader = getPlayerName(rs.getString("uuid"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to get leader of clan. Error Code 22"); // Error Code 22
        }
        return leader;
    }

    public List<String> getAllCoLeaders(String clanName) {
        List<String> coleaders = new ArrayList<>();
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE clan=? AND rank=?");
            statement.setString(1, clanName);
            statement.setInt(2, 2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                coleaders.add(getPlayerName(rs.getString("uuid")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all clan members ranked as coleader. Error Code 21"); // Error Code 21
        }
        return coleaders;
    }

    public List<String> getAllModerators(String clanName) {
        List<String> moderators = new ArrayList<>();
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE clan=? AND rank=?");
            statement.setString(1, clanName);
            statement.setInt(2, 1);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                moderators.add(getPlayerName(rs.getString("uuid")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all clan members ranked as moderator. Error Code 22"); // Error Code 22
        }
        return moderators;
    }

    public List<String> getAllMembers(String clanName) {
        List<String> members = new ArrayList<>();
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE clan=? AND rank=?");
            statement.setString(1, clanName);
            statement.setInt(2, 0);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                members.add(getPlayerName(rs.getString("uuid")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all clan members ranked as member. Error Code 21"); // Error Code 21
        }
        return members;
    }

    public String getPlayerName(String uuid) {
        String name = "";
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) name = rs.getString("name");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get players name from uuid. Error Code 20"); // Error Code 20
        }
        return name;
    }

    public boolean clanExists(String clan) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            statement.setString(1, clan);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to check if clan exists. Error Code 15"); // Error Code 15
        }
        return false;
    }

    public String getPlayerUUID(String playerName) {
        String uuid = "null";
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM players WHERE name=?");
            statement.setString(1, playerName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("uuid");
                return uuid;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to get player uuid. Error Code 14"); // Error Code 14
        }
        return uuid;
    }

    public String getClan(Player player) {
        String clan = null;
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT clan FROM clan_members WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                clan = rs.getString("clan");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all members in clan. Error Code 7"); // Error Code 7
            player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
        }
        return clan;
    }

    public String getClan(String uuid) {
        String clan = null;
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT clan FROM clan_members WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                clan = rs.getString("clan");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all members in clan. Error Code 7"); // Error Code 7
        }
        return clan;
    }

    public List<String> getAllClanMembersUUID(String clan) {
        List<String> playersInClan = new ArrayList<>();
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE clan=?");
            statement.setString(1, clan);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                playersInClan.add(uuid);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed get all members in clan. Error Code 6"); // Error Code 6
        }
        return playersInClan;
    }

    public int getPlayerRank(Player player) { //0 = member, 1 = moderator, 2 = coleader 3 = leader
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) return rs.getInt("rank");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to get rank of player. Error Code 5.2"); // Error Code 5.2
            player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
        }
        return 0;
    }

    public int getPlayerRank(String playerUUID) { //0 = member, 1 = moderator, 2 = coleader 3 = leader
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE uuid=?");
            statement.setString(1, playerUUID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getInt("rank");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to get rank of player. Error Code 5"); // Error Code 5
        }
        return 0;
    }

    public boolean playerExists(Player player) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to check if player exists. Error Code 3.2"); // Error Code 3.2
            player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
        }
        return false;
    }

    public boolean playerExists(String name) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM players WHERE name=?");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to check if player exists. Error Code 3"); // Error Code 3
        }
        return false;
    }

    public boolean playerInClan(Player player) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM clan_members WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.error + "Failed to check if player is in clan. Error Code: 2"); // Error Code 2
            player.sendMessage(plugin.getPrefixInitials() + plugin.error + "There was an issue while attempting to execute your command, please contact a staff member.");
        }
        return false;
    }
}
