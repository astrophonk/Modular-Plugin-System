package dev.memorydealer.modularpluginsystem.modules.maintenance;

import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.modules.maintenance.listeners.MaintenanceListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MaintenanceModeModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled;

    private LocalDateTime endOfMaintenance;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private MaintenanceListener listener;

    // Module-specific config file
    private File configFile;
    private FileConfiguration config;

    public MaintenanceModeModule(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig(); // Load values from our custom config

        // read 'enabled' from config
        this.enabled = config.getBoolean("maintenance.enabled", false);

        // read 'endOfMaintenance' (if present)
        String endTime = config.getString("maintenance.endDate", null);
        if (endTime != null && !endTime.isEmpty()) {
            this.endOfMaintenance = LocalDateTime.parse(endTime, formatter);
        }
    }

    @Override
    public void start() {
        if (!enabled) return;

        listener = new MaintenanceListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        // Kick all non-opped players immediately
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isOp()) {
                p.kickPlayer(getKickMessage());
            }
        }
    }

    @Override
    public void stop() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;

        // store in config
        config.set("maintenance.enabled", enabled);
        saveConfig();

        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    @Override
    public String getName() {
        return "MaintenanceModeModule";
    }

    @Override
    public Map<String, org.bukkit.command.CommandExecutor> getCommands() {
        return Map.of();
    }

    /**
     * Set or remove the maintenance end time, storing in config.
     */
    public void setEndOfMaintenance(LocalDateTime endOfMaintenance) {
        this.endOfMaintenance = endOfMaintenance;

        if (endOfMaintenance != null) {
            config.set("maintenance.endDate", endOfMaintenance.format(formatter));
        } else {
            config.set("maintenance.endDate", null);
        }
        saveConfig();
    }

    public LocalDateTime getEndOfMaintenance() {
        return endOfMaintenance;
    }

    /**
     * Kick message shown to non-opped players.
     */
    public String getKickMessage() {
        String base = "§cServer in Maintenance!";
        if (endOfMaintenance != null) {
            base += "\n§7Estimated Return: §f" + endOfMaintenance.format(formatter);
        }
        return base;
    }

    // --------------------------------------------------
    // CONFIG METHODS
    // --------------------------------------------------

    private void loadConfig() {
        // Create data folder if needed
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Each module can have its own config file, e.g. MaintenanceModeModule.yml
        this.configFile = new File(plugin.getDataFolder(), "MaintenanceModeModule.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}