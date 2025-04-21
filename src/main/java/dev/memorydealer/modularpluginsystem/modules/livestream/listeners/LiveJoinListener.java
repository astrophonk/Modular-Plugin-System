package dev.memorydealer.modularpluginsystem.modules.livestream.listeners;


import dev.memorydealer.modularpluginsystem.modules.livestream.LiveStreamModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LiveJoinListener implements Listener {

    private final LiveStreamModule module;
    public LiveJoinListener(LiveStreamModule module){ this.module = module; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if (module.isLive(e.getPlayer())){
            module.applyTabPrefix(e.getPlayer(), module.getUrl(e.getPlayer()));
        }
    }
}