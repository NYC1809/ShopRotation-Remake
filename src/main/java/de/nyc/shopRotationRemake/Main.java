package de.nyc.shopRotationRemake;

import de.leonheuer.mcguiapi.gui.GUIFactory;
import de.nyc.shopRotationRemake.commands.ChestCommand;
import de.nyc.shopRotationRemake.database.SrDatabase;
import de.nyc.shopRotationRemake.listener.BlockBreakListener;
import de.nyc.shopRotationRemake.listener.ChatListener;
import de.nyc.shopRotationRemake.listener.PlayerInteractListener;
import de.nyc.shopRotationRemake.objects.Hologram;
import de.nyc.shopRotationRemake.util.HologramUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private SrDatabase srDatabase;
    private final List<String> uuidList = new ArrayList<>();
    private final List<String> chestNames = new ArrayList<>();

    private final List<Hologram> hologramList = new ArrayList<>();
    private GUIFactory guiFactory;

    private static Main instance;

    @Override
    public void onEnable() {
        LocalDateTime start = LocalDateTime.now();
        instance = this;

        guiFactory = new GUIFactory(this);

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

        HologramUtils.createHologram();

        registerCommand("srChest", new ChestCommand(this));
        getCommand("srChest").setTabCompleter(new ChestCommand(this));

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);

        getLogger().info("loaded in " + Duration.between(start, LocalDateTime.now()).toMillis() + "ms");
    }

    @Override
    public void onDisable() {
        HologramUtils.deleteHolograms();
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

    public List<String> getUuidList() {
        return uuidList;
    }

    public List<String> getChestNames() {
        return chestNames;
    }

    public GUIFactory getGuiFactory() {
        return guiFactory;
    }

    public List<Hologram> getHologramList() {
        return hologramList;
    }

    public static Main getInstance() {
        return instance;
    }

    public void updateHolograms() {
        HologramUtils.createHologram();
    }

}
