package zonnic.land.zonnicclans.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLGetters;
import zonnic.land.zonnicclans.utilities.mysqlutilities.SQLSetters;

public class JoinLeaveListener implements Listener {
    SQLSetters sqlset = new SQLSetters();
    SQLGetters sqlget= new SQLGetters();
    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        sqlset.addPlayer(e.getPlayer());
        String playerSuffix = sqlget.getPlayerSuffix(e.getPlayer());
        if (!playerSuffix.equals("NONE,FILLER")) {
            ChatListener.playerSuffixes.put(e.getPlayer(), playerSuffix);
        }
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e) {
        String playerSuffix = sqlget.getPlayerSuffix(e.getPlayer());
        if (!playerSuffix.equals("NONE,FILLER")) {
            ChatListener.playerSuffixes.remove(e.getPlayer());
        }
    }

}
