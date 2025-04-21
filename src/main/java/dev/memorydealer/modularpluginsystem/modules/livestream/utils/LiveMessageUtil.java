package dev.memorydealer.modularpluginsystem.modules.livestream.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LiveMessageUtil {

    /* Broadcasts clickable [LIVE] message */
    public static void broadcastLive(Player streamer, String url) {
        TextComponent live = new TextComponent("[LIVE] ");
        live.setColor(net.md_5.bungee.api.ChatColor.RED);
        TextComponent name = new TextComponent(streamer.getName());
        name.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        TextComponent msg = new TextComponent(" is streaming – click to watch!");
        msg.setColor(net.md_5.bungee.api.ChatColor.WHITE);

        live.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        name.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(live, name, msg);
        }
        Bukkit.getConsoleSender().spigot().sendMessage(live, name, msg);
    }

    public static void broadcastOffline(Player streamer) {
        String text = ChatColor.GREEN + streamer.getName() + " ended their live‐stream.";
        Bukkit.broadcastMessage(text);
    }
}