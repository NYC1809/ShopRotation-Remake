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
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class AnvilGUI implements Listener {

    private final Player player;
    private final String initialText;

    private final String titleOfAnvil;
    private final UUID uuid;
    private final String name;
    //TODO: Remove Name from debug!

    public AnvilGUI(Player player, String initialText, String titleOfAnvil, UUID uuid, String name, Plugin plugin) {
        this.player = player;
        this.initialText = initialText;
        this.titleOfAnvil = titleOfAnvil;
        this.uuid = uuid;
        this.name = name;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        if(!event.getView().getTitle().equals(titleOfAnvil)) return;

        if(event.getSlot() == 0 ||event.getSlot() == 1) {
            event.setCancelled(true);
            return;
        }
        if (event.getSlot() == 2) {
            if (event.getInventory() instanceof AnvilInventory) {
                if (event.getSlot() == 2) {
                    event.getWhoClicked().closeInventory();

                    InventoryManager.createAdminSettingsInventory(player, uuid, name);
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!event.getView().getPlayer().equals(player)) return;
        if(!event.getView().getTitle().equals(titleOfAnvil)) return;

        ItemStack result = event.getResult();
        if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
            String enteredText = result.getItemMeta().getDisplayName();
            Bukkit.getLogger().severe("[24:19:00] Entered Text: " + enteredText);
        } else {
            Bukkit.getLogger().severe("[23:65:55] Result item is null or has no display name!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            if(event.getView().getTitle().equals(titleOfAnvil)) {
                InventoryCloseEvent.getHandlerList().unregister(this);
                InventoryClickEvent.getHandlerList().unregister(this);
                PrepareAnvilEvent.getHandlerList().unregister(this);
            }
        }
    }

}

