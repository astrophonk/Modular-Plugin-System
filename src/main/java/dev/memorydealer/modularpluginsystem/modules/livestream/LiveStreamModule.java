package dev.memorydealer.modularpluginsystem.modules.livestream;

import dev.memorydealer.modularpluginsystem.modules.livestream.helpers.TabHelper;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;

import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.modules.livestream.commands.LiveCommand;
import dev.memorydealer.modularpluginsystem.modules.livestream.helpers.StreamPlatform;
import dev.memorydealer.modularpluginsystem.modules.livestream.listeners.LiveChatListener;
import dev.memorydealer.modularpluginsystem.modules.livestream.listeners.LiveJoinListener;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LiveStreamModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled;
    private final boolean tabPresent = Bukkit.getPluginManager().getPlugin("TAB") != null;

    private final Map<UUID, String> liveMap = new HashMap<>();

    private FileConfiguration cfg;
    private File cfgFile;

    private LiveCommand liveCommand;
    private LiveChatListener chatListener;
    private LiveJoinListener joinListener;

    public LiveStreamModule(JavaPlugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
        loadConfig();
    }

    @Override public void start() {
        if (!enabled) return;

        plugin.getLogger().info("LiveStreamModule started");

        liveCommand = new LiveCommand(this);

        chatListener = new LiveChatListener(this);
        joinListener = new LiveJoinListener(this);
        Bukkit.getPluginManager().registerEvents(chatListener, plugin);
        Bukkit.getPluginManager().registerEvents(joinListener, plugin);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (isLive(p)) applyTabPrefix(p, liveMap.get(p.getUniqueId()));
        });
    }

    @Override public void stop() {
        HandlerList.unregisterAll(chatListener);
        HandlerList.unregisterAll(joinListener);
        Bukkit.getOnlinePlayers().forEach(this::clearTabPrefix);
        saveConfig();
    }

    @Override public boolean isEnabled()              { return enabled; }
    @Override public void setEnabled(boolean e)       { if (enabled!=e){enabled=e;if(e)start();else stop();}}
    @Override public String getName()                 { return "LiveStreamModule"; }

    @Override public Map<String, CommandExecutor> getCommands() {
        return Map.of("live", liveCommand == null ? new LiveCommand(this) : liveCommand);
    }

    public boolean isLive(Player p)               { return liveMap.containsKey(p.getUniqueId()); }
    public String  getUrl (Player p)              { return liveMap.get(p.getUniqueId()); }

    public void setLive(Player p, String url) {
        liveMap.put(p.getUniqueId(), url);
        cfg.set("live."+p.getUniqueId(), url);
        saveConfig();

        applyTabPrefix(p,url);
        broadcastLiveStart(p,url);
    }

    public void clearLive(Player p) {
        liveMap.remove(p.getUniqueId());
        cfg.set("live."+p.getUniqueId(), null);
        saveConfig();

        clearTabPrefix(p);
        Bukkit.broadcastMessage(ChatColor.GREEN + p.getName()+" ended their live‑stream.");
    }

    public void applyTabPrefix(Player p, String url) {
        StreamPlatform plat = StreamPlatform.of(url);

        /* vanilla fallback */
        p.setPlayerListName(
                plat.colour + "[LIVE – " + plat.label + "] " + ChatColor.RESET + p.getName());

        /* TAB plugin (any version) */
        if (TabHelper.active()) {
            me.neznamy.tab.api.TabPlayer tp =
                    me.neznamy.tab.api.TabAPI.getInstance().getPlayer(p.getUniqueId());
            if (tp != null) {
                String prefix = plat.colour + "[LIVE – " + plat.label + "] " + ChatColor.RESET;
                TabHelper.setPrefix(tp, prefix);
            }
        }
    }
    public void clearTabPrefix(Player p) {
        p.setPlayerListName(p.getName());

        if (TabHelper.active()) {
            me.neznamy.tab.api.TabPlayer tp =
                    me.neznamy.tab.api.TabAPI.getInstance().getPlayer(p.getUniqueId());
            if (tp != null) TabHelper.clear(tp);
        }
    }

    /* --------------------------------------------------------------------- */
    /* Clickable broadcast on start                                          */
    /* --------------------------------------------------------------------- */
    private void broadcastLiveStart(Player streamer, String url){
        StreamPlatform plat = StreamPlatform.of(url);

        TextComponent pref = tc("["+ "LIVE – "+plat.label +"] ", plat.colour.asBungee(), true, url);
        TextComponent name = tc(streamer.getName(), net.md_5.bungee.api.ChatColor.YELLOW, false, url);
        TextComponent tail = tc(" is now streaming – click!", net.md_5.bungee.api.ChatColor.WHITE, false, url);

        Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(pref,name,tail));
        Bukkit.getConsoleSender().spigot().sendMessage(pref,name,tail);
        Bukkit.getOnlinePlayers().forEach(p ->
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f,1f));
    }

    private TextComponent tc(String txt, net.md_5.bungee.api.ChatColor c, boolean bold, String url){
        TextComponent t = new TextComponent(txt);
        t.setColor(c);
        t.setBold(bold);
        if (url!=null) t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,url));
        return t;
    }

    private void loadConfig() {
        cfgFile = new File(plugin.getDataFolder(),"LiveStreamModule.yml");
        if (!cfgFile.exists()) try { cfgFile.createNewFile(); } catch (IOException e){e.printStackTrace();}
        cfg = YamlConfiguration.loadConfiguration(cfgFile);

        Object o = cfg.getConfigurationSection("live") == null
                ? cfg.createSection("live") : cfg.getConfigurationSection("live")
                .getKeys(false);
    }
    private void saveConfig(){
        try { cfg.save(cfgFile);} catch (IOException e){e.printStackTrace();}
    }
}
