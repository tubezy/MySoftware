package zonnic.land.zonnicdiscord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discord")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(ChatColor.WHITE + "Join our discord using this link!");
                p.sendMessage(ChatColor.AQUA + ZonnicDiscord.plugin.getConfig().getString("discord-link"));
            }
        }
        return true;
    }
}
