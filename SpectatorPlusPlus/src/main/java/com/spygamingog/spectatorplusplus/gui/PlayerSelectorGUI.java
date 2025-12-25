package com.spygamingog.spectatorplusplus.gui;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerSelectorGUI {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    private final WorldSetManager worldSetManager;
    
    public PlayerSelectorGUI(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
        this.worldSetManager = plugin.getWorldSetManager();
    }
    
    public void open(Player spectator) {
        Set<String> playersInSet = worldSetManager.getPlayersInSameSet(spectator);
        List<Player> targetPlayers = new ArrayList<>();
        
        // Filter out spectators and get Player objects
        for (String playerName : playersInSet) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null && !spectatorManager.isSpectator(player)) {
                targetPlayers.add(player);
            }
        }
        
        // Calculate inventory size
        int size = Math.max(9, ((targetPlayers.size() + 8) / 9) * 9);
        size = Math.min(size, 54);
        
        Inventory gui = Bukkit.createInventory(null, size, 
            ChatColor.translateAlternateColorCodes('&', "&6Spectate Players"));
        
        int slot = 0;
        for (Player target : targetPlayers) {
            if (slot >= size) break;
            
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + target.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "World: " + target.getWorld().getName());
            lore.add(ChatColor.GRAY + "Health: " + formatHealth(target.getHealth()));
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
    
    private String formatHealth(double health) {
        int hearts = (int) Math.ceil(health / 2.0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hearts; i++) {
            sb.append("❤");
        }
        return sb.toString();
    }
}
