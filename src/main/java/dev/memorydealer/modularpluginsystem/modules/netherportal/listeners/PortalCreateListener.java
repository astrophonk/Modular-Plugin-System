package dev.memorydealer.modularpluginsystem.modules.netherportal.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalCreateListener implements Listener {

    private final LocalDateTime netherAllowedTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Map<UUID, Long> recentPortalAttempt = new HashMap<>();
    private final boolean enabled;

    public PortalCreateListener(LocalDateTime netherAllowedTime, boolean enabled) {
        this.netherAllowedTime = netherAllowedTime;
        this.enabled = enabled;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && enabled) {
            switch (event.getClickedBlock().getType()) {
                case OBSIDIAN, FIRE_CHARGE, DISPENSER, FIRE:
                    recentPortalAttempt.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if ((event.getReason() == PortalCreateEvent.CreateReason.FIRE
                || event.getReason() == PortalCreateEvent.CreateReason.NETHER_PAIR)
                && enabled) {

            if (LocalDateTime.now().isBefore(netherAllowedTime)) {
                event.setCancelled(true);

                long currentTime = System.currentTimeMillis();
                Player culprit = null;

                // Find the single player who last interacted within the last 3 seconds
                for (Player player : event.getWorld().getPlayers()) {
                    Long interactTime = recentPortalAttempt.get(player.getUniqueId());
                    if (interactTime != null && (currentTime - interactTime) < 3000) {
                        culprit = player;
                        // remove them from the map so we don't broadcast to them again
                        recentPortalAttempt.remove(player.getUniqueId());
                        break;
                    }
                }

                // If we found exactly one culprit, send them the message and play the error sound
                if (culprit != null) {
//                    culprit.sendMessage("§c🇲🇹 [Maltese Network SMP Scheduler AI] Nether portals are disabled until §e"
//                            + netherAllowedTime.format(formatter) + "§c!");
                    culprit.playSound(
                            culprit.getLocation(),
                            Sound.ENTITY_VILLAGER_NO,
                            1.0f,
                            1.0f
                    );
                }
            }
        }
    }
}