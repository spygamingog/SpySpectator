package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlightListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    private final Map<UUID, Long> lastJumpTime;
    private final Map<UUID, Boolean> isFlying;
    private static final long DOUBLE_JUMP_THRESHOLD = 300;
    
    public FlightListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
        this.lastJumpTime = new HashMap<>();
        this.isFlying = new HashMap<>();
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        event.setCancelled(true);
        
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastJump = lastJumpTime.get(playerId);
        
        if (lastJump != null && (currentTime - lastJump) < DOUBLE_JUMP_THRESHOLD) {
            toggleFlight(player);
            lastJumpTime.remove(playerId);
        } else {
            lastJumpTime.put(playerId, currentTime);
            
            Vector velocity = player.getVelocity();
            velocity.setY(0.5);
            player.setVelocity(velocity);
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) return;
        
        lastJumpTime.remove(player.getUniqueId());
    }
    
    private void toggleFlight(Player player) {
        UUID playerId = player.getUniqueId();
        boolean currentlyFlying = isFlying.getOrDefault(playerId, true);
        boolean newFlightState = !currentlyFlying;
        
        isFlying.put(playerId, newFlightState);
        player.setFlying(newFlightState);
        
        if (newFlightState) {
            player.setAllowFlight(true);
            player.setFlySpeed(0.1f);
            player.sendMessage(ChatColor.GREEN + "Flight enabled");
        } else {
            player.setFlySpeed(0.0f);
            player.sendMessage(ChatColor.YELLOW + "Flight disabled - double jump to fly again");
        }
    }
    
    public void resetPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        lastJumpTime.remove(playerId);
        isFlying.remove(playerId);
    }
}