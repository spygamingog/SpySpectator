package com.spygamingog.spectatorplusplus.tasks;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorFollowTask extends BukkitRunnable {
    private final SpectatorPlusPlus plugin;
    private final Player spectator;
    private final Player target;
    
    public SpectatorFollowTask(SpectatorPlusPlus plugin, Player spectator, Player target) {
        this.plugin = plugin;
        this.spectator = spectator;
        this.target = target;
    }
    
    @Override
    public void run() {
        // Check if still valid
        if (!spectator.isOnline() || !target.isOnline()) {
            this.cancel();
            return;
        }
        
        try {
            // Update spectator position to follow target (first-person view)
            Location targetLoc = target.getEyeLocation().clone();
            
            // Set spectator exactly at target's eye location
            spectator.teleport(targetLoc);
            
            // Sync inventory view ONLY if target has inventory open
            // Don't force open inventory - let the natural events handle it
            if (target.getOpenInventory() != null && !target.getOpenInventory().getType().toString().contains("CRAFTING")) {
                // Only sync if spectator doesn't already have same inventory open
                if (spectator.getOpenInventory() == null || 
                    !spectator.getOpenInventory().getTopInventory().equals(target.getOpenInventory().getTopInventory())) {
                    spectator.openInventory(target.getInventory());
                }
            } else if (spectator.getOpenInventory() != null) {
                // If target closed inventory, close spectator's inventory too
                spectator.closeInventory();
            }
            
            // Sync health, food, XP (optional - can be removed if not needed)
            spectator.setHealth(target.getHealth());
            spectator.setFoodLevel(target.getFoodLevel());
            spectator.setExp(target.getExp());
            spectator.setLevel(target.getLevel());
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error in SpectatorFollowTask: " + e.getMessage());
            this.cancel();
        }
    }
}
