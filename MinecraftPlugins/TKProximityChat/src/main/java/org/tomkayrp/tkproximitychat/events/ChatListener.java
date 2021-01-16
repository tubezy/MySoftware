package org.tomkayrp.tkproximitychat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tomkayrp.tkproximitychat.TKProximityChat;
import org.tomkayrp.tkproximitychat.Utils;
import org.tomkayrp.tkproximitychat.commands.ProxyCommand;

import java.util.Objects;

public class ChatListener implements Listener {
    @EventHandler
    public static void chatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        String firstChar = String.valueOf(message.charAt(0));
        String starterSC = TKProximityChat.plugin.getConfig().getString("staff_chat_starter");
        boolean cancelSC = TKProximityChat.plugin.getConfig().getBoolean("staff_chat_canceller");
        if (ProxyCommand.proxyOn.contains(p.getUniqueId().toString())) {
            if (!(p.hasPermission("tkproximitychat.staffchatbypass")) || (p.hasPermission("tkproximitychat.staffchatbypass") && !firstChar.equals(starterSC) && cancelSC)) {
                e.setCancelled(true);
                Utils.chatHandler(p, message);
            } else {
                ProxyCommand.proxyOn.remove(p.getUniqueId().toString());
                Utils.colorConfig(p, "chat.disable.proximity_chat_disable_sc_message");
            }
        } else {
            for (Object pl : e.getRecipients()) {
                if (pl instanceof Player) {
                    Player pla = Objects.requireNonNull(((Player) pl).getPlayer()).getPlayer();
                    if (pla != null) {
                        if (ProxyCommand.proxyOn.contains(String.valueOf(pl.hashCode()))) {
                            e.getRecipients().remove(pla);
                        }
                    }
                }
            }
        }
    }
}
