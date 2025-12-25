package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    private final WorldSetManager worldSetManager;
    
    public WorldListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
        this.worldSetManager = plugin.getWorldSetManager();
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
    
        if (!spectatorManager.isSpectator(player)) return;
    
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) {
            event.setCancelled(true);
            return;
        }
    
        String displayName = item.getItemMeta().getDisplayName();
    
        // Allow compass and bed interactions
        if ((item.getType() == Material.COMPASS && displayName.contains("Spectator Compass")) ||
            (item.getType() == Material.RED_BED && displayName.contains("Leave Spectator Mode"))) {
            // These are handled by PlayerListener
            return;
        }
    
    // Block all other interactions
        event.setCancelled(true);
    }
    
    private boolean isOpenableBlock(Material material) {
        return material == Material.OAK_DOOR || material == Material.SPRUCE_DOOR || 
               material == Material.BIRCH_DOOR || material == Material.JUNGLE_DOOR ||
               material == Material.ACACIA_DOOR || material == Material.DARK_OAK_DOOR ||
               material == Material.MANGROVE_DOOR || material == Material.CHERRY_DOOR ||
               material == Material.BAMBOO_DOOR || material == Material.CRIMSON_DOOR ||
               material == Material.WARPED_DOOR || material == Material.IRON_DOOR ||
               material == Material.OAK_TRAPDOOR || material == Material.SPRUCE_TRAPDOOR ||
               material == Material.BIRCH_TRAPDOOR || material == Material.JUNGLE_TRAPDOOR ||
               material == Material.ACACIA_TRAPDOOR || material == Material.DARK_OAK_TRAPDOOR ||
               material == Material.MANGROVE_TRAPDOOR || material == Material.CHERRY_TRAPDOOR ||
               material == Material.BAMBOO_TRAPDOOR || material == Material.CRIMSON_TRAPDOOR ||
               material == Material.WARPED_TRAPDOOR || material == Material.IRON_TRAPDOOR ||
               material == Material.OAK_FENCE_GATE || material == Material.SPRUCE_FENCE_GATE ||
               material == Material.BIRCH_FENCE_GATE || material == Material.JUNGLE_FENCE_GATE ||
               material == Material.ACACIA_FENCE_GATE || material == Material.DARK_OAK_FENCE_GATE ||
               material == Material.MANGROVE_FENCE_GATE || material == Material.CHERRY_FENCE_GATE ||
               material == Material.BAMBOO_FENCE_GATE || material == Material.CRIMSON_FENCE_GATE ||
               material == Material.WARPED_FENCE_GATE;
    }
    
    private boolean isBlockOpen(Block block) {
        // Check if door/trapdoor/gate is open
        org.bukkit.block.data.Openable openable = (org.bukkit.block.data.Openable) block.getBlockData();
        return openable.isOpen();
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        
        if (damager instanceof Player) {
            Player player = (Player) damager;
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (spectatorManager.isSpectator(player)) {
                event.setCancelled(true);
                player.setFoodLevel(20); // Keep food level full
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            // Allow spectators to toggle flight freely
            event.setCancelled(false);
            
            if (event.isFlying()) {
                player.setFlySpeed(0.1f); // Normal creative flight speed
                player.sendMessage(ChatColor.GRAY + "Flight enabled");
            } else {
                player.setWalkSpeed(0.2f);
                player.sendMessage(ChatColor.GRAY + "Flight disabled");
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            // Spectators can walk normally, no block collision check needed
            // Just ensure they can pass through entities (handled by setCollidable(false))
        }
    }
    
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            // Allow spectators to use portals
            event.setCancelled(false);
            
            // Update visibility after world change
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                spectatorManager.getVisibilityManager().handleWorldChange(player);
            }, 5L);
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player) && !spectatorManager.isSpectating(player)) {
            // Prevent teleport commands while in spectator mode
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND ||
                event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
                // Only allow if teleporting to spectate someone
                boolean isSpectatingTeleport = false;
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (event.getTo().distanceSquared(p.getLocation()) < 4) {
                        isSpectatingTeleport = true;
                        break;
                    }
                }
                
                if (!isSpectatingTeleport) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Cannot teleport while in spectator mode!");
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Update visibility for the new player
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            spectatorManager.getVisibilityManager().handlePlayerJoin(player);
        }, 10L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            // Clean up when spectator leaves
            spectatorManager.leaveSpectator(player);
        }
        
        // Use VisibilityManager
        spectatorManager.getVisibilityManager().handlePlayerQuit(player);
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        
        // Use VisibilityManager
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            spectatorManager.getVisibilityManager().handleWorldChange(player);
            
            // If spectator, update compass available players
            if (spectatorManager.isSpectator(player) && !spectatorManager.isSpectating(player)) {
                // Refresh compass GUI if open
                if (player.getOpenInventory() != null && 
                    player.getOpenInventory().getTitle().contains("Spectate Players")) {
                    player.closeInventory();
                    spectatorManager.getPlayerSelectorGUI().open(player);
                }
            }
        }, 5L);
    }
}
