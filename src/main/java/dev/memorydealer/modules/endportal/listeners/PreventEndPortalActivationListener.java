package dev.memorydealer.modules.endportal.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PreventEndPortalActivationListener implements Listener {

    private final boolean enabled;
    private final LocalDateTime endAllowedTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PreventEndPortalActivationListener(LocalDateTime endAllowedTime, boolean enabled) {
        this.endAllowedTime = endAllowedTime;
        this.enabled = enabled;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && enabled) {
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.ENDER_EYE) {
                    if (LocalDateTime.now().isBefore(endAllowedTime)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§c🇲🇹 [Maltese Network SMP Scheduler AI] End Portal activation is disabled until §e"
                                + endAllowedTime.format(formatter) + "§c!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() == Material.ENDER_EYE && enabled) {
            BlockFace facing = ((Dispenser) event.getBlock().getBlockData()).getFacing();
            Material target = event.getBlock().getRelative(facing).getType();

            if (target == Material.END_PORTAL_FRAME && LocalDateTime.now().isBefore(endAllowedTime)) {
                event.setCancelled(true);
                event.getBlock().getWorld().getPlayers().stream()
                        .filter(p -> p.getLocation().distance(event.getBlock().getLocation()) < 8)
                        .forEach(p -> p.sendMessage("§c🇲🇹 [Maltese Network SMP Scheduler AI] End Portal activation (via dispenser) is disabled until §e"
                                + endAllowedTime.format(formatter) + "§c!"));
            }
        }
    }
}