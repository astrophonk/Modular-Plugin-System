package dev.memorydealer.modularpluginsystem.core.GUI;


import dev.memorydealer.modularpluginsystem.ModularPluginSystem;
import dev.memorydealer.modularpluginsystem.core.ModuleConfig;
import dev.memorydealer.modularpluginsystem.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ModuleMenu {

    private final List<Module> modules;

    private final ModuleConfig moduleConfig;

    public ModuleMenu(List<Module> modules, ModuleConfig moduleConfig) {
        this.modules = modules;
        this.moduleConfig = moduleConfig;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ModularPluginSystem.GUIMenuTitle);

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            // Ensure module state matches what's saved in config
            boolean enabled = moduleConfig.isEnabled(module.getName(), module.isEnabled());
            module.setEnabled(enabled);

            Material icon = enabled ? Material.LIME_WOOL : Material.RED_WOOL;
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.AQUA + module.getName());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
                    "",
                    ChatColor.YELLOW + "Click to toggle."
            ));

            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        player.openInventory(inv);
    }
}
