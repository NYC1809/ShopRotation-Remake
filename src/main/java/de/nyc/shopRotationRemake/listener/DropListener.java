package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DropListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = event.getItemDrop().getItemStack();

        Bukkit.getLogger().warning("[82:57:29--1] " + itemStack.toString());
    }
}
