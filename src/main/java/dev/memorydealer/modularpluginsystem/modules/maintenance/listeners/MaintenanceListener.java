package dev.memorydealer.modularpluginsystem.modules.maintenance.listeners;

import dev.memorydealer.modularpluginsystem.modules.maintenance.MaintenanceModeModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MaintenanceListener implements Listener {

    private final MaintenanceModeModule module;

    public MaintenanceListener(MaintenanceModeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // if not OP => KICK
        if (!event.getPlayer().isOp()) {
            event.getPlayer().kickPlayer(module.getKickMessage());
        }
    }
}