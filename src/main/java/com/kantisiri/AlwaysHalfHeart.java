package com.kantisiri;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class AlwaysHalfHeart extends JavaPlugin {

    private static AlwaysHalfHeart plugin;
    private File dataFile;

    private FileConfiguration dataFileConfig;
    private FileConfiguration configFileConfig;
    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        dataFile = new File(getDataFolder(), "data.yml");
        this.dataFileConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.configFileConfig = this.getConfig();

        if (!dataFileConfig.contains("players")) {
            dataFileConfig.set("players", new ArrayList<String>());
        }
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getCommand("halfheart").setExecutor(new HalfHeartCommand());
    }
    @Override
    public void onDisable() {
        try {
            this.saveConfig();
            this.dataFileConfig.save(dataFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "[AlwaysHalfHeart] Could not save configs!");
        }
    }

    public static AlwaysHalfHeart getPlugin() {
        return plugin;
    }

    public FileConfiguration getDataFileConfig() {
        return dataFileConfig;
    }

    public FileConfiguration getConfigFileConfig() {
        return configFileConfig;
    }


}