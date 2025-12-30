package com.spygamingog.spectatorplusplus.gui;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectatorCompassGUI {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public SpectatorCompassGUI(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    public void open(Player spectator) {
        if (spectator == null || !spectator.isOnline()) {
            return;
        }
        
        List<Player> targetPlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(spectator) && !spectatorManager.isSpectator(player)) {
                targetPlayers.add(player);
            }
        }
        
        if (targetPlayers.isEmpty()) {
            spectator.sendMessage(ChatColor.RED + "No players available to spectate!");
            return;
        }
        
        int playerCount = targetPlayers.size();
        int rows = Math.min(6, (playerCount + 8) / 9);
        int size = rows * 9;
        
        String title = ChatColor.translateAlternateColorCodes('&', "&6&lSpectate Players");
        Inventory gui = Bukkit.createInventory(null, size, title);
        
        int slot = 0;
        for (Player target : targetPlayers) {
            if (slot >= size) break;
            
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + target.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "World: " + target.getWorld().getName());
            lore.add(ChatColor.GRAY + "Health: " + (int)target.getHealth() + "/20");
            lore.add(ChatColor.GRAY + "Gamemode: " + target.getGameMode().toString());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to spectate!");
            
            meta.setLore(lore);
            meta.setOwningPlayer(target);
            skull.setItemMeta(meta);
            
            gui.setItem(slot, skull);
            slot++;
        }
        
        spectator.openInventory(gui);
    }
}