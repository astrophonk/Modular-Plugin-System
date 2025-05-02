package dev.memorydealer.modularpluginsystem.modules.ssx.commands;

import dev.memorydealer.modularpluginsystem.modules.ssx.SsxModule;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SsxCommand implements CommandExecutor {

    private final SsxModule module;

    public SsxCommand(SsxModule module){ this.module = module; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] a) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players.");
            return true;
        }
        if (!p.isOp()) { p.sendMessage(ChatColor.RED+"OP only."); return true; }

        if (a.length != 3) {
            p.sendMessage(ChatColor.YELLOW+"/setssx <player1> <player2> <seconds>");
            return true;
        }

        String p1 = a[0];
        String p2 = a[1];
        int seconds;
        try { seconds = Integer.parseInt(a[2]); }
        catch (NumberFormatException ex){
            p.sendMessage(ChatColor.RED+"Seconds must be a number."); return true;
        }
        if (seconds < 1){ p.sendMessage(ChatColor.RED+"Seconds must be ≥1"); return true; }

        module.createScene(p.getLocation(), p1, p2, seconds);
        p.sendMessage(ChatColor.GREEN+"SSX scene created: "+p1+" & "+p2+" every "+seconds+"s");
        return true;
    }
}