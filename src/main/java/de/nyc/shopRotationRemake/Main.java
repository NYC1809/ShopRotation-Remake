package de.nyc.shopRotationRemake;

import de.nyc.shopRotationRemake.commands.CreateChestCommand;
import de.nyc.shopRotationRemake.database.SrDatabase;
import de.nyc.shopRotationRemake.listener.ChatListener;
import de.nyc.shopRotationRemake.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private SrDatabase srDatabase;
    private List<String> uuidList = new ArrayList<>();

    @Override
    public void onEnable() {
        LocalDateTime start = LocalDateTime.now();

        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            srDatabase = new SrDatabase(getDataFolder().getAbsolutePath() + "/srDatabase.db", this);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        registerCommand("srChest", new CreateChestCommand(this));
        registerTabCompleter("srChest", new CreateChestCommand(this));

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

    private void registerTabCompleter(String command, TabCompleter tabCompleter) {
        PluginCommand cmd = this.getCommand(command);
        if(cmd == null) {
            getLogger().severe("Tabcompleter for command " + command + " is null!");
            return;
        }
        cmd.setTabCompleter(tabCompleter);
    }

    public List<String> getUuidList() {
        return uuidList;
    }
}
