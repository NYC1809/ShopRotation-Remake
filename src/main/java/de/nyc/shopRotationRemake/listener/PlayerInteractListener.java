package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final Main main;

    public PlayerInteractListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) throws SQLException {
        Player player = event.getPlayer();

        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();

        if(!this.main.getSrDatabase().locationExistsInDB(location)) {
            return;
        }

        UUID uuid = UUID.fromString(this.main.getSrDatabase().getChestByLocation(location));
        Bukkit.getLogger().info("[09:27:11] Interacted with srChest \"" + uuid + "\"");

        //TODO: Open the inventory of the chest with the right uuid
    }
}
