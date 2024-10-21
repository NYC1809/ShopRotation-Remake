package de.nyc.shopRotationRemake;

import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        LocalDateTime start = LocalDateTime.now();
        getLogger().info("loaded in " + Duration.between(start, LocalDateTime.now()).toMillis() + "ms");
    }

    @Override
    public void onDisable() {

    }
}
