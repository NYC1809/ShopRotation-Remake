package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryManager {

    private final Main main;

    public InventoryManager(Main main) {
        this.main = main;
    }

    public static void createDefaultInventory(Player player, UUID uuid, String name) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.setColorInMessage(name));
        player.openInventory(inventory);

        ItemStack defaultItemGray = new ItemAPI(" ", Material.GRAY_STAINED_GLASS_PANE, 1).build();


        for(int i = 0; i<9; i++) {
            inventory.setItem(i, new ItemAPI(" ", Material.CYAN_STAINED_GLASS_PANE, 1).build());
        }
        for(int i = 45; i<54; i++) {
            if(i == 49) { break;}
            inventory.setItem(i, new ItemAPI(" ", Material.CYAN_STAINED_GLASS_PANE, 1).build());
        }
        for(int i = 9; i<28; i++) {
            if(i == 13) {break;}
            inventory.setItem(i, defaultItemGray);
        }
        for(int i = 35; i<45; i++) {
            inventory.setItem(i, defaultItemGray);
        }
        //TODO: Names of below:
        inventory.setItem(10, new ItemAPI("History:", Material.OAK_HANGING_SIGN, 1).build());
        inventory.setItem(15, new ItemAPI("Klicke hier um deine Items abzugeben!", Material.HOPPER, 1).build());
        inventory.setItem(49, new ItemAPI("»HELP«", Material.NETHER_STAR, 1).build());

    }
}
