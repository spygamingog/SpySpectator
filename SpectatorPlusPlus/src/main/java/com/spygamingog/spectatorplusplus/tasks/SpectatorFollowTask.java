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
        if (!spectator.isOnline() || !target.isOnline()) {
            this.cancel();
            return;
        }
        
        try {
            Location targetLoc = target.getEyeLocation().clone();
            
            spectator.teleport(targetLoc);
            
            if (target.getOpenInventory() != null && !target.getOpenInventory().getType().toString().contains("CRAFTING")) {
                if (spectator.getOpenInventory() == null || 
                    !spectator.getOpenInventory().getTopInventory().equals(target.getOpenInventory().getTopInventory())) {
                    spectator.openInventory(target.getInventory());
                }
            } else if (spectator.getOpenInventory() != null) {
                spectator.closeInventory();
            }
            
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