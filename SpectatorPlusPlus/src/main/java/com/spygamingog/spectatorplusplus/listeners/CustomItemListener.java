package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public CustomItemListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        
        if (isCustomSpectatorItem(item)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cannot place spectator items!");
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.getInventory().addItem(item);
            });
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        
        if (spectatorManager.isSpectator(player) && isCustomSpectatorItem(item)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cannot drop spectator items!");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItem();
        if (item == null) return;
        
        if (isCustomSpectatorItem(item)) {
            if (event.getAction().toString().contains("RIGHT_CLICK_AIR")) {
                return;
            }
            
            if (event.getAction().toString().contains("RIGHT_CLICK_BLOCK") || 
                event.getAction().toString().contains("LEFT_CLICK_BLOCK")) {
                event.setCancelled(true);
                
                if (item.getType() == Material.COMPASS) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        spectatorManager.getPlayerSelectorGUI().open(player);
                    });
                }
            }
        }
    }
    
    private boolean isCustomSpectatorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        
        if (meta.hasCustomModelData()) {
            int cmd = meta.getCustomModelData();
            if (cmd >= 1000 && cmd <= 1003) {
                return true;
            }
        }
        
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            return name.contains("Spectator Compass") ||
                   name.contains("Leave Spectator Mode") ||
                   name.contains("Toggle Spectator") ||
                   name.contains("Toggle Chat");
        }
        
        return false;
    }
}