package de.nyc.shopRotationRemake.objects;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.exceptions.HologramAlreadyDestroyedException;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class Hologram {

    private Location location;
    private String title;
    private ItemStack item;
    private Main main;

    private ArmorStand hologramArmorStand;
    private ArmorStand itemArmorStand;
    private Item displayItem;

    private BukkitTask checkLivingTask;
    private boolean isShown = false;
    private boolean isDestroyed = false;

    public Hologram(Location location, String title, ItemStack item, Main main) {
        this.location = location;
        this.title = Utils.setColorInMessage(title);
        this.item = item;
        this.main = main;

        checkLivingTask = new BukkitRunnable() {
            public void run() {
                if (!isShown) return;
                if (hologramArmorStand.isDead() || displayItem.isDead() ||itemArmorStand.isDead()) {
                    kill();
                    create();
                }
            }
        }.runTaskTimer(main, 0, 200);
    }

    public void create() {
        if (isDestroyed) {
            throw new HologramAlreadyDestroyedException();
        }

        //TODO: Set the correct style of the hologram

        Location hologramLocation = location.clone().add(0.5, -0.65, 0.5);
        hologramArmorStand = (ArmorStand) hologramLocation.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);
        hologramArmorStand.setVisible(false);
        hologramArmorStand.setCustomNameVisible(true);
        hologramArmorStand.setGravity(false);
        hologramArmorStand.setCustomName(title);
        hologramArmorStand.setInvulnerable(true);
        hologramArmorStand.setCanPickupItems(false);

        Location itemLocationArmorStand = location.clone().add(0.5, -1.2, 0.5);
        itemArmorStand = (ArmorStand) itemLocationArmorStand.getWorld().spawnEntity(itemLocationArmorStand, EntityType.ARMOR_STAND);
        itemArmorStand.setVisible(false);
        itemArmorStand.setGravity(false);
        itemArmorStand.setCustomNameVisible(false);
        itemArmorStand.setInvulnerable(true);
        itemArmorStand.setCanPickupItems(false);

        Location itemLocation = location.clone().add(0.5, -1.2, 0.5);
        displayItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.ITEM);
        displayItem.setItemStack(item);
        displayItem.setGravity(false);
        displayItem.setPickupDelay(Integer.MAX_VALUE);
        displayItem.setTicksLived(Integer.MAX_VALUE);
        itemArmorStand.addPassenger(displayItem);

        isShown = true;
    }

    public void kill() {
        hologramArmorStand.setInvulnerable(false);
        hologramArmorStand.remove();

        displayItem.setInvulnerable(false);
        displayItem.remove();

        itemArmorStand.setInvulnerable(false);
        itemArmorStand.removePassenger(displayItem);
        itemArmorStand.remove();

        isShown = false;
    }

    public void destroy() {
        kill();
        checkLivingTask.cancel();
        isDestroyed = true;
    }
}
