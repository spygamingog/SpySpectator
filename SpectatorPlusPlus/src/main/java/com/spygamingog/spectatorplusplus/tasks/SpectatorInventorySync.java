package com.spygamingog.spectatorplusplus.tasks;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        if (spectator.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return;
        }
        
        org.bukkit.inventory.InventoryView targetView = target.getOpenInventory();
        org.bukkit.inventory.InventoryView spectatorView = spectator.getOpenInventory();
        
        if (targetView != null && targetView.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING) {
            if (spectatorView == null || 
                !spectatorView.getTopInventory().equals(targetView.getTopInventory())) {
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (spectator.isOnline() && target.isOnline()) {
                        try {
                            spectator.openInventory(target.getInventory());
                        } catch (Exception e) {
                        }
                    }
                });
            }
        } else if (spectatorView != null && spectatorView.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (spectator.isOnline()) {
                    spectator.closeInventory();
                }
            });
        }
    }
}