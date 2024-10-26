package de.nyc.shopRotationRemake.util;

import de.nyc.shopRotationRemake.Main;
import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

    //This class contains different utility functions
    //See functions below:
    private final Main main;

    public Utils(Main main) {
        this.main = main;
    }

    public static void copyToClipboard(Player player, String message, String uuid, Boolean prefix) {
        if(prefix) {
            Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "tellraw " + player.getName() +
                            " {\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                            uuid +
                            "\"},\"text\":\"" +
                            Utils.getPrefix() +
                            message +
                            "\"}");
            Bukkit.getLogger().info("[23:55:23] copiedToClipboard / " + player.getName() + " / " + uuid);
        } else {
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
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Shop" + ChatColor.GOLD + "Rotation" + ChatColor.DARK_GRAY + "] ";
    }
}
