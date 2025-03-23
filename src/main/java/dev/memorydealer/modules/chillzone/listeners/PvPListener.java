package dev.memorydealer.modules.chillzone.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.LocalDateTime;

public class PvPListener implements Listener {

    private final LocalDateTime chillEndDate;
    private final boolean enabled;

    public PvPListener(LocalDateTime chillEndDate, boolean enabled) {
        this.chillEndDate = chillEndDate;
        this.enabled = enabled;
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (LocalDateTime.now().isBefore(chillEndDate) && enabled) {
                event.setCancelled(true);
                ((Player) event.getDamager()).sendActionBar(ChatColor.AQUA + "☮ Chill Zone active! No PvP allowed yet.");
            } else {
                event.setCancelled(false);
            }
        }
    }
}