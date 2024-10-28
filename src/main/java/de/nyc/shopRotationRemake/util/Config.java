package de.nyc.shopRotationRemake.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {

    private final JavaPlugin plugin;
    private final String fileName;
    private File configFile;
    private FileConfiguration config;

    public Config(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        createConfig();
    }

    private void createConfig() {
        configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);  // Copy defaults from resources if available
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            createConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + fileName + "!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), fileName);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }


}
