package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.util.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        if(!this.main.getSrDatabase().locationExistsInDB(location)) {
            return;
        }

        event.setCancelled(true);
        UUID uuid = UUID.fromString(this.main.getSrDatabase().getChestByLocation(location));
        String name = this.main.getSrDatabase().getNameOfChest(uuid);
        Bukkit.getLogger().info("[09:27:11] Player " + player.getName() + " interacted with srChest " + uuid);

        if (!this.main.getSrDatabase().chestIsEnabled(uuid.toString())) {
            player.sendMessage(Messages.CHEST_IS_DISABLED.getMessage());
            return;
        }

        InventoryManager.createDefaultInventory(player, uuid, name);

        //TODO: Inventory Manager^^
    }
}
