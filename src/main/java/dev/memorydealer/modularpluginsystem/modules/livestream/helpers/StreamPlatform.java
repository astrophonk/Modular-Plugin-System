package dev.memorydealer.modularpluginsystem.modules.livestream.helpers;

import org.bukkit.ChatColor;

public enum StreamPlatform {
    KICK   ("Kick",    ChatColor.GREEN,       "kick.com"),
    TWITCH ("Twitch",  ChatColor.DARK_PURPLE, "twitch.tv"),
    YOUTUBE("YouTube", ChatColor.RED,         "youtube.com", "youtu.be"),
    OTHER  ("Live",    ChatColor.YELLOW);

    public final String label;
    public final ChatColor colour;
    private final String[] hints;

    StreamPlatform(String label,
                   ChatColor colour,
                   String... hints) {
        this.label  = label;
        this.colour = colour;
        this.hints  = hints;
    }

    /** Determine a platform from a URL. */
    public static StreamPlatform of(String url) {
        String lower = url.toLowerCase();
        for (StreamPlatform p : values()) {
            for (String h : p.hints) if (lower.contains(h)) return p;
        }
        return OTHER;
    }
}