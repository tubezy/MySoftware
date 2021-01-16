package org.tomkayrp.tkproximitychat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tomkayrp.tkproximitychat.Utils;

public class PcCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pc")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String arg : args) {
                        sb.append(arg);
                        sb.append(" ");
                    }
                    String message = sb.toString();
                    Utils.chatHandler(p, message);

                } else {
                    Utils.colorConfig(p, "chat.other.error_message");
                }
            }
        }
        return true;
    }
}
