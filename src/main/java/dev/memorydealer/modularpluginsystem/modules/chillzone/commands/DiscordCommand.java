package dev.memorydealer.modularpluginsystem.modules.chillzone.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class DiscordCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Create your clickable text
            TextComponent message = new TextComponent("§9https://discord.gg/FgDknCWgnU");
            // Set a click event (OPEN_URL to open a website)
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/FgDknCWgnU"));

            // Send the clickable message to the player
            player.spigot().sendMessage(message);
        } else {
            sender.sendMessage("This command can only be used by a player!");
        }
        return true;
    }
}
