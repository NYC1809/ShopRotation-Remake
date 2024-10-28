package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;

public class BlockBreakListener implements Listener {

    private final Main main;

    public BlockBreakListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onblockBreak(BlockBreakEvent event) throws SQLException {
        Player player = event.getPlayer();

        Location location = event.getBlock().getLocation();

        if(this.main.getSrDatabase().locationExistsInDB(location)) {
            event.setCancelled(true);
            player.sendMessage(Messages.CANNOT_BREAK_BLOCK.getMessage());
        }
    }
}
