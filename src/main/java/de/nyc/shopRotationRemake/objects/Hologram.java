package de.nyc.shopRotationRemake.objects;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
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
    private HologramStyle hologramStyle;
    private String progressBarTitle;

    private ArmorStand hologramIItemArmorStand;

    private ArmorStand hologramINArmorStand;
    private ArmorStand hologramINItemArmorStand;

    private ArmorStand hologramINPArmorStand;
    private ArmorStand hologramINPItemArmorStand;
    private ArmorStand hologramINPProgressArmorStand;

    private ArmorStand hologramNPArmorStand;
    private ArmorStand hologramNPProgressArmorStand;

    private ArmorStand hologramIPItemArmorStand;
    private ArmorStand hologramIPProgressArmorStand;

    private Item displayItem;

    private BukkitTask checkLivingTask;
    private boolean isShown = false;
    private boolean isDestroyed = false;

    public Hologram(Location location, String title, ItemStack item, Main main, HologramStyle hologramStyle, String progressBarTitle) {
        this.location = location;
        this.title = Utils.setColorInMessage(title);
        this.item = item;
        this.main = main;
        this.hologramStyle = hologramStyle;
        this.progressBarTitle = progressBarTitle;

        checkLivingTask = new BukkitRunnable() {
            public void run() {
                if(!isShown) return;
                switch (hologramStyle) {
                    case HOLOGRAM_ITEM -> {
                        if (displayItem.isDead() ||hologramIItemArmorStand.isDead()) {
                            kill();
                            create();
                        }
                    }
                    case HOLOGRAM_ITEM_NAME -> {
                        if (hologramINArmorStand.isDead() || displayItem.isDead() ||hologramINItemArmorStand.isDead()) {
                            kill();
                            create();
                        }
                    }
                    case HOLOGRAM_ITEM_NAME_PROGRESS -> {
                        if (hologramINPArmorStand.isDead() || displayItem.isDead() || hologramINPItemArmorStand.isDead() || hologramINPProgressArmorStand.isDead()) {
                            kill();
                            create();
                        }
                    }
                    case HOLOGRAM_NAME_PROGRESS -> {
                        if (hologramNPArmorStand.isDead() || hologramNPProgressArmorStand.isDead()) {
                            kill();
                            create();
                        }
                    }
                    case HOLOGRAM_ITEM_PROGRESS -> {
                        if (hologramIPProgressArmorStand.isDead() || displayItem.isDead() || hologramIPItemArmorStand.isDead()) {
                            kill();
                            create();
                        }
                    }
                }
            }
        }.runTaskTimer(main, 0, 200);
    }

    public void create() {
        if (isDestroyed) {
            throw new HologramAlreadyDestroyedException();
        }
        switch (hologramStyle.getName()) {
            case "hologram_item" -> {
                //Item ArmorStand
                Location itemLocationArmorStand = location.clone().add(0.5, -1.2, 0.5);
                hologramIItemArmorStand = (ArmorStand) itemLocationArmorStand.getWorld().spawnEntity(itemLocationArmorStand, EntityType.ARMOR_STAND);
                hologramIItemArmorStand.setVisible(false);
                hologramIItemArmorStand.setGravity(false);
                hologramIItemArmorStand.setCustomNameVisible(false);
                hologramIItemArmorStand.setInvulnerable(true);
                hologramIItemArmorStand.setCanPickupItems(false);

                //DisplayItem
                Location itemLocation = location.clone().add(0.5, -1.2, 0.5);
                displayItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.ITEM);
                displayItem.setItemStack(item);
                displayItem.setGravity(false);
                displayItem.setPickupDelay(Integer.MAX_VALUE);
                displayItem.setTicksLived(Integer.MAX_VALUE);
                hologramIItemArmorStand.addPassenger(displayItem);
            }
            case "hologram_item_name" -> {
                //Name ArmorStand
                Location hologramNameLocation = location.clone().add(0.5, -0.65, 0.5);
                hologramINArmorStand = (ArmorStand) hologramNameLocation.getWorld().spawnEntity(hologramNameLocation, EntityType.ARMOR_STAND);
                hologramINArmorStand.setVisible(false);
                hologramINArmorStand.setCustomNameVisible(true);
                hologramINArmorStand.setGravity(false);
                hologramINArmorStand.setCustomName(title);
                hologramINArmorStand.setInvulnerable(true);
                hologramINArmorStand.setCanPickupItems(false);

                //Item ArmorStand
                Location itemLocationArmorStand = location.clone().add(0.5, -1.2, 0.5);
                hologramINItemArmorStand = (ArmorStand) itemLocationArmorStand.getWorld().spawnEntity(itemLocationArmorStand, EntityType.ARMOR_STAND);
                hologramINItemArmorStand.setVisible(false);
                hologramINItemArmorStand.setGravity(false);
                hologramINItemArmorStand.setCustomNameVisible(false);
                hologramINItemArmorStand.setInvulnerable(true);
                hologramINItemArmorStand.setCanPickupItems(false);

                //DisplayItem
                Location itemLocation = location.clone().add(0.5, -1.2, 0.5);
                displayItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.ITEM);
                displayItem.setItemStack(item);
                displayItem.setGravity(false);
                displayItem.setPickupDelay(Integer.MAX_VALUE);
                displayItem.setTicksLived(Integer.MAX_VALUE);
                hologramINItemArmorStand.addPassenger(displayItem);
            }
            case "hologram_item_name_progress" -> {
                //Name ArmorStand
                Location hologramNameLocation = location.clone().add(0.5, - 0.65, 0.5);
                hologramINPArmorStand = (ArmorStand) hologramNameLocation.getWorld().spawnEntity(hologramNameLocation, EntityType.ARMOR_STAND);
                hologramINPArmorStand.setVisible(false);
                hologramINPArmorStand.setCustomNameVisible(true);
                hologramINPArmorStand.setGravity(false);
                hologramINPArmorStand.setCustomName(title);
                hologramINPArmorStand.setInvulnerable(true);
                hologramINPArmorStand.setCanPickupItems(false);

                //Progress ArmorStand
                Location hologramProgressLocation = location.clone().add(0.5, -0.9, 0.5);
                hologramINPProgressArmorStand = (ArmorStand) hologramProgressLocation.getWorld().spawnEntity(hologramProgressLocation, EntityType.ARMOR_STAND);
                hologramINPProgressArmorStand.setVisible(false);
                hologramINPProgressArmorStand.setCustomNameVisible(true);
                hologramINPProgressArmorStand.setGravity(false);
                hologramINPProgressArmorStand.setCustomName(progressBarTitle);
                hologramINPProgressArmorStand.setInvulnerable(true);
                hologramINPProgressArmorStand.setCanPickupItems(false);

                //Item ArmorStand
                Location itemLocationArmorStand = location.clone().add(0.5, -1.2, 0.5);
                hologramINPItemArmorStand = (ArmorStand) itemLocationArmorStand.getWorld().spawnEntity(itemLocationArmorStand, EntityType.ARMOR_STAND);
                hologramINPItemArmorStand.setVisible(false);
                hologramINPItemArmorStand.setGravity(false);
                hologramINPItemArmorStand.setCustomNameVisible(false);
                hologramINPItemArmorStand.setInvulnerable(true);
                hologramINPItemArmorStand.setCanPickupItems(false);

                //DisplayItem
                Location itemLocation = location.clone().add(0.5, -1.2, 0.5);
                displayItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.ITEM);
                displayItem.setItemStack(item);
                displayItem.setGravity(false);
                displayItem.setPickupDelay(Integer.MAX_VALUE);
                displayItem.setTicksLived(Integer.MAX_VALUE);
                hologramINPItemArmorStand.addPassenger(displayItem);
            }
            case "hologram_name_progress" -> {
                //Name ArmorStand
                Location hologramNameLocation = location.clone().add(0.5, -0.65, 0.5);
                hologramNPArmorStand = (ArmorStand) hologramNameLocation.getWorld().spawnEntity(hologramNameLocation, EntityType.ARMOR_STAND);
                hologramNPArmorStand.setVisible(false);
                hologramNPArmorStand.setCustomNameVisible(true);
                hologramNPArmorStand.setGravity(false);
                hologramNPArmorStand.setCustomName(title);
                hologramNPArmorStand.setInvulnerable(true);
                hologramNPArmorStand.setCanPickupItems(false);

                //Progress ArmorStand
                Location hologramProgressLocation = location.clone().add(0.5, -0.9, 0.5);
                hologramNPProgressArmorStand = (ArmorStand) hologramProgressLocation.getWorld().spawnEntity(hologramProgressLocation, EntityType.ARMOR_STAND);
                hologramNPProgressArmorStand.setVisible(false);
                hologramNPProgressArmorStand.setCustomNameVisible(true);
                hologramNPProgressArmorStand.setGravity(false);
                hologramNPProgressArmorStand.setCustomName(progressBarTitle);
                hologramNPProgressArmorStand.setInvulnerable(true);
                hologramNPProgressArmorStand.setCanPickupItems(false);
            }
            case "hologram_item_progress" -> {
                //Progress ArmorStand
                Location hologramProgressLocation = location.clone().add(0.5, -0.9, 0.5);
                hologramIPProgressArmorStand = (ArmorStand) hologramProgressLocation.getWorld().spawnEntity(hologramProgressLocation, EntityType.ARMOR_STAND);
                hologramIPProgressArmorStand.setVisible(false);
                hologramIPProgressArmorStand.setCustomNameVisible(true);
                hologramIPProgressArmorStand.setGravity(false);
                hologramIPProgressArmorStand.setCustomName(progressBarTitle);
                hologramIPProgressArmorStand.setInvulnerable(true);
                hologramIPProgressArmorStand.setCanPickupItems(false);

                //Item ArmorStand
                Location itemLocationArmorStand = location.clone().add(0.5, -1.2, 0.5);
                hologramIPItemArmorStand = (ArmorStand) itemLocationArmorStand.getWorld().spawnEntity(itemLocationArmorStand, EntityType.ARMOR_STAND);
                hologramIPItemArmorStand.setVisible(false);
                hologramIPItemArmorStand.setGravity(false);
                hologramIPItemArmorStand.setCustomNameVisible(false);
                hologramIPItemArmorStand.setInvulnerable(true);
                hologramIPItemArmorStand.setCanPickupItems(false);

                //DisplayItem
                Location itemLocation = location.clone().add(0.5, -1.2, 0.5);
                displayItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.ITEM);
                displayItem.setItemStack(item);
                displayItem.setGravity(false);
                displayItem.setPickupDelay(Integer.MAX_VALUE);
                displayItem.setTicksLived(Integer.MAX_VALUE);
                hologramIPItemArmorStand.addPassenger(displayItem);
            }
        }

        isShown = true;
    }

    public void kill() {
        switch (hologramStyle) {
            case HOLOGRAM_ITEM -> {
                displayItem.setInvulnerable(false);
                displayItem.remove();

                hologramIItemArmorStand.setInvulnerable(false);
                hologramIItemArmorStand.removePassenger(displayItem);
                hologramIItemArmorStand.remove();
            }
            case HOLOGRAM_ITEM_NAME -> {
                hologramINArmorStand.setInvulnerable(false);
                hologramINArmorStand.remove();

                displayItem.setInvulnerable(false);
                displayItem.remove();

                hologramINItemArmorStand.setInvulnerable(false);
                hologramINItemArmorStand.removePassenger(displayItem);
                hologramINItemArmorStand.remove();
            }
            case HOLOGRAM_ITEM_NAME_PROGRESS -> {
                hologramINPArmorStand.setInvulnerable(false);
                hologramINPArmorStand.remove();

                displayItem.setInvulnerable(false);
                displayItem.remove();

                hologramINPItemArmorStand.setInvulnerable(false);
                hologramINPItemArmorStand.removePassenger(displayItem);
                hologramINPItemArmorStand.remove();

                hologramINPProgressArmorStand.setInvulnerable(false);
                hologramINPProgressArmorStand.remove();
            }
            case HOLOGRAM_NAME_PROGRESS -> {
                hologramNPArmorStand.setInvulnerable(false);
                hologramNPArmorStand.remove();

                hologramNPProgressArmorStand.setInvulnerable(false);
                hologramNPProgressArmorStand.remove();
            }
            case HOLOGRAM_ITEM_PROGRESS -> {
                hologramIPProgressArmorStand.setInvulnerable(false);
                hologramIPProgressArmorStand.remove();

                displayItem.setInvulnerable(false);
                displayItem.remove();

                hologramIPItemArmorStand.setInvulnerable(false);
                hologramIPItemArmorStand.removePassenger(displayItem);
                hologramIPItemArmorStand.remove();
            }
        }
        isShown = false;
    }

    public void destroy() {
        kill();
        checkLivingTask.cancel();
        isDestroyed = true;
    }

    public void updateHologram(String title, ItemStack item, String progressBarTitle) {
        switch (hologramStyle) {
            case HOLOGRAM_ITEM -> {
                displayItem.setItemStack(item);
            }
            case HOLOGRAM_ITEM_NAME -> {
                displayItem.setItemStack(item);
                hologramINArmorStand.setCustomName(Utils.setColorInMessage(title));
            }
            case HOLOGRAM_ITEM_NAME_PROGRESS -> {
                displayItem.setItemStack(item);
                hologramINPArmorStand.setCustomName(Utils.setColorInMessage(title));
                hologramINPProgressArmorStand.setCustomName(Utils.setColorInMessage(progressBarTitle));
            }
            case HOLOGRAM_NAME_PROGRESS -> {
                hologramNPArmorStand.setCustomName(Utils.setColorInMessage(title));
                hologramNPProgressArmorStand.setCustomName(Utils.setColorInMessage(progressBarTitle));
            }
            case HOLOGRAM_ITEM_PROGRESS -> {
                hologramIPProgressArmorStand.setCustomName(Utils.setColorInMessage(progressBarTitle));
                displayItem.setItemStack(item);
            }
        }
    }
}
