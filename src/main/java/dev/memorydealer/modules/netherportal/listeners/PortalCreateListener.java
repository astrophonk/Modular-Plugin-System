package dev.memorydealer.modules.netherportal.listeners;

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
        if ((event.getReason() == PortalCreateEvent.CreateReason.FIRE ||
                event.getReason() == PortalCreateEvent.CreateReason.NETHER_PAIR) && enabled) {

            if (LocalDateTime.now().isBefore(netherAllowedTime)) {
                event.setCancelled(true);

                long currentTime = System.currentTimeMillis();
                for (Player player : event.getWorld().getPlayers()) {
                    Long interactTime = recentPortalAttempt.get(player.getUniqueId());
                    if (interactTime != null && (currentTime - interactTime) < 3000) {
                        player.sendMessage("§c🇲🇹 [Maltese Network SMP Scheduler AI] Nether portals are disabled until §e"
                                + netherAllowedTime.format(formatter) + "§c!");
                        recentPortalAttempt.remove(player.getUniqueId());
                    }
                }
            }
        }
    }
}