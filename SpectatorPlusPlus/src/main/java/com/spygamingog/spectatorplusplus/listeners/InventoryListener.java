package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public InventoryListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        if (spectatorManager.isSpectator(player)) {
            if (spectatorManager.isSpectating(player)) {
                event.setCancelled(true);
                return;
            }
            
            String title = event.getView().getTitle();
            if (title.contains("Spectate Players")) {
                if (event.getCurrentItem() != null && 
                    event.getCurrentItem().getType().toString().contains("PLAYER_HEAD")) {
                    String playerName = event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace("§a", "").replace("§2", "").replace("§e", "").replace("§6", "");
                    playerName = org.bukkit.ChatColor.stripColor(playerName);
                        
                    org.bukkit.entity.Player target = plugin.getServer().getPlayerExact(playerName);
                    if (target != null && !spectatorManager.isSpectator(target)) {
                        event.setCancelled(true);
                        player.closeInventory();
                        spectatorManager.spectatePlayer(player, target);
                    }
                }
                return;
            }
            
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                org.bukkit.inventory.meta.ItemMeta meta = event.getCurrentItem().getItemMeta();
                String displayName = meta.getDisplayName();
                if (displayName.contains("Spectator Compass") || 
                    displayName.contains("Leave Spectator Mode")) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        if (spectatorManager.isSpectator(player)) {
            String title = event.getView().getTitle();
            if (!title.contains("Spectate Players")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            if (!spectatorManager.isSpectating(player)) {
                if (event.getInventory().getType() != InventoryType.ENDER_CHEST && 
                    event.getInventory().getType() != InventoryType.WORKBENCH) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}