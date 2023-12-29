package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class HalfHeartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Server server = Bukkit.getServer();

        if (!sender.hasPermission("halfheart.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
            return true;
        }

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
                            sender.sendMessage(ChatColor.GREEN + "Added player " + uuid);
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            sender.sendMessage(ChatColor.GREEN + "Removed player " + uuid);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found within Minecraft. Are you sure it's the right name?");
                    }
                });

                return true;
            }
            case "players":
                sender.sendMessage(ChatColor.RED + "List of players:");
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "/halfheart add <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart remove <player>");
                sender.sendMessage(ChatColor.RED + "/halfheart players");
                return true;
        }

    }
}
