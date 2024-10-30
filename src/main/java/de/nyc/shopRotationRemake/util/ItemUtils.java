package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemUtils {

    private final Main main;

    public ItemUtils(Main main) {
        this.main = main;
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
}
