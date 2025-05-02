package dev.memorydealer.modularpluginsystem.modules.ssx;

import dev.memorydealer.modularpluginsystem.core.module.Module;
import dev.memorydealer.modularpluginsystem.modules.ssx.commands.SsxCommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SsxModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled;
    private SsxCommand command;

    /* store active repeating task id so we can cancel on disable */
    private int taskId = -1;

    /* reference to the two spawned NPCs */
    private NPC npc1;
    private NPC npc2;

    public SsxModule(JavaPlugin plugin, boolean enabled) { this.plugin = plugin; this.enabled = enabled; }

    @Override public void start() { if (!enabled) return; plugin.getLogger().info("SsxModule enabled"); }
    @Override public void stop()  {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
        if (npc1 != null) npc1.destroy();
        if (npc2 != null) npc2.destroy();
        plugin.getLogger().info("SsxModule disabled");
    }

    @Override public boolean isEnabled() { return enabled; }
    @Override public void setEnabled(boolean e){ if(enabled!=e){enabled=e;if(e)start(); else stop();}}
    @Override public String getName(){ return "SsxModule"; }

    @Override
    public Map<String, CommandExecutor> getCommands() {
        if (command == null) command = new SsxCommand(this);
        return Map.of("setssx", command);
    }

    /* -------------------------------------------------- */
    /* Called by SsxCommand                               */
    /* -------------------------------------------------- */
    public void createScene(org.bukkit.Location loc,
                            String p1, String p2, int intervalSeconds) {

        // clean old scene
        stop();

        npc1 = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, p1);
        npc2 = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, p2);

        npc1.spawn(loc);
        npc2.spawn(loc.add(0,0,2));

        npc1.setProtected(true);
        npc2.setProtected(true);

        // repeating task
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!npc1.isSpawned() || !npc2.isSpawned()) return;

            // 1) teleport npc2 behind npc1
            org.bukkit.Location base = npc1.getEntity().getLocation();
            org.bukkit.Location behind = base.clone().add(base.getDirection().multiply(-1)).add(0,0,0);
            npc2.teleport(behind, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);

            // face same direction
            behind.setYaw(base.getYaw());
            behind.setPitch(0);

            // 2) crouch for 3 seconds
            npc2.getEntity().setSneaking(true);
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    npc2.getEntity().setSneaking(false), 60); // 3 sec

        }, 0L, intervalSeconds * 20L).getTaskId();
    }
}
