package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.objects.CurrentItem;
import de.nyc.shopRotationRemake.objects.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HologramUtils {

    private static final Main main = Main.getInstance();

    public static void createHologram() {

        try {
            main.getSrDatabase().processAllChestUuids();
            if(main.getHologramMap() == null || main.getUuidList() == null) {
                return;
            }
            deleteHolograms();
            for(String uuid : main.getUuidList()) {
                Location location = main.getSrDatabase().getLocationOfChest(uuid);
                HologramStyle hologramStyle = HologramStyle.fromKey(main.getSrDatabase().getHologramStyle(UUID.fromString(uuid)));
                boolean holgramEnabled = main.getSrDatabase().getHologramEnabled(UUID.fromString(uuid));
                if(!holgramEnabled) {
                    continue;
                }

                boolean currentItemExists = CurrentItem.calculateCurrentItem(UUID.fromString(uuid));
                String name = main.getSrDatabase().getNameOfChest(UUID.fromString(uuid));

                if(!currentItemExists) {
                    ItemStack item = new ItemStack(Material.BARRIER);
                    //Create barrier hologram
                    Hologram hologram = new Hologram(location, name, item, main, hologramStyle, Utils.setColorInMessage("&c&l✖"));
                    hologram.create();
                    main.getHologramMap().put(UUID.fromString(uuid), hologram);
                    return;
                }

                String itemString = CurrentItem.getItemString(UUID.fromString(uuid));
                ItemStack item = new ItemStack(Objects.requireNonNull(ItemUtils.getItemMaterial(itemString)));
                Integer requiredAmount = CurrentItem.getRequiredAmount(UUID.fromString(uuid));
                Integer holdingAmount = CurrentItem.getHoldingAmount(UUID.fromString(uuid));

                Integer percentage = Utils.calculatePercentage(holdingAmount, requiredAmount);
                String progressBar = Utils.createProgressBar(percentage, 25);

                String progressBarTitle = Utils.setColorInMessage("&c✖ &a" + percentage + "&6% " + progressBar);

                Hologram hologram = new Hologram(location, name, item, main, hologramStyle, progressBarTitle);
                hologram.create();
                main.getHologramMap().put(UUID.fromString(uuid), hologram);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteHolograms() {
        if(main.getHologramMap() == null) {
            return;
        }
        for(Map.Entry<UUID, Hologram> entry : main.getHologramMap().entrySet()) {
            UUID uuid = entry.getKey();
            Hologram hologram = entry.getValue();
            hologram.destroy();
        }
    }

    public static void deleteSpecificHologram(UUID uuid) {
        if(main.getHologramMap() == null) {
            return;
        }
        for(Map.Entry<UUID, Hologram> entry : main.getHologramMap().entrySet()) {
            if(entry.getKey().equals(uuid)) {
                Hologram hologram = entry.getValue();
                hologram.destroy();
            }
        }
    }

    public static void updateSpecificHologram(UUID uuid) throws SQLException{
        if(main.getHologramMap() == null) {
            return;
        }
        for(Map.Entry<UUID, Hologram> entry : main.getHologramMap().entrySet()) {
            if(entry.getKey().equals(uuid)) {
                boolean currentItemExists = CurrentItem.calculateCurrentItem(uuid);
                String name = main.getSrDatabase().getNameOfChest(uuid);

                if(!currentItemExists) {
                    ItemStack item = new ItemStack(Material.BARRIER);
                    Hologram hologram = entry.getValue();
                    hologram.updateHologram(name, item, Utils.setColorInMessage("&c&l✖"));
                    return;
                }

                String itemString = CurrentItem.getItemString(uuid);
                ItemStack item = new ItemStack(Objects.requireNonNull(ItemUtils.getItemMaterial(itemString)));
                Integer requiredAmount = CurrentItem.getRequiredAmount(uuid);
                Integer holdingAmount = CurrentItem.getHoldingAmount(uuid);

                Integer percentage = Utils.calculatePercentage(holdingAmount, requiredAmount);
                String progressBar = Utils.createProgressBar(percentage, 25);

                String progressBarTitle = Utils.setColorInMessage("&c✖ &a" + percentage + "&6% " + progressBar);

                Hologram hologram = entry.getValue();
                hologram.updateHologram(name, item, progressBarTitle);
            }
        }
    }

}
