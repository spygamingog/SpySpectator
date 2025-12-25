package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        ItemStack item = event.getItem();
        if (item == null) return;
        
        if (!item.hasItemMeta()) return;
        
        String displayName = item.getItemMeta().getDisplayName();
        
        if (item.getType() == Material.COMPASS && displayName.contains("Spectator Compass")) {
            event.setCancelled(true);
            // Open player selector GUI
            spectatorManager.getPlayerSelectorGUI().open(player);
            return;
        }
        
        if (item.getType() == Material.RED_BED && displayName.contains("Leave Spectator Mode")) {
            event.setCancelled(true);
            spectatorManager.leaveSpectator(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getMessage("leave-spectator")));
            return;
        }
        
        // Cancel all other interactions for spectators
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player) && spectatorManager.isSpectating(player)) {
            if (event.isSneaking()) {
                // Stop spectating when sneaking
                spectatorManager.stopSpectating(player);
                player.sendMessage(ChatColor.YELLOW + "Stopped spectating");
            }
        }
    }
}
