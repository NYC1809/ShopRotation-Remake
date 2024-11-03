package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static ItemStack convertToItemStack(String input) {
        return ItemBuilder.of(Material.BARRIER).name("Default Name").description("Default Description").asItem();
//DEBUG
    }
/*
    public static String convertItemStackToString(Material material, String name, String... lore) {
        return String.valueOf(ItemBuilder.of(material).name(name).description(lore).asItem());
    }
*/
    public static boolean isNumeric(String input) {
        return input != null && input.matches("\\d+");
    }

    public static boolean isMaterial(String input) {
        String value = input.substring(input.lastIndexOf(".") + 1);

        for (Material material : Material.values()) {
            if (material == Material.getMaterial(value)) {
                return true;
            }
        }
        return false;
    }

    public static List<Material> getItemList() {
        return new ArrayList<>(Arrays.asList(Material.values()));
    }

    public static List<Material> getBlockList() {
        List<Material> blocks = new ArrayList<>();
        for(Material material : Material.values()) {
            if (material.isBlock()) {
                if(!material.isAir()) {
                    blocks.add(material);
                }
            }
        }
        return blocks;
    }

    public static boolean isEnchantmentInt(Integer input) {
        return input > 0 && input <= 255;
    }

    public static String createTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm]");
        return ZonedDateTime.now().format(dateTimeFormatter);
    }

    public static boolean isValidBlock(String input) { //example input: "Material.STONE"
        List<Material> blocks = new ArrayList<>();

        for(Material material : Material.values()) {
            if(material.isBlock()) {
                if(!material.isAir()) {
                    blocks.add(material);
                }
            }
        }
        String value = input.substring(input.lastIndexOf(".") + 1);
        return blocks.contains(Material.getMaterial(value));
    }

    public static Material getBlockType(String argument) {
        String value = argument.substring(argument.lastIndexOf(".") + 1);
        return Material.getMaterial(value);
    }
}
