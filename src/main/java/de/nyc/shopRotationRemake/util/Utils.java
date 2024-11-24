package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {

    private final Main main;

    public Utils(Main main) {
        this.main = main;
    }

    public static void copyToClipboard(Player player, String message, String uuid) {
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "tellraw " + player.getName() +
                        " {\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"text\":\"" +
                        message +
                        "\"}");
        Bukkit.getLogger().info("[23:55:23] copiedToClipboard / " + player.getName() + " / " + uuid);
    }

    public static void coloredCopyToClipboard(Player player, String uuid, String name) {
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "tellraw " + player.getName() +
                        " [" +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"dark_gray\",\"text\":\"[\"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"yellow\",\"text\":\"Shop\"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"gold\",\"text\":\"Rotation\"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"dark_gray\",\"text\":\"] \"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"green\",\"text\":\"" +
                        uuid +
                        "\"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"dark_green\",\"text\":\" | Name: \"}," +
                        "{\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        uuid +
                        "\"},\"color\":\"yellow\",\"text\":\"" +
                        name +
                        "\"}]");

        Bukkit.getLogger().info("[23:55:23] pastedToCopyMSG / " + player.getName() + " / " + uuid);
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Shop" + ChatColor.GOLD + "Rotation" + ChatColor.DARK_GRAY + "] ";
    }

    public static BlockFace getFacingDirection(Location location) {
        float yaw = location.getYaw();
        if (yaw < 0) { yaw += 360; }

        if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    //example: Location{world=CraftWorld{name=world},x=32.0,y=69.0,z=27.0,pitch=0.0,yaw=0.0}
    public static Location stringToLocation(String input) {
        input = input.replace("Location{","").replace("}", "");
        String[] parts = input.split(",");

        String worldName = parts[0].split("=")[2];
        double x = Double.parseDouble(parts[1].split("=")[1]);
        double y = Double.parseDouble(parts[2].split("=")[1]);
        double z = Double.parseDouble(parts[3].split("=")[1]);
        float pitch = Float.parseFloat(parts[4].split("=")[1]);
        float yaw = Float.parseFloat(parts[5].split("=")[1]);

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            throw new IllegalArgumentException("Wold \"" + worldName + "\" doesnt exists!");
        }

        Bukkit.getLogger().info("[45:36:85] " + new Location(world, x, y, z, yaw, pitch));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String setColorInMessage(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> setColorInList(List<String> input) {
        List<String> reColored = new ArrayList<>();
        for(String string : input) {
            reColored.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return reColored;
    }

    public static boolean isNumeric(String input) {
        return input != null && input.matches("\\d+");
    }

    public static boolean isMaterial(String input) {
        String value = input.substring(input.lastIndexOf(".") + 1);

        for (Material material : Material.values()) {
            if (material == Material.getMaterial(value)) {
                return !isNonObtainable(material);
            }
        }
        return false;
    }

    public static List<Material> getItemList() {
        List<Material> obtainableItems = new ArrayList<>();
        for(Material material : Material.values()) {
            if(!isNonObtainable(material)) {
                obtainableItems.add(material);
            }
        }
        return obtainableItems;
    }

    public static List<Material> getBlockList() {
        List<Material> blocks = new ArrayList<>();
        for(Material material : Material.values()) {
            if (material.isBlock()) {
                if(!isNonObtainable(material)) {
                    blocks.add(material);
                }
            }
        }
        return blocks;
    }
/*
    public static boolean isEnchantmentInt(Integer input) {
        return input > 0 && input <= 255;
    }
*/

    public static String createTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm]");
        return ZonedDateTime.now().format(dateTimeFormatter);
    }

    public static String getTimeAgo(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(timestamp, formatter);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(parsedDateTime, now);

            long minutes = duration.toMinutes();
            long hours = duration.toHours();

            if (minutes < 60) {
                if(minutes == 0) {
                    return "(Gerade eben...)";
                } else if(minutes == 1) {
                    return "(Vor " + minutes + " Minute...)";
                } else {
                    return "(Vor " + minutes + " Minuten...)";
                }
            } else if (hours < 24) {
                if(hours == 1) {
                    return "(Vor " + hours + " Stunde...)";
                } else {
                    return "(Vor " + hours + " Stunden...)";
                }
            } else {
                return "(" + timestamp + ")";
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return "Invalid timestamp format.";
        }
    }

    public static boolean isValidBlock(String input) { //example input: "Material.STONE"
        List<Material> blocks = new ArrayList<>();

        for(Material material : Material.values()) {
            if(material.isBlock()) {
                if(!isNonObtainable(material)) {
                    blocks.add(material);
                }
            }
        }
        String value = input.substring(input.lastIndexOf(".") + 1);
        return blocks.contains(Material.getMaterial(value));
    }

    public static Material getMaterialType(String argument) {
        String value = argument.substring(argument.lastIndexOf(".") + 1);
        return Material.getMaterial(value);
    }

    private static boolean isNonObtainable(Material material) {
        return material == Material.AIR ||
                material == Material.CAVE_AIR ||
                material == Material.VOID_AIR ||
                material == Material.WATER ||
                material == Material.LAVA ||
                material == Material.BARRIER ||
                material == Material.STRUCTURE_VOID ||
                material == Material.STRUCTURE_BLOCK ||
                material == Material.COMMAND_BLOCK ||
                material == Material.COMMAND_BLOCK_MINECART ||
                material == Material.CHAIN_COMMAND_BLOCK ||
                material == Material.REPEATING_COMMAND_BLOCK ||
                material == Material.JIGSAW ||
                material == Material.DEBUG_STICK ||
                material == Material.LIGHT ||
                material == Material.FARMLAND ||
                material == Material.END_GATEWAY ||
                material == Material.END_PORTAL ||
                material == Material.END_PORTAL_FRAME ||
                material == Material.NETHER_PORTAL ||
                material == Material.MOVING_PISTON ||
                material == Material.PETRIFIED_OAK_SLAB ||
                material == Material.PISTON_HEAD ||
                material == Material.BUBBLE_COLUMN ||
                material == Material.FIRE ||
                material == Material.SOUL_FIRE ||
                material == Material.CAMPFIRE ||
                material == Material.KNOWLEDGE_BOOK;
    }

    public static boolean isEnchantment(String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        return Registry.ENCHANTMENT.get(namespacedKey) != null;
    }

    public static Integer calculatePercentage(Integer a, Integer b) {
        if(b == 0) {
            throw new IllegalArgumentException("The value of b cannot be zero.");
        }
        int percentage = (int) ((a / (double) b) * 100);
        return Math.min(percentage, 100);
    }

    public static Integer calculatePercentageAmount(Integer value, Integer percentage) {
        double output = ((value * percentage) / 100.0);
        return parseDoubleToInt(output);
    }

    private static int parseDoubleToInt(double number) {
        return (int) Math.ceil(number);
        //Used to round up and casted to int
        //To fix the problem having 0 items as limit when input too less itemLimit %
    }

    public static String createProgressBar(int percentage, int totalBars) {
        int coloredBars = (int) Math.round((percentage / 100.0) * totalBars);

        StringBuilder progressBar = new StringBuilder("&6[");

        progressBar.append("&a");
        for (int i = 0; i < coloredBars; i++) {
            progressBar.append("|");
        }

        progressBar.append("&7");
        for (int i = coloredBars; i < totalBars; i++) {
            progressBar.append("|");
        }

        progressBar.append("&6").append("]");

        return progressBar.toString();
    }

    public static void removeItemsFromInventory(Player player, String targetItemString, int amountToRemove) {
        ItemStack[] contents = player.getInventory().getContents();
        int remainingToRemove = amountToRemove;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null) {
                continue;
            }

            ItemMeta itemMeta = item.getItemMeta();
            String displayName;

            if(itemMeta.getDisplayName().isEmpty()) {
                displayName = item.getType().name();
            } else {
                displayName = itemMeta.getDisplayName();
            }
            String createItemString = ItemUtils.createItemString(displayName, item.getType(), item.getItemMeta().getEnchants(), item.getItemMeta().getLore());

            if(!createItemString.equals(targetItemString)) {
                continue;
            }

            int stackAmount = item.getAmount();

            if (stackAmount > remainingToRemove) {
                item.setAmount(stackAmount - remainingToRemove);
                break;
            } else {
                remainingToRemove -= stackAmount;
                contents[i] = null;
            }
        }
        player.getInventory().setContents(contents);
    }
}
