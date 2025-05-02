package dev.memorydealer.modularpluginsystem.modules.poop.commands;

import dev.memorydealer.modularpluginsystem.modules.poop.PoopModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PoopCommand implements CommandExecutor {

    private final PoopModule module;

    public PoopCommand(PoopModule module) { this.module = module; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You need OP to reload the Poop module.");
                return true;
            }
            module.reloadConfigAndRestart();
            sender.sendMessage(ChatColor.GREEN + "PoopModule config reloaded.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Usage: /poop reload");
        return true;
    }
}
