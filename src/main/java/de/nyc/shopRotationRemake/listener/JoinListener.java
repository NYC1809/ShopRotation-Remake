package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {

    private final Main main;

    public JoinListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        if(this.main.getSrDatabase().playerExists(player)) {
            return;
        }
        this.main.getSrDatabase().addPlayer(event.getPlayer());
    }
}
