package dev.memorydealer.modularpluginsystem.core.commands;

import dev.memorydealer.modularpluginsystem.ModularPluginSystem;
import dev.memorydealer.modularpluginsystem.core.ModuleConfig;
import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.core.GUI.ModuleMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ModuleMenuCommand implements CommandExecutor {

    private final ModuleMenu menu;

    public ModuleMenuCommand(List<Module> modules,  ModuleConfig moduleConfig) {
        this.menu = new ModuleMenu(modules, moduleConfig);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission(ModularPluginSystem.class.getPackage().getName())) {
            player.sendMessage("§cNo permission.");
            return true;
        }

        menu.open(player);
        return true;
    }
}