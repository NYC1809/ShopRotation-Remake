package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.objects.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.UUID;

public class HologramUtils {

    private static final Main main = Main.getInstance();

    public static void createHologram() {

        try {
            main.getSrDatabase().processAllChestUuids();
            if(main.getHologramList() == null || main.getUuidList() == null) {
                return;
            }
            deleteHolograms();
            for(String uuid : main.getUuidList()) {
                Location location = main.getSrDatabase().getLocationOfChest(uuid);
                String name = main.getSrDatabase().getNameOfChest(UUID.fromString(uuid));
                //TODO: Replace Barrier with currentitem when implemented

                Hologram hologram = new Hologram(location, name, ItemBuilder.of(Material.BARRIER).asItem(), main);
                hologram.create();
                main.getHologramList().add(hologram);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteHolograms() {
        if(main.getHologramList() == null || main.getUuidList() == null) {
            return;
        }
        for(Hologram hologram : main.getHologramList()) {
            hologram.destroy();
        }
    }
}
