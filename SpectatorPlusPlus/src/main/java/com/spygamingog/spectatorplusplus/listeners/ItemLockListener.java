package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLockListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public ItemLockListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            ItemStack item = event.getItemDrop().getItemStack();
            
            if (isSpectatorItem(item)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot drop spectator items!");
                
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        player.getInventory().setItem(0, createCompassItem());
                        player.getInventory().setItem(8, createBedItem());
                    }
                }, 1L);
            }
        }
    }
    
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            if (isSpectatorItem(event.getMainHandItem()) || isSpectatorItem(event.getOffHandItem())) {
                event.setCancelled(true);
            }
        }
    }
    
    private boolean isSpectatorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;
        
        String displayName = meta.getDisplayName();
        return displayName.contains("Spectator Compass") || 
               displayName.contains("Leave Spectator Mode");
    }
    
    private ItemStack createCompassItem() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Spectator Compass");
            meta.setUnbreakable(true);
            compass.setItemMeta(meta);
        }
        return compass;
    }
    
    private ItemStack createBedItem() {
        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta meta = bed.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Leave Spectator Mode");
            meta.setUnbreakable(true);
            bed.setItemMeta(meta);
        }
        return bed;
    }
}