package de.nyc.shopRotationRemake.objects;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.util.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class AnvilGUI implements Listener {

    private final Player player;
    private final String initialText;
    private static final Main main = Main.getInstance();

    private final String titleOfAnvil;
    private final UUID uuid;
    private final String name;
    //TODO: Remove Name from debug!

    public AnvilGUI(Player player, String initialText, String titleOfAnvil, UUID uuid, String name) {
        this.player = player;
        this.initialText = initialText;
        this.titleOfAnvil = titleOfAnvil;
        this.uuid = uuid;
        this.name = name;
        Bukkit.getPluginManager().registerEvents(this, main);
        openAnvil();
    }

    public void openAnvil() {
        Inventory anvilInventory = Bukkit.createInventory(null, InventoryType.ANVIL, titleOfAnvil);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(initialText);
        item.setItemMeta(meta);

        anvilInventory.setItem(0, item);

        player.openInventory(anvilInventory);
    }

    //TODO: Fix Eventhandler registration!

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SQLException {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getWhoClicked().equals(player)) return;

        if (event.getInventory() instanceof AnvilInventory) {
            Bukkit.getLogger().severe("[77:19:65] AnvilInventory-Click-Event-Registered!");
            if(event.getSlot() == 0 ||event.getSlot() == 1) {
                event.setCancelled(true);
            }
            if (event.getSlot() == 2) {
                ItemStack result = event.getCurrentItem();
                if (result != null && result.hasItemMeta()) {
                    if(Objects.requireNonNull(result.getItemMeta()).getDisplayName().isEmpty()) {
                        Bukkit.getLogger().severe("[23:19:65] Text is empty!");
                        event.setCancelled(true);
                    }
                    String enteredText = result.getItemMeta().getDisplayName();

                    player.sendMessage("You entered: " + enteredText);

                    event.getWhoClicked().closeInventory();
                    //TODO: Change when implemented:
                    InventoryManager.createAdminSettingsInventory(player, uuid, name);

                    InventoryCloseEvent.getHandlerList().unregister(this);
                    InventoryClickEvent.getHandlerList().unregister(this);
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            InventoryCloseEvent.getHandlerList().unregister(this);
            InventoryClickEvent.getHandlerList().unregister(this);
        }
    }
}

