package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AlwaysHalfHeart extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getCommand("halfheart").setExecutor(new HalfHeartCommand());
        Bukkit.getLogger().log(Level.INFO, "Loaded command successfully!");
    }

}