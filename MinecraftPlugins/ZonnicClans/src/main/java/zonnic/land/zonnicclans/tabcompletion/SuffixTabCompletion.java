package zonnic.land.zonnicclans.tabcompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SuffixTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> sCommands = new ArrayList<>();
            sCommands.add("set"); sCommands.add("remove");
            return sCommands;
        }

        return null;
    }
}
