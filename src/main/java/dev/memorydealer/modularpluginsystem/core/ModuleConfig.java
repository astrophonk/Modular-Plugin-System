package dev.memorydealer.modularpluginsystem.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ModuleConfig {

    private final JavaPlugin plugin;
    private final Gson gson;
    private final File configFile;

    private Map<String, Boolean> moduleStates;

    public ModuleConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configFile = new File(plugin.getDataFolder(), "config.json");
        load();
    }

    private void load() {
        try {
            if (!configFile.exists()) {
                moduleStates = new HashMap<>();
                save();
                return;
            }
            Reader reader = Files.newBufferedReader(configFile.toPath());
            Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
            moduleStates = gson.fromJson(reader, type);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            moduleStates = new HashMap<>();
        }
    }

    public void save() {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            Writer writer = new FileWriter(configFile);
            gson.toJson(moduleStates, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled(String moduleName, boolean defaultValue) {
        return moduleStates.getOrDefault(moduleName, defaultValue);
    }

    public void setEnabled(String moduleName, boolean enabled) {
        moduleStates.put(moduleName, enabled);
        save();
    }
}

