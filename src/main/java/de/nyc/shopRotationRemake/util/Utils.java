package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        Bukkit.getLogger().severe("[23:55:23] copiedToClipboard / " + player.getName() + " / " + uuid);
    }

    public static void coloredCopyToClipboard(Player player, String uuid) {
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
                        "\"}]");

        Bukkit.getLogger().severe("[23:55:23] copiedToClipboard / " + player.getName() + " / " + uuid);
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Shop" + ChatColor.GOLD + "Rotation" + ChatColor.DARK_GRAY + "] ";
    }


//    /tellraw player [{"clickEvent":{"action":"copy_to_clipboard","value":"uuid_here"},"color":"dark_blue","text":"TEST"}, {"clickEvent":{"action":"copy_to_clipboard","value":"uuid_here"},"color":"dark_blue","text":"TESTZWO"}]

//    /tellraw NYC_1809 [{"clickEvent":{"action":"copy_to_clipboard","value":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"},"color":"dark_gray","text":"["},
//                      {"clickEvent":{"action":"copy_to_clipboard","value":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"},"color":"yellow","text":"Shop"},
//                      {"clickEvent":{"action":"copy_to_clipboard","value":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"},"color":"gold","text":"Rotation"},
//                      {"clickEvent":{"action":"copy_to_clipboard","value":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"},"color":"dark_gray","text":"] "},
//                      {"clickEvent":{"action":"copy_to_clipboard","value":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"},"color":"green","text":"a56b6c74-6a80-47a5-b9fd-a08d8b0b0c04"}]
}
