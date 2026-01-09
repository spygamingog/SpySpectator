package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class EntityListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public EntityListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Spectators cannot interact with entities!");
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (spectatorManager.isSpectator(damager)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Spectators cannot shear entities!");
        }
    }
    
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Spectators cannot ride vehicles!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Spectators cannot use beds!");
        }
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Spectators cannot consume items!");
        }
    }
}
