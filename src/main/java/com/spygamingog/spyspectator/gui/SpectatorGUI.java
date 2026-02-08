package com.spygamingog.spyspectator.gui;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spycore.api.SpyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectatorGUI {

    private final SpySpectator plugin;

    public SpectatorGUI(SpySpectator plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        // Create a GUI with a size appropriate for the number of players
        // For simplicity, we'll use a fixed size of 54, or pages if needed (sticking to 54 for now)
        int size = 54;
        Inventory gui = Bukkit.createInventory(null, size, "§8Spectator Teleporter");

        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        // Remove self
        targets.remove(player);

        int index = 0;
        for (Player target : targets) {
            if (index >= size) break;

            // Optional: Check "world set" here if needed. 
            // For now, listing all other players.
            
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName("§e" + target.getName());
                List<String> lore = new ArrayList<>();
                String worldName = SpyAPI.getAliasForWorld(target.getWorld());
                lore.add("§7World: §f" + worldName);
                lore.add("§7Click to teleport");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            gui.setItem(index++, head);
        }

        player.openInventory(gui);
    }
}
