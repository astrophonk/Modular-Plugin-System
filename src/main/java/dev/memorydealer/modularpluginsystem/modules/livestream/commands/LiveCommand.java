package dev.memorydealer.modularpluginsystem.modules.livestream.commands;

import dev.memorydealer.modularpluginsystem.modules.livestream.LiveStreamModule;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LiveCommand implements CommandExecutor {

    private final LiveStreamModule module;

    public LiveCommand(LiveStreamModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player!");
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use /live.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /live <url|off>");
            return true;
        }

        if ("off".equalsIgnoreCase(args[0])) {
            if (module.isLive(player)) {
                module.clearLive(player);
                player.sendMessage(ChatColor.GREEN + "You are no longer marked as live.");
            } else {
                player.sendMessage(ChatColor.RED + "You weren't live.");
            }
            return true;
        }

        // Combine the rest of args into the URL
        String url = String.join(" ", args);
        if (!url.startsWith("http")) {
            player.sendMessage(ChatColor.RED + "Please provide a valid stream URL (http/https).");
            return true;
        }

        // Toggle streamer ON
        module.setLive(player, url);
        player.sendMessage(ChatColor.GREEN + "You are now live!");

        /* Optional clickable confirmation just to the streamer */
        TextComponent confirm = new TextComponent("[LIVE link] Click to open your stream.");
        confirm.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        player.spigot().sendMessage(confirm);

        return true;
    }
}