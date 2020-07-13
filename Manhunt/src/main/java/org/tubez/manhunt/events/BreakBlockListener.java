package org.tubez.manhunt.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.tubez.manhunt.TubezMethods;
import org.tubez.manhunt.commands.ManhuntCommand;

public class BreakBlockListener implements Listener {
    @EventHandler
    public static void breakBlock (BlockBreakEvent e) {
        if (!ManhuntCommand.started) {
            e.setCancelled(true);
            TubezMethods.colorConfig(e.getPlayer(), "Events.Game_Not_Started");
        }
    }
}
