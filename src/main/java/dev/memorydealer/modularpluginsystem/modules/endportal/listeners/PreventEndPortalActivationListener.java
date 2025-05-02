package dev.memorydealer.modularpluginsystem.modules.endportal.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
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
        if (!enabled) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.ENDER_EYE) {
                    // If end time not reached, cancel
                    if (LocalDateTime.now().isBefore(endAllowedTime)) {
                        event.setCancelled(true);

                        // The direct culprit
                        Player culprit = event.getPlayer();

                        // Play the dragon sound only to them
                        culprit.playSound(
                                culprit.getLocation(),
                                Sound.ENTITY_ENDER_DRAGON_GROWL,
                                1.0f,
                                1.0f
                        );
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!enabled) return;

        // Check if item is an Eye of Ender
        if (event.getItem().getType() == Material.ENDER_EYE) {
            // If not allowed yet, cancel
            if (LocalDateTime.now().isBefore(endAllowedTime)) {
                // The block is a dispenser
                // Check the facing block => if it's an End Portal Frame
                BlockFace facing = ((Dispenser) event.getBlock().getBlockData()).getFacing();
                Material target = event.getBlock().getRelative(facing).getType();

                if (target == Material.END_PORTAL_FRAME) {
                    event.setCancelled(true);

                    // Find the single closest player in 8 blocks, so only they hear it
                    Player closest = null;
                    double closestDist = Double.MAX_VALUE;

                    for (Player p : event.getBlock().getWorld().getPlayers()) {
                        double dist = p.getLocation().distance(event.getBlock().getLocation());
                        if (dist < 8 && dist < closestDist) {
                            closestDist = dist;
                            closest = p;
                        }
                    }

                    if (closest != null) {
                        closest.playSound(
                                closest.getLocation(),
                                Sound.ENTITY_ENDER_DRAGON_GROWL,
                                1.0f,
                                1.0f
                        );
                    }
                }
            }
        }
    }
}