package dev.memorydealer.modularpluginsystem.modules.poop;

import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.modules.poop.commands.PoopCommand;
import dev.memorydealer.modularpluginsystem.modules.poop.listerners.PoopListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PoopModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled;
    private PoopCommand poopCommand;

    /* configurable values (defaults) */
    private int  sneakSeconds   = 3;
    private int  cooldownSecs   = 30;
    private long despawnTicks   = 12L * 60 * 60 * 20; // 12 h

    /* config file */
    private FileConfiguration cfg;
    private File cfgFile;

    private PoopListener listener;

    public PoopModule(JavaPlugin plugin, boolean enabled) {
        this.plugin  = plugin;
        this.enabled = enabled;
        loadConfig();                // fills the three fields
    }

    /* -------------------------------------------------- */
    @Override public void start() {
        if (!enabled) return;

        listener = new PoopListener(plugin,
                sneakSeconds,
                cooldownSecs,
                despawnTicks);

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        plugin.getLogger().info("PoopModule enabled (sneak " + sneakSeconds
                + "s, cooldown " + cooldownSecs + "s, despawn " + despawnTicks/20/60/60 + "h)");
    }

    @Override public void stop() {
        org.bukkit.event.HandlerList.unregisterAll(listener);
        plugin.getLogger().info("PoopModule disabled");
    }

    /* -------------------------------------------------- */
    @Override public boolean isEnabled() { return enabled; }
    @Override public void setEnabled(boolean e){ if(enabled!=e){enabled=e;if(e)start(); else stop();}}
    @Override public String getName(){ return "PoopModule"; }

    @Override public Map<String, CommandExecutor> getCommands() {
        if (poopCommand == null) poopCommand = new PoopCommand(this);
        return Map.of("poop", poopCommand);
    }

    public void reloadConfigAndRestart() {
        loadConfig();
        stop();
        start();
    }

    /* -------------------------------------------------- */
    private void loadConfig() {
        try {
            cfgFile = new File(plugin.getDataFolder(),"PoopModule.yml");
            if (!cfgFile.exists()) {
                cfgFile.getParentFile().mkdirs();
                cfgFile.createNewFile();
            }
            cfg = YamlConfiguration.loadConfiguration(cfgFile);

            /* defaults if keys missing */
            cfg.addDefault("sneak-seconds", 3);
            cfg.addDefault("cooldown-seconds", 30);
            cfg.addDefault("despawn-hours", 12);
            cfg.options().copyDefaults(true);
            cfg.save(cfgFile);

            /* read */
            sneakSeconds = Math.max(1, cfg.getInt("sneak-seconds", 3));
            cooldownSecs = Math.max(1, cfg.getInt("cooldown-seconds", 30));
            int despawnH = Math.max(1, cfg.getInt("despawn-hours", 12));
            despawnTicks = despawnH * 60L * 60 * 20;

        } catch (IOException ex) {
            plugin.getLogger().warning("Could not load PoopModule.yml – using defaults");
        }
    }
}