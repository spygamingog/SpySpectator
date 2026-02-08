package com.spygamingog.spyspectator.gui;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectatorSettingsGUI {

    private final SpySpectator plugin;

    public SpectatorSettingsGUI(SpySpectator plugin) {
        this.plugin = plugin;
    }

    public enum SettingsType {
        CHAT, VISIBILITY
    }

    public void openGUI(Player player, SettingsType type) {
        SpectatorManager manager = plugin.getSpectatorManager();
        String title = type == SettingsType.CHAT ? "§8Spectator Chat Settings" : "§8Spectator Visibility Settings";
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Global Toggle Button (Slot 4)
        boolean globalEnabled = type == SettingsType.CHAT ? manager.isChatEnabled(player) : manager.isVisibilityEnabled(player);
        ItemStack globalToggle = new ItemStack(globalEnabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta globalMeta = globalToggle.getItemMeta();
        globalMeta.setDisplayName(type == SettingsType.CHAT ? "§aGlobal Chat: " + (globalEnabled ? "ON" : "OFF") : "§aGlobal Visibility: " + (globalEnabled ? "ON" : "OFF"));
        List<String> globalLore = new ArrayList<>();
        globalLore.add(type == SettingsType.CHAT ? "§7Click to toggle all spectator chat" : "§7Click to toggle seeing all spectators");
        globalMeta.setLore(globalLore);
        globalToggle.setItemMeta(globalMeta);
        gui.setItem(4, globalToggle);

        // List Spectators in same world
        int index = 9;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) continue;
            if (!manager.isSpectator(p)) continue;
            if (!p.getWorld().equals(player.getWorld())) continue; // Same world only

            if (index >= 54) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(p);
                
                boolean isSpecificEnabled;
                if (type == SettingsType.CHAT) {
                    isSpecificEnabled = !manager.isIgnored(player.getUniqueId(), p.getUniqueId());
                } else {
                    isSpecificEnabled = !manager.isHidden(player.getUniqueId(), p.getUniqueId());
                }
                
                String status = isSpecificEnabled ? "§aVISIBLE" : "§cHIDDEN";
                if (type == SettingsType.CHAT) status = isSpecificEnabled ? "§aHEARD" : "§cIGNORED";

                meta.setDisplayName("§e" + p.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Status: " + status);
                lore.add("§7Click to toggle");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            
            gui.setItem(index++, head);
        }

        player.openInventory(gui);
    }
}
