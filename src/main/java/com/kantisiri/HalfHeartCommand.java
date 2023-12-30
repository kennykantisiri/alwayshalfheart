package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HalfHeartCommand implements CommandExecutor {

    private final AlwaysHalfHeart plugin;

    public HalfHeartCommand() {
        plugin = AlwaysHalfHeart.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration dataConfig = plugin.getDataFileConfig();
        List<String> players = dataConfig.getStringList("players");

        if (!sender.hasPermission("halfheart.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/halfheart add <player>");
            sender.sendMessage(ChatColor.RED + "/halfheart remove <player>");
            sender.sendMessage(ChatColor.RED + "/halfheart players");
            sender.sendMessage(ChatColor.RED + "/halfheart reload");
            return true;
        }

        switch(args[0]) {
            case "add":
            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "You must specify a player!");
                    return true;
                }

                CompletableFuture<Pair<String, String>> playerUUIDFuture = MojangAPIHelper.getPlayerInfo(args[1]);
                playerUUIDFuture.thenAccept(record -> {
                    String name = record.getKey();
                    String uuid = record.getValue();
                    if (uuid != null) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (players.contains(uuid)) {
                                sender.sendMessage(ChatColor.RED + "Player (" + name + ") already exists in database.");
                            } else {
                                players.add(uuid);
                                sender.sendMessage(ChatColor.GREEN + "Added player " + name);
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (players.contains(uuid)) {
                                players.remove(uuid);
                                sender.sendMessage(ChatColor.GREEN + "Removed player " + name);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player (" + name + ") does not exist in database.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found within Minecraft. Are you sure it's the right name?");
                    }

                    dataConfig.set("players", players);
                    this.plugin.saveData();
                });

                return true;
            }
            case "players":
                MojangAPIHelper.convertUUIDToPlayer(players)
                        .thenAccept(names ->
                                sender.sendMessage(ChatColor.GREEN + "Player Effected List: " + names));
                return true;
            case "reload":
                plugin.reloadDataConfig();
                plugin.reloadConfig();
                plugin.mapDamageCauses();
                plugin.sendDebugMessage("Loaded configuration and data files.");
                sender.sendMessage(ChatColor.GREEN + "Reloaded configuration!");
                return true;
            case "debug":
                if (sender instanceof ConsoleCommandSender) {
                    if (plugin.debug) {
                        plugin.debug = false;
                        sender.sendMessage(ChatColor.RED + "Console debug is DISABLED.");
                    } else {
                        plugin.debug = true;
                        sender.sendMessage(ChatColor.GREEN + "Console debug is ENABLED.");
                    }

                    return true;
                }

                // passthru to default if not console
            default:
                sender.sendMessage(ChatColor.RED + "/halfheart add <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart remove <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart players");
                sender.sendMessage(ChatColor.RED + "/halfheart reload");
                return true;
        }

    }
}
