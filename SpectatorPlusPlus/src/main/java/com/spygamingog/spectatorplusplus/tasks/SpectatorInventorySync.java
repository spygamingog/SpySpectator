package com.spygamingog.spectatorplusplus.tasks;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorInventorySync extends BukkitRunnable {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public SpectatorInventorySync(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @Override
    public void run() {
        for (Player spectator : spectatorManager.getSpectators()) {
            if (spectatorManager.isSpectating(spectator)) {
                Player target = spectatorManager.getSpectatingTarget(spectator);
                if (target != null && target.isOnline()) {
                    syncInventoryView(spectator, target);
                }
            }
        }
    }
    
    private void syncInventoryView(Player spectator, Player target) {
        InventoryView targetView = target.getOpenInventory();
        InventoryView spectatorView = spectator.getOpenInventory();
        
        // If target has an inventory open
        if (targetView != null && !targetView.getType().toString().contains("CRAFTING")) {
            // If spectator doesn't have same inventory open, open it
            if (spectatorView == null || 
                !spectatorView.getTopInventory().equals(targetView.getTopInventory())) {
                
                // Schedule to open on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (spectator.isOnline() && target.isOnline()) {
                        try {
                            spectator.openInventory(target.getInventory());
                        } catch (Exception e) {
                            // Silently handle - might be inventory already open
                        }
                    }
                });
            }
        } else if (spectatorView != null) {
            // If target closed inventory, close spectator's too
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (spectator.isOnline()) {
                    spectator.closeInventory();
                }
            });
        }
    }
}
