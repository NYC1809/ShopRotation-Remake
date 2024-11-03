package de.nyc.shopRotationRemake.util;

import com.google.common.base.Preconditions;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    private static final Main main = Main.getInstance();

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
        Bukkit.getLogger().info("[89:90:25] getItemName Function returned name: " + name);

        return name;
    }

    public static Material getItemMaterial(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);
        itemString = itemString.substring(1, itemString.length() - 1);

        String[] parts = itemString.split(",", 6);
        String material = parts[0].replace("Material.", "");
        Bukkit.getLogger().info("[22:91:25] getItemName Function returned material: " + material);

        return Material.getMaterial(material);
    }

    public static List<String> getItemDescription(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);
        List<String> itemDescriptionList = new ArrayList<>();
        itemString = itemString.substring(1, itemString.length() - 1);

        String[] parts = itemString.split(",", 6);

        String descriptionPart = parts[2].trim();

        descriptionPart = descriptionPart.substring(1, descriptionPart.length() - 1);

        String[] descriptionEntries = descriptionPart.split(",");
        Bukkit.getLogger().info("[10:55:09] getItemDescription Function returned Entries: " + Arrays.stream(descriptionEntries).toList());

        for (String descriptionEntry : descriptionEntries) {
            itemDescriptionList.add(descriptionEntry.trim());
        }
        return itemDescriptionList;
    }

    public static Enchantment getItemEnchantment(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);

        int enchantmentIndex = itemString.indexOf("Enchantment.");
        int start = enchantmentIndex + "Enchantment.".length();
        int end = itemString.indexOf(",", start);

        String enchantment = itemString.substring(start, end).trim();
        if(enchantment.equals("NONE")) {
            return null;
        }
        Bukkit.getLogger().warning("[DEBUG THIS!! [88:11:22] ENCHANTMENT RETURNED: " + getEnchantment(enchantment.toLowerCase()));
        return getEnchantment(enchantment.toLowerCase());

    }

    public static Integer getItemEnchantmentLevel(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);

        int levelIndex = itemString.indexOf("Level.");
        int start = levelIndex + "Level.".length();
        int end = itemString.indexOf(",", start);

        String levelValue = itemString.substring(start, end).trim();
        if (!Utils.isNumeric(levelValue)) {
            Bukkit.getLogger().warning("[00:01:11] WARNING: Levelvalue is not numeric!!");
            return null;
        }
        return Integer.valueOf(levelValue);
    }

    public static ItemFlag getItemFlag(UUID itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(itemUuid);

        int itemFlagIndex = itemString.indexOf("ItemFlag.");
        int start = itemFlagIndex + "ItemFlag.".length();
        int end = itemString.indexOf("}", start);

        String itemFlagValue = itemString.substring(start, end).trim();

        if(itemFlagValue.equals("NONE")) {
            return null;
        }
        Bukkit.getLogger().info("[02:22:67] getItemFlag Function returned ItemFlag: " + itemFlagValue);
        return ItemFlag.valueOf(itemFlagValue);
    }

    private static Enchantment getEnchantment(String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        Enchantment enchantment = (Enchantment) Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return enchantment;
    }

    public static ItemStack createItemStack(Material material, String name, Enchantment enchantment, Integer enchLevel, ItemFlag itemFlag, String... description) {
        if(enchantment == null) {
            if (itemFlag == null) {
                return ItemBuilder.of(material)
                        .name(name)
                        .description(description)
                        .asItem();
            } else {
                return ItemBuilder.of(material)
                        .name(name)
                        .description(description)
                        .addFlags(itemFlag)
                        .asItem();
            }
        }
        return ItemBuilder.of(material)
                .name(name)
                .description(description)
                .enchant(enchantment, enchLevel)
                .addFlags(itemFlag)
                .asItem();
    }
}
