package com.kantisiri;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DamageListener implements Listener {

    private final AlwaysHalfHeart plugin;

    public DamageListener() {
        plugin = AlwaysHalfHeart.getPlugin();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            plugin.sendDebugMessage("Player " + e.getEntity().getName() + " was damaged by " + e.getCause() + " with damage of " + e.getDamage());
            List<EntityDamageEvent.DamageCause> allowedCauses = plugin.getDamageCausesList();
            List<String> playersEffected = plugin.getDataFileConfig().getStringList("players");
            Player player = (Player) e.getEntity();

            if (!playersEffected.contains(player.getUniqueId().toString().replace("-", ""))) {
                plugin.sendDebugMessage("Player " + player.getName() + " was not in effected list so they were ignored.");
                return;
            }

            if (!allowedCauses.contains(e.getCause())) {
                double health = player.getHealth();
                plugin.sendDebugMessage("Cause: " + e.getCause() + " was NOT in allowedCauses. Original Health: " + health + " Damage of " + e.getDamage()
                        + ". Is less than or equal to 0? " + (health - e.getDamage() <= 0));
                if (health - e.getDamage() <= 0) {
                    e.setDamage(health - 0.5);
                    plugin.sendDebugMessage("Since true, so damage was set to " + (health - 0.5) + " from previous damage.");
                }
            } else {
                plugin.sendDebugMessage("Cause: " + e.getCause() + " was in allowedCauses. Skipping.");
            }
        }
    }

}
