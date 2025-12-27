package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorItemListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public SpectatorItemListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            ItemStack item = event.getItemInHand();
            
            if (isSpectatorItem(item)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place spectator items!");
                
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.getInventory().addItem(item);
                });
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItem();
        if (item == null) return;
        
        if (isSpectatorItem(item)) {
            if (event.getAction().toString().contains("RIGHT_CLICK_AIR")) {
                return; 
            }
            
            if (event.getAction().toString().contains("RIGHT_CLICK_BLOCK") || 
                event.getAction().toString().contains("LEFT_CLICK_BLOCK")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use spectator items on blocks!");
            }
        }
    }
    
    private boolean isSpectatorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;
        
        String displayName = meta.getDisplayName();
        return displayName.contains("Spectator Compass") || 
               displayName.contains("Leave Spectator Mode") ||
               displayName.contains("Toggle Spectator Visibility") ||
               displayName.contains("Toggle Spectator Chat");
    }
}