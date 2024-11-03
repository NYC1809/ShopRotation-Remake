package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class ItemUtils {

    private static Main main;

    public ItemUtils(Main main) {
        ItemUtils.main = main;
    }

    public static String createStringA(String name, Material material, String... description) {
        return "{Material." +
                material.toString() +
                "," +
                name +
                "," +
                Arrays.toString(description) +
                ",Enchantment.NONE" +
                ",Level.NONE" +
                ",ItemFlag.NONE" +
                "}";
    }
    public static String createStringB(String name, Material material, Enchantment enchantment, int level, String... description) {
        if(!Utils.isEnchantmentInt(level)) {
            level = 1;
        }

        return "{Material." +
                material.toString() +
                "," +
                name +
                "," +
                Arrays.toString(description) +
                "," +
                enchantment.toString() +
                ",Level." +
                level +
                "}";
    }

    public static String createStringC(String name, Material material, Enchantment enchantment, int level, ItemFlag itemFlag, String... description) {
        if(!Utils.isEnchantmentInt(level)) {
            level = 1;
        }

        return "{Material." +
                material.toString() +
                "," +
                name +
                "," +
                Arrays.toString(description) +
                ",Enchantment." +
                enchantment.toString() +
                ",Level." +
                level +
                ",ItemFlag." +
                itemFlag.toString() +
                "}";
    }

    public static String createStringD(String name, Material material, ItemFlag itemFlag, String... description) {
        return "{Material." +
                material.toString() +
                "," +
                name +
                "," +
                Arrays.toString(description) +
                ",Enchantment.NONE" +
                ",Level.NONE" +
                ",ItemFlag." +
                itemFlag.toString() +
                "}";
    }

    public static String getItemName(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);
        itemString = itemString.substring(1, itemString.length() - 1);

        String[] parts = itemString.split(",", 6);

        String name = parts[1];
        Bukkit.getLogger().info("[89:90:25] getItemName Functions returns name: " + name);

        return name;
    }
}
