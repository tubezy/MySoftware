package zonnic.land.zonnicclans.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import zonnic.land.zonnicclans.ZonnicClans;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;

import java.util.*;

public class Utils {
    public static HashMap<UUID, Integer> teleportingPlayers = new HashMap<>();
    ZonnicClans plugin = ZonnicClans.getPlugin();
    SQLGetters sqlget = new SQLGetters();

    public String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public ItemStack getPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.DARK_AQUA + player.getName() + "'s skull");
        playerHead.setItemMeta(meta);

        return playerHead;
    }

    public String removeBannedWords(String string) {
        List<String> bannedWords = plugin.getConfig().getStringList("clans.banned-words");
        List<String> splitString = Arrays.asList(string.split(" "));

        for (String stringAt : bannedWords) {
            for (int i = 0; i < splitString.size(); i++) {
                if (splitString.get(i).toLowerCase().equalsIgnoreCase(stringAt)) {
                    int censorAmount = stringAt.length();
                    StringBuilder censoredSB = new StringBuilder();
                    for (int j = 0; j < censorAmount; j++) censoredSB.append("*");
                    String censored = censoredSB.toString();
                    splitString.set(i, censored);
                }
            }
        }

        StringBuilder cleaned = new StringBuilder();
        for (String word : splitString) cleaned.append(word).append(" ");

        return cleaned.toString().trim();
    }

    public String removeColorTags(String string) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            String temp = String.valueOf(string.charAt(i));
            if (temp.equals("&")) {
                i++;
            } else {
                newString.append(temp);
            }
        }
        return newString.toString();
    }

    public int getAmountOfMembers(String clan) {
        List<String> allMembersInClan = sqlget.getAllClanMembersUUID(clan);
        return allMembersInClan.size();
    }

    public double getClanKDR(String clan) {
        int kills = SQLGetters.getClanKills(clan);
        double deaths = SQLGetters.getClanDeaths(clan);
        double kdr;
        if (kills == 0.0) {
            kdr = 0.0;
        } else {
            kdr = kills / deaths;
        }

        String decimalFormat = String.format("%.03f", kdr);

        return Double.parseDouble(decimalFormat);
    }

    public void returnTopClans(Player player) {
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Top 10 clans:");
        player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "-= Updates every " + plugin.getConfig().getInt("clans.update-cache") + " seconds =-");
        List<String> topClans = ZonnicClans.clanTopCache;
        List<String> checkedClans = new ArrayList<>();
        int numOn = 1;
        for (String current : topClans) {
            if (!checkedClans.contains(current)) {
                player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + numOn + ". " + ChatColor.GOLD + current + ChatColor.GRAY + " (Kills: " + ChatColor.RED + SQLGetters.getClanKills(current) + ChatColor.GRAY + ")");
                checkedClans.add(current);
            }
            numOn++;
        }
    }

    public void returnClanStats(Player player, String clanName) {
        player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Clan Name: " + ChatColor.YELLOW + clanName);
        player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "-= Updates every " + plugin.getConfig().getInt("clans.update-cache") + " seconds =-");
        if (ZonnicClans.clanKillsCache.get(clanName) != null) player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + ZonnicClans.clanKillsCache.get(clanName));
        else player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + "0");

        if (ZonnicClans.clanDeathsCache.get(clanName) != null) player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Deaths: " + ChatColor.WHITE + ZonnicClans.clanDeathsCache.get(clanName));
        else player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Deaths: " + ChatColor.WHITE + "0");

        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "KDR: " + ChatColor.WHITE + getClanKDR(clanName));
    }

    public boolean hasSpecialCharacter(String string) {
        List<String> specialCharacters = plugin.getConfig().getStringList("clans.banned-characters");
        List<Character> charactersInString = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) charactersInString.add(string.charAt(i));

        for (Character character : charactersInString) {
            String charString = String.valueOf(character);
            for (String stringAt : specialCharacters) {
                if (charString.equals(stringAt)) return false;
            }
        }
        return true;
    }

    public boolean hasBannedWords(String string) {
        List<String> specialCharacters = plugin.getConfig().getStringList("clans.banned-words");
        for (String stringAt : specialCharacters) {
            if (string.contains(stringAt)) return true;
        }
        return false;
    }

    public void messageToClan(String clan, String message) {
        List<String> clanMembers = sqlget.getAllClanMembersUUID(clan);
        for (String clanMember : clanMembers) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().toString().equals(clanMember)) {
                    if (player.isOnline()) player.sendMessage(message);
                }
            }
        }
    }

    public void messageToClan(String clan, String message, Player denyMessage) {
        List<String> clanMembers = sqlget.getAllClanMembersUUID(clan);
        for (String clanMember : clanMembers) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().toString().equals(clanMember)) {
                    if (denyMessage != player && player.isOnline()) player.sendMessage(message);
                }
            }
        }
    }

    public void returnClanMembers(Player player, String clanName) {
        List<String> membersList = sqlget.getAllMembers(clanName);
        List<String> moderatorsList = sqlget.getAllModerators(clanName);
        List<String> coleadersList = sqlget.getAllCoLeaders(clanName);

        String leader = sqlget.getLeader(clanName);
        String dateCreated = sqlget.getDateCreated(clanName);
        StringBuilder members = new StringBuilder();
        StringBuilder moderators = new StringBuilder();
        StringBuilder coleaders = new StringBuilder();

        if (!membersList.isEmpty()) {
            for (String member : membersList) members.append(member).append(", ");
            members.substring(0, members.length() - 2);
        } else members.append(ChatColor.GRAY).append("None.");

        if (!moderatorsList.isEmpty()) {
            for (String moderator : moderatorsList) moderators.append(moderator).append(", ");
            moderators.substring(0, moderators.length() - 2);
        } else moderators.append(ChatColor.GRAY).append("None.");

        if (!coleadersList.isEmpty()) {
            for (String coleader : coleadersList) coleaders.append(coleader).append(", ");
            coleaders.substring(0, coleaders.length() - 2);
        } else coleaders.append(ChatColor.GRAY).append("None.");

        player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Clan Name: " + ChatColor.YELLOW + clanName);
        player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Date Created: " + ChatColor.YELLOW + dateCreated);
        player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "=-------------------=");
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Leader: " + ChatColor.WHITE + leader);
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Co-Leaders: " + ChatColor.WHITE + coleaders);
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Moderators: " + ChatColor.WHITE + moderators);
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Members: " + ChatColor.WHITE + members);
    }
}