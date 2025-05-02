package dev.memorydealer.modularpluginsystem.modules.chillzone.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SMPRulesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Make sure it's a player executing the command
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Rules header
        player.sendMessage(ChatColor.GOLD + "📜 The Maltese Network SMP - Server Rules");
        player.sendMessage(ChatColor.YELLOW + "Welcome to The Maltese Network SMP! To keep the server fun and fair for everyone, please follow these rules.");

        // General Rules
        player.sendMessage(ChatColor.BLUE + "🛡️ General Rules:");
        player.sendMessage("1️⃣ Be respectful – Treat all players with kindness. No harassment, hate speech, or toxicity.");
        player.sendMessage("2️⃣ No griefing – Do not destroy or modify other players' builds without permission.");
        player.sendMessage("3️⃣ No stealing – Do not take items from others without their consent.");
        player.sendMessage("4️⃣ Pranks & trolling must be harmless – Any pranks should be non-destructive and easily fixable.");
        player.sendMessage("5️⃣ No cheating in any form – This includes dupes, hacks, cheats, or x-ray texture packs.");
        player.sendMessage("6️⃣ If any issues arise, contact @GetPrecise or @MemoryDealer.");

        // PvP Rules
        player.sendMessage(ChatColor.BLUE + "\n⚔ PvP Rules:");
        player.sendMessage("• PvP is only allowed if both players agree to fight beforehand,");
        player.sendMessage("  OR if the player being attacked is wearing any amount of netherite armor.");
        player.sendMessage("• Anyone being attacked can fight back, regardless of armor.");
        player.sendMessage("• Wearing even a single piece of netherite means you are open to PvP.");

        return true;
    }
}
