package dev.memorydealer.modularpluginsystem;

import dev.memorydealer.modularpluginsystem.core.GUI.ModuleMenu;
import dev.memorydealer.modularpluginsystem.core.LazyModuleLoader;
import dev.memorydealer.modularpluginsystem.core.ModuleConfig;
import dev.memorydealer.modularpluginsystem.core.commands.ModuleMenuCommand;
import dev.memorydealer.modularpluginsystem.core.managers.DynamicCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import dev.memorydealer.modularpluginsystem.core.module.Module;

import java.util.ArrayList;
import java.util.List;

public final class ModularPluginSystem extends JavaPlugin {

    public static String GUIMenuTitle = "MPS by MemoryDLR";
    private LazyModuleLoader moduleLoader;
    private ModuleConfig moduleConfig;
    private DynamicCommandManager commandManager;

    @Override
    public void onEnable() {

        moduleConfig = new ModuleConfig(this);

        moduleLoader = new LazyModuleLoader(this, moduleConfig);
        commandManager = new DynamicCommandManager(this);

        // Lazily load all modules
        List<Module> modules = new ArrayList<>(moduleLoader.getAllModules());

        // Register enabled modules' commands and start modules
        modules.forEach(module -> {
            if (module.isEnabled()) {
                module.start();
                module.getCommands().forEach(commandManager::registerCommand);
                getLogger().info("Loaded and started Module: " + module.getName());
            }
        });

        getCommand("modules").setExecutor(new ModuleMenuCommand(modules, moduleConfig));

        getLogger().info("Modular Plugin System by MemoryDLR is enabled!");

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getView().getTitle().equals(GUIMenuTitle)) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() == null) return;

                    Player player = (Player) event.getWhoClicked();
                    int slot = event.getSlot();

                    if (slot < modules.size()) {
                        Module module = modules.get(slot);
                        boolean newState = !module.isEnabled();

                        if (newState) {
                            module.setEnabled(true);
                            module.getCommands().forEach(commandManager::registerCommand);
                        } else {
                            commandManager.unregisterAll(module.getCommands());
                            module.setEnabled(false);
                        }

                        moduleConfig.setEnabled(module.getName(), newState);

                        player.sendMessage("§aModule §e" + module.getName() + " §aset to " +
                                (module.isEnabled() ? "§aEnabled" : "§cDisabled"));

                        new ModuleMenu(modules, moduleConfig).open(player);
                    }
                }
            }
        }, this);

    }

    @Override
    public void onDisable() {
        moduleLoader.stopAllModules();
    }
}
