package de.nyc.shopRotationRemake.util;

import com.google.common.base.Preconditions;
import de.nyc.shopRotationRemake.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtils {

    private static final Main main = Main.getInstance();

    public static String createItemString(String name, Material material, Map<Enchantment, Integer> enchantmentMap, List<String> description) {
        if(enchantmentMap == null || enchantmentMap.isEmpty()) {
            if(description == null || description.isEmpty()) {
                return "{Material." +
                        material.toString() +
                        "," +
                        name +
                        "," +
                        "[]" +
                        ",Enchantments:" +
                        "NONE" +
                        "}";
            } else {
                String lore = loreToString(description);
                return "{Material." +
                        material.toString() +
                        "," +
                        name +
                        "," +
                        lore +
                        ",Enchantments:" +
                        "NONE" +
                        "}";
            }
        } else if(description == null || description.isEmpty()) {
            String enchantments = enchantmentsToString(enchantmentMap);
            return "{Material." +
                    material.toString() +
                    "," +
                    name +
                    "," +
                    "[]" +
                    ",Enchantments:" +
                    enchantments +
                    "}";
        }
        String enchantments = enchantmentsToString(enchantmentMap);
        String lore = loreToString(description);
        return "{Material." +
                material.toString() +
                "," +
                name +
                "," +
                lore +
                ",Enchantments:" +
                enchantments +
                "}";
    }

    private static String enchantmentsToString(Map<Enchantment, Integer> enchantments) {
        StringBuilder enchantmentString = new StringBuilder();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            enchantmentString.append(enchantment.getKey().getKey())
                    .append("-Level.")
                    .append(level)
                    .append(";");
        }
        return enchantmentString.toString().trim();
    }

    private static String loreToString(List<String> description) {
        StringBuilder loreString = new StringBuilder();

        loreString.append("[");
        for(String line : description) {
            loreString.append(line);
            loreString.append(";");
        }
        loreString.append("]");
        return loreString.toString();
    }

    public static String getItemName(String itemString) {

        Pattern pattern = Pattern.compile("Material\\.[A-Z_]+,([A-Za-z0-9_§öÖÄäÜü\\s]+)");
        Matcher matcher = pattern.matcher(itemString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static Material getItemMaterial(String itemString) {
        Pattern pattern = Pattern.compile("Material\\.([A-Z_]+)");
        Matcher matcher = pattern.matcher(itemString);

        if (matcher.find()) {
            String materialValue = matcher.group(1);
            return Material.getMaterial(materialValue);
        } else {
            return null;
        }
    }

    public static List<String> getItemDescription(String itemString) {
        Pattern pattern = Pattern.compile("\\[([^\\]]*)\\]");
        Matcher matcher = pattern.matcher(itemString);

        List<String> entries = new ArrayList<>();

        if (matcher.find()) {
            String listContent = matcher.group(1).trim();
            if (!listContent.isEmpty()) {
                for (String entry : listContent.split(";")) {
                    entries.add(entry.trim());
                }
                return entries;
            }
        }
        return null;
    }

    public static Map<Enchantment, Integer> getItemEnchantments(String itemString) {
        Pattern pattern = Pattern.compile("Enchantments:([^}]+)");
        Matcher matcher = pattern.matcher(itemString);

        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if (matcher.find()) {
            String enchantmentsContent = matcher.group(1).trim();

            if (enchantmentsContent.equals("NONE")) {
                return enchantments;
            }
            for (String enchantmentEntry : enchantmentsContent.split(";")) {
                String[] parts = enchantmentEntry.split("-Level\\.");

                if (parts.length == 2) {
                    String enchantmentName = parts[0].trim();
                    int level = Integer.parseInt(parts[1].trim());
                    Enchantment enchantment = getEnchantment(enchantmentName.toLowerCase());
                    enchantments.put(enchantment, level);
                }
            }
        }
        return enchantments;
    }

    public static Enchantment getEnchantment(String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        Enchantment enchantment = (Enchantment) Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return enchantment;
    }

    public static ItemStack createItemStack(Material material, String name, Map<Enchantment, Integer> enchantmentMap, List<String> description) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta != null) {
            if (name != null && !name.equals(String.valueOf(material)) && !name.isEmpty()) {
                itemMeta.setDisplayName(name);
            }
            if ((description != null) && !(description.isEmpty())) {
                itemMeta.setLore(description);
            }
            if ((enchantmentMap != null) && !(enchantmentMap.isEmpty())) {
                for (Map.Entry<Enchantment, Integer> entry : enchantmentMap.entrySet()) {
                    itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
            item.setItemMeta(itemMeta);
        }
        return fixJsonDisplayName(item);
    }

    public static boolean compareItemStacks(ItemStack itemStackA, ItemStack itemStackB) {
        if (itemStackA == null || itemStackB == null) {
            return false;
        }
        if (itemStackA == itemStackB) {
            return true;
        }

        if (itemStackA.getType() != itemStackB.getType()) {
            return false;
        }

        ItemMeta metaA = itemStackA.getItemMeta();
        ItemMeta metaB = itemStackB.getItemMeta();
        if (metaA == null || metaB == null) {
            return metaA == metaB;
        }

        String displayNameA = metaA.hasDisplayName() ? ChatColor.stripColor(metaA.getDisplayName()) : itemStackA.getType().name();
        String displayNameB = metaB.hasDisplayName() ? ChatColor.stripColor(metaB.getDisplayName()) : itemStackB.getType().name();
        if (!Objects.equals(displayNameA, displayNameB)) {
            return false;
        }

        List<String> normalizedLoreA = normalizeLore(metaA);
        List<String> normalizedLoreB = normalizeLore(metaB);
        if (!Objects.equals(normalizedLoreA, normalizedLoreB)) {
            return false;
        }

        Map<org.bukkit.enchantments.Enchantment, Integer> enchantsA = itemStackA.getEnchantments();
        Map<org.bukkit.enchantments.Enchantment, Integer> enchantsB = itemStackB.getEnchantments();
        return Objects.equals(enchantsA, enchantsB);
    }

    private static List<String> normalizeLore(ItemMeta meta) {
        List<String> lore = meta.getLore();
        if (lore == null) {
            return Collections.emptyList();
        }
        return lore.stream()
                .map(line -> ChatColor.stripColor(line).stripTrailing())
                .toList();
    }

    public static String createProgressBarString(Integer percentage, String progressBar) {
        if(percentage == 100) {
            return Utils.setColorInMessage("&a✔ 100&6% &6[&a||||||||||||||||||||||||||||||||||||||||||||||||||&6]");
        } else {
            return Utils.setColorInMessage("&c✖ &a" + percentage + "&6% " + progressBar);
        }
    }

    public static Integer giveItemsToPlayer(Player player, ItemStack itemStack, Integer amount) {
        Inventory inventory = player.getInventory();
        int maxStackSize = itemStack.getMaxStackSize();
        int remaining = amount;

        while (remaining > 0) {
            int currentStackAmount = Math.min(maxStackSize, remaining);

            ItemStack stackToGive = itemStack.clone();
            stackToGive.setAmount(currentStackAmount);

            HashMap<Integer, ItemStack> leftovers = inventory.addItem(stackToGive);

            if (!leftovers.isEmpty()) {
                int leftoverAmount = leftovers.values().stream()
                        .mapToInt(ItemStack::getAmount)
                        .sum();
                remaining -= (currentStackAmount - leftoverAmount);
                break;
            } else {
                remaining -= currentStackAmount;
            }
        }

        return remaining;
    }

    //TODO: Bugfixing the displayName of the item
    //Version 1: ItemStack{ANVIL x 51, UNSPECIFIC_META:{meta-type=UNSPECIFIC, display-name="ANVILLLII"}}
    //Version 2: ItemStack{ANVIL x 11, UNSPECIFIC_META:{meta-type=UNSPECIFIC, display-name={"text":"","extra":["ANVILLLII"]}}}
    //
    //The first one is the correct vanilla minecraft itemMeta style

    public static ItemStack fixJsonDisplayName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();

            if (isJsonDisplayName(displayName)) {
                String plainName = extractPlainText(displayName);
                meta.setDisplayName(plainName);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    private static boolean isJsonDisplayName(String displayName) {
        return displayName.startsWith("{") && displayName.contains("\"extra\"");
    }

    private static String extractPlainText(String json) {
        try {
            int start = json.indexOf("[\"") + 2;
            int end = json.indexOf("\"]", start);
            if (start >= 0 && end > start) {
                return json.substring(start, end);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return json;
    }
}
