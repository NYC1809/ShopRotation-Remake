package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class InventoryManager implements Listener {

    private static Main main;

    public InventoryManager(Main main) {
        InventoryManager.main = main;
    }

    public static void createDefaultInventory(Player player, UUID uuid, String name) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.setColorInMessage(name));
        player.openInventory(inventory);

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(name));
        for(int i=0; i<9; i++) {
            gui.set(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").getItem(), event -> {
                event.setCancelled(true);
            });
        }
        gui.show(player);
    }
}
