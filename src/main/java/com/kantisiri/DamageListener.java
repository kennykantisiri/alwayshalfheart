package com.kantisiri;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class DamageListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            Player player = (Player) e.getEntity();
            double health = player.getHealth();
            if (health - e.getDamage() < 0) {
                e.setDamage(health - 0.5);
            }
        }
    }

}
