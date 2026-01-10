package com.spygamingog.spectatorplusplus.utils;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectatorCameraManager {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    private final Map<UUID, CameraFollowTask> cameraTasks;
    
    public SpectatorCameraManager(SpectatorPlusPlus plugin, SpectatorManager spectatorManager) {
        this.plugin = plugin;
        this.spectatorManager = spectatorManager;
        this.cameraTasks = new HashMap<>();
    }
    
    public void startFirstPersonView(Player spectator, Player target) {
        UUID spectatorId = spectator.getUniqueId();
        
        stopFirstPersonView(spectator);
        
        spectator.setGameMode(org.bukkit.GameMode.SPECTATOR);
        spectator.setSpectatorTarget(target);
        
        target.hidePlayer(plugin, spectator);
        
        CameraFollowTask task = new CameraFollowTask(spectator, target);
        task.runTaskTimer(plugin, 0L, 1L);
        cameraTasks.put(spectatorId, task);
        
        spectator.sendMessage(org.bukkit.ChatColor.GREEN + "Now in first-person view of " + target.getName());
        spectator.sendMessage(org.bukkit.ChatColor.GRAY + "Sneak to exit");
    }
    
    public void stopFirstPersonView(Player spectator) {
        UUID spectatorId = spectator.getUniqueId();
        
        if (cameraTasks.containsKey(spectatorId)) {
            cameraTasks.get(spectatorId).cancel();
            cameraTasks.remove(spectatorId);
        }
        
        spectator.setGameMode(org.bukkit.GameMode.ADVENTURE);
        spectator.setSpectatorTarget(null);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(spectator)) {
                player.showPlayer(plugin, spectator);
            }
        }
    }
    
    public boolean isInFirstPersonView(Player spectator) {
        return cameraTasks.containsKey(spectator.getUniqueId());
    }
    
    private class CameraFollowTask extends BukkitRunnable {
        private final Player spectator;
        private final Player target;
        
        public CameraFollowTask(Player spectator, Player target) {
            this.spectator = spectator;
            this.target = target;
        }
        
        @Override
        public void run() {
            if (!spectator.isOnline() || !target.isOnline()) {
                this.cancel();
                return;
            }
            
            Location targetEye = target.getEyeLocation().clone();
            
            spectator.teleport(targetEye);
            
            if (target.getOpenInventory() != null) {
                if (spectator.getOpenInventory() == null || 
                    !spectator.getOpenInventory().getTopInventory().equals(target.getOpenInventory().getTopInventory())) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (spectator.isOnline() && target.isOnline()) {
                            spectator.openInventory(target.getInventory());
                        }
                    });
                }
            } else if (spectator.getOpenInventory() != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (spectator.isOnline()) {
                        spectator.closeInventory();
                    }
                });
            }
        }
    }
}