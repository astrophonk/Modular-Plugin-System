package dev.memorydealer.modularpluginsystem.core;

import dev.memorydealer.modularpluginsystem.ModularPluginSystem;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import dev.memorydealer.modularpluginsystem.core.module.Module;

import java.util.*;
import java.util.function.Supplier;

import static org.bukkit.Bukkit.getLogger;

public class LazyModuleLoader {

    private final JavaPlugin plugin;
    private final ModuleConfig moduleConfig;
    private final Map<String, Supplier<Module>> moduleSuppliers = new HashMap<>();
    private final Map<String, Module> loadedModules = new HashMap<>();

    public LazyModuleLoader(JavaPlugin plugin, ModuleConfig moduleConfig) {
        this.plugin = plugin;
        this.moduleConfig = moduleConfig;
        discoverModules();
    }

    private void discoverModules() {
        Reflections reflections = new Reflections(ModularPluginSystem.class.getPackage().getName());
        Set<Class<? extends Module>> classes = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> clazz : classes) {
            moduleSuppliers.put(clazz.getSimpleName(), () -> instantiate(clazz));
        }
    }

    private Module instantiate(Class<? extends Module> clazz) {
        try {
            boolean enabled = moduleConfig.isEnabled(clazz.getSimpleName(), true);
            getLogger().info("Loaded instantiate module: " + clazz.getSimpleName());
            return clazz.getConstructor(JavaPlugin.class, boolean.class).newInstance(plugin, enabled);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate module: " + clazz.getSimpleName(), e);
        }
    }

    public Module getModule(String name) {
        return loadedModules.computeIfAbsent(name, moduleName -> {
            Supplier<Module> supplier = moduleSuppliers.get(moduleName);
            if (supplier != null) {
                Module module = supplier.get();
                return module;
            }
            return null;
        });
    }

    public Collection<Module> getAllModules() {
        moduleSuppliers.keySet().forEach(this::getModule);
        return loadedModules.values();
    }

    public void stopAllModules() {
        loadedModules.values().forEach(Module::stop);
    }
}