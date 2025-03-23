package dev.memorydealer.modules.netherportal;

import dev.memorydealer.core.module.Module;
import dev.memorydealer.modules.netherportal.listeners.PortalCreateListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;

public class NetherPortalModule implements Module {

    private boolean enabled = false;
    private final Plugin plugin;
    private PortalCreateListener portalCreateListener;

    public NetherPortalModule(Plugin plugin, boolean enabled) {

        this.plugin = plugin;
        this.enabled = enabled;
    }

    public void start() {
        if(!enabled) return;
        LocalDateTime baseline = LocalDateTime.of(2025, 3, 28, 0, 0);
        LocalDateTime netherAllowedTime = baseline.plusWeeks(1); // Opens 04 April 2025

        portalCreateListener = new PortalCreateListener(netherAllowedTime, enabled);

        Bukkit.getPluginManager().registerEvents(portalCreateListener, plugin);
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(portalCreateListener);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;
        if (enabled) start();
        else stop();
    }

    @Override
    public String getName() {
        return "Nether Portal Module";
    }
}