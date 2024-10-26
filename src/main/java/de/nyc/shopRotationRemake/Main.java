package de.nyc.shopRotationRemake;

import de.nyc.shopRotationRemake.commands.CreateChestCommand;
import de.nyc.shopRotationRemake.database.SrDatabase;
import de.nyc.shopRotationRemake.listener.ChatListener;
import de.nyc.shopRotationRemake.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Main extends JavaPlugin {

    private SrDatabase srDatabase;

    @Override
    public void onEnable() {
        LocalDateTime start = LocalDateTime.now();

        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            srDatabase = new SrDatabase(getDataFolder().getAbsolutePath() + "/srDatabase.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        registerCommand("srChest", new CreateChestCommand(this));

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        getLogger().info("loaded in " + Duration.between(start, LocalDateTime.now()).toMillis() + "ms");
    }

    @Override
    public void onDisable() {
        try {
            srDatabase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SrDatabase getSrDatabase() {
        return srDatabase;
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) {
            getLogger().severe("No entry for command " + command + " could be found in the plugin.yml.");
            return;
        }
        cmd.setExecutor(executor);
    }

    public void copyToClipboard(Player player, String message, String url) {
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "tellraw " + player.getName() +
                        " {\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" +
                        url +
                        "\"},\"text\":\"" +
                        message +
                        "\"}");
    }
}
