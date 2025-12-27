package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public PlayerListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        
        String displayName = item.getItemMeta().getDisplayName();
        
        if (item.getType() == Material.COMPASS && displayName.contains("Spectator Compass")) {
            event.setCancelled(true);
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && spectatorManager.isSpectator(player)) {
                    try {
                        if (player.getOpenInventory() != null) {
                            player.closeInventory();
                        }
                        
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            spectatorManager.getPlayerSelectorGUI().open(player);
                        }, 2L);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Failed to open player selector!");
                        plugin.getLogger().severe("Error opening GUI: " + e.getMessage());
                    }
                }
            });
            return;
        }
        
        if (item.getType() == Material.RED_BED && displayName.contains("Leave Spectator Mode")) {
            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    spectatorManager.leaveSpectator(player);
                }
            });
            return;
        }
        
        if (item.getType() == Material.ENDER_EYE && displayName.contains("Toggle Spectator Visibility")) {
            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    spectatorManager.toggleSpectatorVisibility(player);
                }
            });
            return;
        }
        
        if (item.getType() == Material.PAPER && displayName.contains("Toggle Spectator Chat")) {
            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    spectatorManager.toggleSpectatorChat(player);
                }
            });
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            
            if (!player.equals(target) && !spectatorManager.isSpectator(target)) {
                event.setCancelled(true);
                
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && target.isOnline()) {
                        if (!spectatorManager.isSpectator(player)) {
                            spectatorManager.enterSpectator(player);
                        }
                        
                        spectatorManager.spectatePlayer(player, target);
                        player.sendMessage(ChatColor.GREEN + "Now spectating " + target.getName() + " (first-person view)");
                    }
                });
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (event.isSneaking() && spectatorManager.isSpectating(player)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    spectatorManager.stopSpectating(player);
                    player.sendMessage(ChatColor.YELLOW + "Stopped spectating - still in spectator mode");
                }
            });
        }
    }
}