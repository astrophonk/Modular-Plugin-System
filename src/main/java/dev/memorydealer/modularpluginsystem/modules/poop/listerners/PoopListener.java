package dev.memorydealer.modularpluginsystem.modules.poop.listerners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class PoopListener implements Listener {

    private final JavaPlugin plugin;
    private final int sneakSeconds;
    private final int cooldownSeconds;
    private final long despawnTicks;

    /* player UUID -> time (ms) when started sneaking */
    private final Map<UUID, Long> sneakStart = new HashMap<>();
    /* player UUID -> next allowed poop epoch-ms */
    private final Map<UUID, Long> nextPoop   = new HashMap<>();

    private static final String BASE64 =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWIzYjFmNzg1ZjAxNzUzYzQ1ZWY5N2ZjZmZmZmIzZjUyNjU4ZmZjZWIxN2FkM2Y3YjU5Mjk0NWM2ZGYyZmEifX19";

    public PoopListener(JavaPlugin plugin, int sneakSeconds, int cooldownSeconds, long despawnTicks) {
        this.plugin = plugin;
        this.sneakSeconds = sneakSeconds;
        this.cooldownSeconds = cooldownSeconds;
        this.despawnTicks = despawnTicks;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();

            for (Player p : Bukkit.getOnlinePlayers()) {
                long readyAt = nextPoop.getOrDefault(p.getUniqueId(), 0L);

                if (now < readyAt) {
                    long secs = (readyAt - now + 999) / 1000;  // round-up seconds
                    p.sendActionBar("§6☢ Poop cool-down: §e" + secs + "§6s");
                } else {
                    // clear the bar if it was showing
                    p.sendActionBar("");   // (Paper API – empty string clears)
                }
            }
        }, 20L, 20L); // start after 1 s, repeat every 1 s
    }

    /* -------------------------------------------------- */
    /* Sneak tracking                                     */
    /* -------------------------------------------------- */
    @EventHandler
    public void onToggle(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (e.isSneaking()) {                  // started sneaking
            sneakStart.put(id, System.currentTimeMillis());

            // schedule check
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (p.isSneaking() &&
                        sneakStart.containsKey(id) &&
                        System.currentTimeMillis() - sneakStart.get(id) >= sneakSeconds*1000)
                {
                    attemptPoop(p);
                }
            }, sneakSeconds * 20L + 1);
        } else {                               // stopped sneaking
            sneakStart.remove(id);
        }
    }

    /* -------------------------------------------------- */
    private void attemptPoop(Player p) {
        long now = System.currentTimeMillis();
        if (now < nextPoop.getOrDefault(p.getUniqueId(), 0L)) return;  // cooldown

        nextPoop.put(p.getUniqueId(), now + cooldownSeconds*1000L);

        // find block behind player (opposite their facing)
        Block behind = p.getLocation().getBlock().getRelative(p.getFacing().getOppositeFace());
        // 1) Ensure the target block is air
        if (behind.getType() != Material.AIR) return;

        // 2) Place head block
                behind.setType(Material.PLAYER_HEAD, false);

        // 3) Apply custom texture on the block tile entity
                if (behind.getState() instanceof Skull skull) {

                    // --- build a Bukkit PlayerProfile with the texture ---
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                    profile.getProperties().add(
                            new ProfileProperty("textures", BASE64));

                    // --- set it directly on the skull ---
                    skull.setPlayerProfile(profile);
                    skull.update(true);          // push NBT to world
                }
        // schedule despawn
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (behind.getType() == Material.PLAYER_HEAD) behind.setType(Material.AIR,false);
        }, despawnTicks);

        // particles (1 second)
        BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location loc = behind.getLocation().add(0.5, 0.7, 0.5);
            p.getWorld().spawnParticle(Particle.COMPOSTER, loc, 6, .2,.1,.2, 0);
            p.getWorld().spawnParticle(Particle.PORTAL, loc, 4,
                    new Particle.DustOptions(Color.fromRGB(110, 52, 13), 1));
        }, 0L, 5L);
        Bukkit.getScheduler().runTaskLater(plugin, particleTask::cancel, 20L);

        double radiusSq = 8 * 8;
        Location loc = behind.getLocation().add(0.5, 0.5, 0.5);
        for (Player nearby : loc.getWorld().getPlayers()) {
            if (nearby.getLocation().distanceSquared(loc) <= radiusSq) {
                nearby.playSound(loc, Sound.ENTITY_BEE_HURT, 1f, 1f);
            }
        }

    }

    /* -------------------------------------------------- */
    /** injects base64 texture into SkullMeta */
    private void applyTexture(@NotNull SkullMeta meta) {
        try {
            Object profile = Class.forName("com.mojang.authlib.GameProfile")
                    .getConstructor(UUID.class, String.class)
                    .newInstance(UUID.randomUUID(), null);

            Object propMap = profile.getClass()
                    .getMethod("getProperties").invoke(profile);
            Object textures = Class.forName("com.mojang.authlib.properties.Property")
                    .getConstructor(String.class, String.class)
                    .newInstance("textures", BASE64);
            propMap.getClass().getMethod("put", Object.class, Object.class)
                    .invoke(propMap, "textures", textures);

            Field f = meta.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            f.set(meta, profile);
        } catch (Throwable t) { t.printStackTrace(); }
    }
}