package org.tomkayrp.tkrpgproximitychat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tomkayrp.tkrpgproximitychat.TKRPGProximityChat;
import org.tomkayrp.tkrpgproximitychat.Utils;

public class ChatListener implements Listener {
    @EventHandler
    public static void chatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        String firstChar = String.valueOf(message.charAt(0));
        String starterSC = TKRPGProximityChat.plugin.getConfig().getString("staff_chat_starter");
        boolean cancelSC = TKRPGProximityChat.plugin.getConfig().getBoolean("staff_chat_canceller");
        if (!(p.hasPermission("tkproximitychat.staffchatbypass")) || (p.hasPermission("tkproximitychat.staffchatbypass") && !firstChar.equals(starterSC) && cancelSC)) {
            e.setCancelled(true);
            Utils.chatHandler(p, message);
        }
    }
}
