package dev.memorydealer.modularpluginsystem.modules.chillzone;

import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.modules.chillzone.commands.ChillZoneCommand;
import dev.memorydealer.modularpluginsystem.modules.chillzone.listeners.PvPListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class ChillZoneModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled = false;
    private PvPListener pvpListener;
    private LocalDateTime chillEndDate;
    private int taskId;

    public ChillZoneModule(JavaPlugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
    }

    public void start() {
        if (!enabled) return;
        LocalDateTime baseline = LocalDateTime.of(2025, 3, 28, 0, 0);
        chillEndDate = baseline.plusDays(3);

        // Register PvP listener
        pvpListener = new PvPListener(chillEndDate, enabled);
        Bukkit.getPluginManager().registerEvents(pvpListener, plugin);

        // Schedule ActionBar message
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long daysLeft = LocalDateTime.now().until(chillEndDate, ChronoUnit.DAYS);
            long hoursLeft = LocalDateTime.now().until(chillEndDate, ChronoUnit.HOURS) % 24;

            String message = LocalDateTime.now().isBefore(chillEndDate)
                    ? ChatColor.GREEN + "☮ Chill Zone ends in " + ChatColor.YELLOW + daysLeft + "d " + hoursLeft + "h"
                    : "";

            Bukkit.getOnlinePlayers().forEach(player ->
                    player.sendActionBar(message)
            );

        }, 0L, 20L).getTaskId();
    }

    public void stop() {
        HandlerList.unregisterAll(pvpListener);

        Bukkit.getScheduler().cancelTask(taskId);
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
        return "ChillZone";
    }

    @Override
    public Map<String, CommandExecutor> getCommands() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        commands.put("chillzone", new ChillZoneCommand());
        return commands;
    }
}