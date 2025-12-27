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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockPlacePreventionListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public BlockPlacePreventionListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItemInHand();
        
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "Cannot place blocks while in spectator mode!");
        
        if (isSpectatorItem(item)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.getInventory().addItem(item);
            });
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItemDrop().getItemStack();
        
        if (isSpectatorItem(item)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cannot drop spectator items!");
        }
    }
    
    private boolean isSpectatorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;
        
        String displayName = meta.getDisplayName();
        return displayName.contains("Spectator") || 
               displayName.contains("Toggle") ||
               displayName.contains("Leave");
    }
}