package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HalfHeartTabComplete implements TabCompleter {


    private final String[] args = new String[] {"add", "remove", "reload", "players"};
    private final AlwaysHalfHeart plugin;

    public HalfHeartTabComplete() {
        plugin = AlwaysHalfHeart.getPlugin();
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] argsProvided) {
        final List<String> completions = new ArrayList<>();

        if (commandSender.hasPermission("halfheart.admin")) {
            if (argsProvided.length == 2) {
                List<String> players = plugin.getDataFileConfig().getStringList("players");
                switch (argsProvided[0]) {
                    case "add":
                        Bukkit.getOnlinePlayers()
                                .stream()
                                .filter(player -> !players.contains(player.getUniqueId().toString().replace("-", "")))
                                .forEach(p -> completions.add(p.getName()));
                        break;
                    case "remove":
                        completions.addAll(MojangAPIHelper.convertUUIDToPlayer(players).join());
                }
            }
            else if (argsProvided.length == 1) {
                completions.addAll(Arrays.asList(args));
            }
        }

        return completions;
    }
}
