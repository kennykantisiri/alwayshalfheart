package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HalfHeartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        AlwaysHalfHeart plugin = AlwaysHalfHeart.getPlugin();
        FileConfiguration dataConfig = plugin.getDataFileConfig();
        List<String> players = dataConfig.getStringList("players");

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/halfheart add <player>");
            sender.sendMessage(ChatColor.RED + "/halfheart remove <player>");
            sender.sendMessage(ChatColor.RED + "/halfheart players");
            return true;
        }

        switch(args[0]) {
            case "add":
            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "You must specify a player!");
                    return true;
                }

                CompletableFuture<String> playerUUIDFuture = MojangAPIHelper.getPlayerUUID(args[1]);
                playerUUIDFuture.thenAccept(uuid -> {
                    if (uuid != null) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (players.contains(uuid)) {
                                Bukkit.getLogger().log(Level.INFO, players.toString());
                                sender.sendMessage(ChatColor.RED + "Player (" + uuid + ") already exists in database.");
                            } else {
                                players.add(uuid);
                                Bukkit.getLogger().log(Level.INFO, "players: " + players.toString());
                                sender.sendMessage(ChatColor.GREEN + "Added player " + uuid);
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (players.contains(uuid)) {
                                players.remove(uuid);
                                sender.sendMessage(ChatColor.GREEN + "Removed player " + uuid);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player (" + uuid + ") does not exist in database.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found within Minecraft. Are you sure it's the right name?");
                    }

                    dataConfig.set("players", players);
                });

                return true;
            }
            case "players":
                sender.sendMessage(ChatColor.RED + "List of players: " + players);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "/halfheart add <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart remove <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart players");
                return true;
        }

    }
}
