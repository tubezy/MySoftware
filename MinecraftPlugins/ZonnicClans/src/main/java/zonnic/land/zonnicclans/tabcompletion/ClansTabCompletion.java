package zonnic.land.zonnicclans.tabcompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ClansTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> cCommands = new ArrayList<>();
            //FIRST HELP PAGE
            cCommands.add("help"); cCommands.add("create"); cCommands.add("invite"); cCommands.add("uninvite"); cCommands.add("leave");

            //SECOND HELP PAGE
            cCommands.add("chat"); cCommands.add("togglechat"); cCommands.add("accept"); cCommands.add("deny"); cCommands.add("promote");

            //THIRD HELP PAGE
             cCommands.add("demote"); cCommands.add("kick"); cCommands.add("sethome"); cCommands.add("home"); cCommands.add("delhome");

             //FOURTH HELP PAGE
            cCommands.add("disband"); cCommands.add("rename"); cCommands.add("leader"); cCommands.add("who"); cCommands.add("stats");

            //FIFTH HELP PAGE
            cCommands.add("top");

            if (sender.hasPermission("zonnicclans.admin")) {
                //SIXTH HELP PAGE
                cCommands.add("forcerename"); cCommands.add("forcekick"); cCommands.add("forcejoin"); cCommands.add("forcesethome"); cCommands.add("forcedelhome");
                cCommands.add("reload"); cCommands.add("socialspy");
            }

            return cCommands;
        }

        return null;
    }
}
