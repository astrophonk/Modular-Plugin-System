package dev.memorydealer.modularpluginsystem.core.module;

import org.bukkit.command.CommandExecutor;

import java.util.Collections;
import java.util.Map;

public interface Module {
    void start();
    void stop();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    default String getName() {
        return getClass().getSimpleName();
    }

    default Map<String, CommandExecutor> getCommands() {
        return Collections.emptyMap(); // No commands by default
    }
}