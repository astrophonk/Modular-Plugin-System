package dev.memorydealer.core.commands;

import dev.memorydealer.core.ModuleConfig;
import dev.memorydealer.core.module.Module;
import dev.memorydealer.core.GUI.ModuleMenu;
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

        if (!player.hasPermission("dev.memorydealer.modules")) {
            player.sendMessage("§cNo permission.");
            return true;
        }

        menu.open(player);
        return true;
    }
}