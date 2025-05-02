package dev.memorydealer.modularpluginsystem.modules.livestream.listeners;

import dev.memorydealer.modularpluginsystem.modules.livestream.LiveStreamModule;
import dev.memorydealer.modularpluginsystem.modules.livestream.helpers.StreamPlatform;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LiveChatListener implements Listener {

    private final LiveStreamModule module;

    public LiveChatListener(LiveStreamModule module){ this.module = module; }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if (!module.isLive(e.getPlayer())) return;   // not live => normal

        e.setCancelled(true);                        // custom send
        String url = module.getUrl(e.getPlayer());
        StreamPlatform plat = StreamPlatform.of(url);

        TextComponent prefix = new TextComponent("[LIVE – "+plat.label+"] ");
        prefix.setColor(net.md_5.bungee.api.ChatColor.valueOf(plat.colour.name()));
        prefix.setBold(true);
        prefix.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,url));

        TextComponent name = new TextComponent(e.getPlayer().getName()+": ");
        name.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        name.setClickEvent(prefix.getClickEvent());

        TextComponent msg  = new TextComponent(e.getMessage());
        msg.setColor(net.md_5.bungee.api.ChatColor.WHITE);

        e.getRecipients().forEach(p -> p.spigot().sendMessage(prefix,name,msg));
    }
}