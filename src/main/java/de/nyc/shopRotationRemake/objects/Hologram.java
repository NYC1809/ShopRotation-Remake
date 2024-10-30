package de.nyc.shopRotationRemake.objects;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.exceptions.HologramAlreadyDestroyedException;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Hologram {

    private Location location;
    private String title;
    private ItemStack item;
    private Main main;

    private ArmorStand armorStand;
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
                if (armorStand.isDead() || displayItem.isDead()) {
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

        Location holoLoc = location.clone().add(0.5, -0.2, 0.5);
        armorStand = (ArmorStand) holoLoc.getWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(title);
        armorStand.setInvulnerable(true);
        armorStand.setCanPickupItems(false);

        Location itemLoc = holoLoc.clone().add(0.0, -0.5, 0.0);
        displayItem = (Item) itemLoc.getWorld().spawnEntity(itemLoc, EntityType.ITEM);
        displayItem.setItemStack(item);
        displayItem.setGravity(false);
        displayItem.setPickupDelay(Integer.MAX_VALUE);
        displayItem.setTicksLived(Integer.MAX_VALUE);
        armorStand.addPassenger(displayItem);

        isShown = true;
    }

    public void kill() {
        armorStand.setInvulnerable(false);
        armorStand.removePassenger(displayItem);
        displayItem.setInvulnerable(false);
        displayItem.remove();
        armorStand.remove();

        isShown = false;
    }

    public void destroy() {
        kill();
        checkLivingTask.cancel();
        isDestroyed = true;
    }
}
