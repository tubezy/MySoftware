package zonnic.land.zonnicclans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import zonnic.land.zonnicclans.clancommands.ClanCommand;
import zonnic.land.zonnicclans.clancommands.QuickClanChat;
import zonnic.land.zonnicclans.events.*;
import zonnic.land.zonnicclans.tabcompletion.ClansTabCompletion;
import zonnic.land.zonnicclans.tabcompletion.SuffixTabCompletion;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLSetters;
import zonnic.land.zonnicclans.zonnicCS.SuffixCommand;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class ZonnicClans extends JavaPlugin {
    public static ZonnicClans plugin;
    FileConfiguration config = getConfig();
    public String suffixPluginPrefix = ChatColor.DARK_PURPLE + "[" + ChatColor.WHITE + "ZonnicCS" + ChatColor.DARK_PURPLE + "] ";
    public String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "ZClans" + ChatColor.DARK_GRAY + "] ";
    public String prefixInitials = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Z" + ChatColor.GRAY + "C" + ChatColor.DARK_GRAY + "] ";
    public String clanAnnouncementPrefix = ChatColor.WHITE + "[" + ChatColor.YELLOW + "Clan Announcement" + ChatColor.WHITE + "] ";
    public ChatColor announcementHighlight = ChatColor.WHITE;
    public ChatColor announcement = ChatColor.DARK_AQUA;
    public ChatColor clanAnnouncementHighlight = ChatColor.BLUE;
    public ChatColor clanAnnouncement = ChatColor.WHITE;
    public ChatColor error = ChatColor.RED;
    public ChatColor clanAnnouncementBad = ChatColor.LIGHT_PURPLE;
    public ChatColor clanAnnouncementBadHighlight = ChatColor.WHITE;
    public ChatColor selfBad = ChatColor.DARK_AQUA;
    public ChatColor selfBadHighlight = ChatColor.AQUA;
    public ChatColor selfGood = ChatColor.YELLOW;
    public ChatColor selfGoodHighlight = ChatColor.GOLD;

    //CACHE
    public static List<String> clanTopCache = new ArrayList<>();
    public static HashMap<String, Integer> clanKillsCache = new HashMap<>();
    public static HashMap<String, Integer> clanDeathsCache = new HashMap<>();

    public String version = "1.3";
    private Connection connection;
    public String host, database, username, password;
    public int port;

    @Override
    public void onEnable() {
        configSetup();
        MYSQLSetup();
        plugin = this;

        // Commands
        Objects.requireNonNull(this.getCommand("clan")).setExecutor(new ClanCommand());
        Objects.requireNonNull(this.getCommand("cc")).setExecutor(new QuickClanChat());
        Objects.requireNonNull(this.getCommand("suffix")).setExecutor(new SuffixCommand()); // SEPARATE FUNCTION

        // Tab Completion
        Objects.requireNonNull(this.getCommand("clan")).setTabCompleter(new ClansTabCompletion());
        Objects.requireNonNull(this.getCommand("suffix")).setTabCompleter(new SuffixTabCompletion());

        // Events
        this.getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        this.getServer().getPluginManager().registerEvents(new MoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new KillDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new AttackListener(), this);

        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "ZonnicClans v" + version + " has started! (by tubez)");


        // CACHING
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            clanTopCache = SQLGetters.getTopClans();
            for (String clan : SQLGetters.getAllClans()) {
                if (KillDeathListener.kills.containsKey(clan)) {
                    SQLSetters.incrementClanKills(clan, KillDeathListener.kills.get(clan));
                    System.out.println(KillDeathListener.kills.get(clan));

                    KillDeathListener.kills.remove(clan);
                    clanKillsCache.remove(clan);
                }

                if (KillDeathListener.deaths.containsKey(clan)) {
                    SQLSetters.incrementClanDeaths(clan, KillDeathListener.deaths.get(clan));
                    System.out.println(KillDeathListener.deaths.get(clan));

                    KillDeathListener.deaths.remove(clan);
                    clanDeathsCache.remove(clan);
                }

                clanKillsCache.put(clan, SQLGetters.getClanKills(clan));
                clanDeathsCache.put(clan, SQLGetters.getClanDeaths(clan));
            }

            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + plugin.selfGood + "All caches have been updated");
        }, 0L, (20L * plugin.getConfig().getInt("clans.update-cache")));
    }


    public void MYSQLSetup() {
        host = config.getString("mysql_data.host");
        database = config.getString("mysql_data.database");
        username = config.getString("mysql_data.username");
        password = config.getString("mysql_data.password");
        port = config.getInt("mysql_data.port");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                if (!getConfig().getBoolean("mysql_data.use-jdbc-connection-string")) {
                    setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                } else {
                    String jdbcConnectionConfig = getConfig().getString("mysql_data.jdbc-connection-string");
                    if (jdbcConnectionConfig != null) {
                        setConnection(DriverManager.getConnection(jdbcConnectionConfig));
                    } else {
                        Bukkit.getConsoleSender().sendMessage(prefix + plugin.error + "You enabled 'use-jdbc-connection-string' but did not provide the config with a string'");
                        getServer().getPluginManager().disablePlugin(plugin);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "MySQL Database has been connected successfully!");
                // CLANS TABLE: (clan name, base /clan home location, date created)
                // CLAN MEMBERS TABLE: (uuid of player, clan player is in, rank of player)
                // PLAYERS TABLE: (uuid of player, name of player, amount of kills, amount of deaths)
                try {
                    PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS clans (" +
                            "name varchar(100), " +
                            "base varchar(100), " +
                            "date varchar(100), "  +
                            "kills int(100), " +
                            "deaths int(100))");

                    PreparedStatement statement2 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS clan_members (" +
                            "uuid varchar(100), " +
                            "clan varchar(100), " +
                            "rank int(100))");

                    PreparedStatement statement3 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS players (" +
                            "uuid varchar(100), " +
                            "name varchar(100), " +
                            "suffix varchar(100))"); // SEPARATE FUNCTION

                    statement.executeUpdate();
                    statement2.executeUpdate();
                    statement3.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to created tables.");
                }
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + "An error has occurred while attempting to connect to MySQL. (Check your MySQL data in the config)");
        }
    }
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void configSetup() {
        config.options().copyDefaults(true);
        saveDefaultConfig();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixInitials() {
        return prefixInitials;
    }

    public static ZonnicClans getPlugin() {
        return plugin;
    }
}
