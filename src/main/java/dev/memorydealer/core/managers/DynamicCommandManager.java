package dev.memorydealer.core.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public class DynamicCommandManager {

    private final Plugin plugin;
    private CommandMap commandMap;

    public DynamicCommandManager(Plugin plugin) {
        this.plugin = plugin;
        this.commandMap = getCommandMap();
    }

    private CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(Bukkit.getPluginManager());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access Bukkit CommandMap", e);
        }
    }

    public void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = createPluginCommand(name, executor);
        commandMap.register(plugin.getName(), command);
    }

    private PluginCommand createPluginCommand(String name, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(name, plugin);
            command.setExecutor(executor);
            return command;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create command: " + name, e);
        }
    }

    public void unregisterCommand(String name) {
        try {
            Command command = commandMap.getCommand(name);
            if (command == null) return;

            // Unregister from CommandMap
            command.unregister(commandMap);

            // Remove completely from the knownCommands map
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            knownCommands.remove(name);
            knownCommands.remove(plugin.getName().toLowerCase() + ":" + name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to unregister command: " + name, e);
        }
    }

    public void unregisterAll(Map<String, CommandExecutor> commands) {
        commands.keySet().forEach(this::unregisterCommand);
    }
}