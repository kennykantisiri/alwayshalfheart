package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class AlwaysHalfHeart extends JavaPlugin {

    private static AlwaysHalfHeart plugin;
    public boolean debug;
    private File dataFile;

    private FileConfiguration dataFileConfig;
    private FileConfiguration configFileConfig;
    private List<EntityDamageEvent.DamageCause> damageCausesList;
    @Override
    public void onEnable() {
        debug = true;
        plugin = this;
        this.saveDefaultConfig();
        dataFile = new File(getDataFolder(), "data.yml");
        this.dataFileConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.configFileConfig = this.getConfig();
        this.mapDamageCauses();

        if (!dataFileConfig.contains("players")) {
            dataFileConfig.set("players", new ArrayList<String>());
        }

        sendDebugMessage("Loaded configuration and data files.");

        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getCommand("halfheart").setExecutor(new HalfHeartCommand());
        this.getCommand("halfheart").setTabCompleter(new HalfHeartTabComplete());

        sendDebugMessage("Registered Events and Command");
        debug = false;
    }
    @Override
    public void onDisable() {
        this.saveConfig();
        plugin.reloadDataConfig();
    }
    public void reloadDataConfig() {
        dataFile = new File(getDataFolder(), "data.yml");
        this.dataFileConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            this.dataFileConfig.save(dataFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "[AlwaysHalfHeart] Could not save data!");
        }
    }

    public void sendDebugMessage(String rawMessage) {
        String message = ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + this.getName() + "] DEBUG: " +
                ChatColor.RESET + ChatColor.YELLOW + rawMessage;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("halfheart.debug")) {
                player.sendMessage(message);
            }
        });

        if (debug) Bukkit.getLogger().log(Level.WARNING, ChatColor.stripColor(message));
    }

    public static AlwaysHalfHeart getPlugin() {
        return plugin;
    }

    public void mapDamageCauses() {
        List<String> allowedKills = this.getConfig().getStringList("allowed-kills");
        this.damageCausesList = allowedKills.stream().map(s -> {
                    try {
                        this.sendDebugMessage("Trying to find DamageCause.valueOf(" + s + ")");
                        return EntityDamageEvent.DamageCause.valueOf(s);
                    } catch (IllegalArgumentException err) {
                        this.sendDebugMessage("[" + plugin.getName() + "] Invalid DamageCause in config.yml: " + s);
                        Bukkit.getLogger().log(Level.SEVERE, "[" + plugin.getName() + "] Invalid DamageCause in config.yml: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<EntityDamageEvent.DamageCause> getDamageCausesList() {
        return damageCausesList;
    }

    public FileConfiguration getDataFileConfig() {
        return dataFileConfig;
    }

}