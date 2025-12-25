package com.spygamingog.spectatorplusplus.utils;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import com.spygamingog.spectatorplusplus.gui.PlayerSelectorGUI;
import com.spygamingog.spectatorplusplus.tasks.SpectatorFollowTask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpectatorManager {
    private final SpectatorPlusPlus plugin;
    private final ConfigManager configManager;
    private final WorldSetManager worldSetManager;
    private final VisibilityManager visibilityManager;
    private final Set<UUID> spectators;
    private final Map<UUID, UUID> spectatingTargets; // Spectator -> Target
    private final Map<UUID, SpectatorFollowTask> followTasks;
    private final Map<UUID, Location> originalLocations;
    private final Map<UUID, GameMode> originalGameModes;
    private final Map<UUID, ItemStack[]> originalInventories;
    private final Map<UUID, ItemStack[]> originalArmor;
    
    public SpectatorManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.worldSetManager = plugin.getWorldSetManager();
        this.visibilityManager = new VisibilityManager(this);
        this.spectators = new HashSet<>();
        this.spectatingTargets = new HashMap<>();
        this.followTasks = new HashMap<>();
        this.originalLocations = new HashMap<>();
        this.originalGameModes = new HashMap<>();
        this.originalInventories = new HashMap<>();
        this.originalArmor = new HashMap<>();
    }
    
    public void enterSpectator(Player player) {
        if (isSpectator(player)) {
            player.sendMessage(ChatColor.RED + "You are already in spectator mode!");
            return;
        }
        
        try {
            UUID playerId = player.getUniqueId();
            spectators.add(playerId);
            
            // Save original state
            originalLocations.put(playerId, player.getLocation().clone());
            originalGameModes.put(playerId, player.getGameMode());
            originalInventories.put(playerId, player.getInventory().getContents().clone());
            originalArmor.put(playerId, player.getInventory().getArmorContents().clone());
            
            // Set spectator properties
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.1f);
            player.setWalkSpeed(0.2f);
            player.setCollidable(false);
            player.setInvulnerable(true);
            player.setSilent(true);
            
            // Add invisibility effect to hide from mobs
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
                Integer.MAX_VALUE, 0, false, false, false));
            
            // Clear inventory and give locked spectator items
            player.getInventory().clear();
            giveSpectatorItems(player);
            
            // Add night vision for better visibility
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 
                Integer.MAX_VALUE, 0, false, false, false));
            
            // FIX: Spectators should see normal players, but normal players shouldn't see spectators
            updateSpectatorVisibilityForAll();
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("enter-spectator")));
            player.sendMessage(ChatColor.GRAY + "Use compass to select players to spectate");
            plugin.getLogger().info(player.getName() + " entered spectator mode");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error entering spectator mode for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            if (isSpectator(player)) {
                spectators.remove(player.getUniqueId());
            }
            player.sendMessage(ChatColor.RED + "An error occurred while entering spectator mode!");
        }
    }
    
    public void leaveSpectator(Player player) {
        if (!isSpectator(player)) {
            player.sendMessage(ChatColor.RED + "You are not in spectator mode!");
            return;
        }
        
        try {
            UUID playerId = player.getUniqueId();
            spectators.remove(playerId);
            
            // Stop spectating if currently spectating someone
            if (isSpectating(player)) {
                stopSpectating(player);
            }
            
            // Cancel any follow task
            if (followTasks.containsKey(playerId)) {
                followTasks.get(playerId).cancel();
                followTasks.remove(playerId);
            }
            
            // Restore original state
            if (originalGameModes.containsKey(playerId)) {
                player.setGameMode(originalGameModes.get(playerId));
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
            
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFlySpeed(0.1f);
            player.setWalkSpeed(0.2f);
            player.setCollidable(true);
            player.setInvulnerable(false);
            player.setSilent(false);
            
            // Remove invisibility effect
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            
            // Clear inventory and restore original
            player.getInventory().clear();
            if (originalInventories.containsKey(playerId)) {
                ItemStack[] inventory = originalInventories.get(playerId);
                if (inventory != null) {
                    player.getInventory().setContents(inventory);
                }
            }
            if (originalArmor.containsKey(playerId)) {
                ItemStack[] armor = originalArmor.get(playerId);
                if (armor != null) {
                    player.getInventory().setArmorContents(armor);
                }
            }
            
            // Remove night vision
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            
            // Teleport to lobby if exists, otherwise original location
            Location lobby = configManager.getLobbyLocation();
            if (lobby != null && lobby.getWorld() != null) {
                player.teleport(lobby);
            } else if (originalLocations.containsKey(playerId)) {
                Location original = originalLocations.get(playerId);
                if (original != null && original.getWorld() != null) {
                    player.teleport(original);
                }
            }
            
            // Update visibility for all players
            updateSpectatorVisibilityForAll();
            
            // Clean up
            originalLocations.remove(playerId);
            originalGameModes.remove(playerId);
            originalInventories.remove(playerId);
            originalArmor.remove(playerId);
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("leave-spectator")));
            plugin.getLogger().info(player.getName() + " left spectator mode");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error leaving spectator mode for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            spectators.remove(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "An error occurred while leaving spectator mode!");
        }
    }
    
    public void spectatePlayer(Player spectator, Player target) {
        if (!isSpectator(spectator)) {
            spectator.sendMessage(ChatColor.RED + "You must be in spectator mode to spectate!");
            return;
        }
        
        if (spectator.equals(target)) {
            spectator.sendMessage(ChatColor.RED + "You cannot spectate yourself!");
            return;
        }
        
        if (isSpectator(target)) {
            spectator.sendMessage(ChatColor.RED + "You cannot spectate another spectator!");
            return;
        }
        
        try {
            UUID spectatorId = spectator.getUniqueId();
            
            // Stop any existing spectating
            if (isSpectating(spectator)) {
                stopSpectating(spectator);
            }
            
            spectatingTargets.put(spectatorId, target.getUniqueId());
            
            // Set to adventure mode for proper viewing
            spectator.setGameMode(GameMode.ADVENTURE);
            spectator.setAllowFlight(true);
            spectator.setFlying(false);
            
            // Teleport to target's eye location for first-person view
            Location targetLoc = target.getEyeLocation().clone();
            spectator.teleport(targetLoc);
            
            // Make spectator invisible to target
            target.hidePlayer(plugin, spectator);
            
            // Start follow task with first-person view
            SpectatorFollowTask followTask = new SpectatorFollowTask(plugin, spectator, target);
            followTask.runTaskTimer(plugin, 0L, 1L);
            followTasks.put(spectatorId, followTask);
            
            // Send message
            spectator.sendMessage(ChatColor.GREEN + "Now spectating " + target.getName() + " (first-person view)");
            spectator.sendMessage(ChatColor.GRAY + "Sneak to stop spectating");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error spectating player: " + e.getMessage());
            e.printStackTrace();
            spectator.sendMessage(ChatColor.RED + "Failed to spectate player!");
        }
    }
    
    public void stopSpectating(Player spectator) {
        UUID spectatorId = spectator.getUniqueId();
        if (spectatingTargets.containsKey(spectatorId)) {
            Player target = getSpectatingTarget(spectator);
            
            // Cancel follow task
            if (followTasks.containsKey(spectatorId)) {
                followTasks.get(spectatorId).cancel();
                followTasks.remove(spectatorId);
            }
            
            spectatingTargets.remove(spectatorId);
            
            // Close any open inventory
            if (spectator.getOpenInventory() != null) {
                spectator.closeInventory();
            }
            
            // Make spectator visible to target again
            if (target != null && target.isOnline()) {
                target.showPlayer(plugin, spectator);
            }
            
            // Re-enable flight
            spectator.setFlying(true);
            spectator.setFlySpeed(0.1f);
            
            // Send message
            spectator.sendMessage(ChatColor.YELLOW + "Stopped spectating" + 
                (target != null ? " " + target.getName() : ""));
        }
    }
    
    private void giveSpectatorItems(Player player) {
        // Compass for player selection (locked in hotbar)
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        if (compassMeta != null) {
            compassMeta.setDisplayName(ChatColor.GOLD + "Spectator Compass");
            compassMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Right-click to open player selector",
                ChatColor.GRAY + "Select a player to spectate",
                ChatColor.DARK_GRAY + "Locked in hotbar"
            ));
            // Make item unbreakable and add custom tag
            compassMeta.setUnbreakable(true);
            compass.setItemMeta(compassMeta);
        }
        
        // Leave spectator item (locked in hotbar)
        ItemStack leaveItem = new ItemStack(Material.RED_BED);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        if (leaveMeta != null) {
            leaveMeta.setDisplayName(ChatColor.RED + "Leave Spectator Mode");
            leaveMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Right-click to leave spectator mode",
                ChatColor.GRAY + "You will be teleported to lobby",
                ChatColor.DARK_GRAY + "Locked in hotbar"
            ));
            leaveMeta.setUnbreakable(true);
            leaveItem.setItemMeta(leaveMeta);
        }
        
        // Set items in fixed positions (can't be moved)
        player.getInventory().setItem(0, compass);
        player.getInventory().setItem(8, leaveItem);
    }
    
    public void updateSpectatorVisibilityForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateSpectatorVisibility(player);
        }
    }
    
    private void updateSpectatorVisibility(Player player) {
        if (isSpectator(player)) {
            // Spectator: can see everyone, but others can't see spectator
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                
                // Spectator can see everyone
                player.showPlayer(plugin, other);
                
                // Others can't see spectator unless they're also spectator/admin
                if (!isSpectator(other) && !other.hasPermission("spectatorplusplus.admin")) {
                    other.hidePlayer(plugin, player);
                } else {
                    other.showPlayer(plugin, player);
                }
            }
        } else {
            // Non-spectator: can't see spectators unless admin
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                
                if (isSpectator(other) && !player.hasPermission("spectatorplusplus.admin")) {
                    player.hidePlayer(plugin, other);
                } else {
                    player.showPlayer(plugin, other);
                }
            }
        }
    }
    
    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }
    
    public boolean isSpectating(Player player) {
        return spectatingTargets.containsKey(player.getUniqueId());
    }
    
    public Player getSpectatingTarget(Player spectator) {
        UUID targetId = spectatingTargets.get(spectator.getUniqueId());
        return targetId != null ? Bukkit.getPlayer(targetId) : null;
    }
    
    public Set<Player> getSpectators() {
        Set<Player> specPlayers = new HashSet<>();
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                specPlayers.add(player);
            }
        }
        return specPlayers;
    }
    
    public Set<UUID> getSpectatorUUIDs() {
        return new HashSet<>(spectators);
    }
    
    public Map<UUID, UUID> getSpectatingTargets() {
        return new HashMap<>(spectatingTargets);
    }
    
    public void disableAllSpectators() {
        List<Player> spectatorsList = new ArrayList<>(getSpectators());
        for (Player player : spectatorsList) {
            try {
                leaveSpectator(player);
            } catch (Exception e) {
                plugin.getLogger().warning("Error disabling spectator for " + player.getName() + ": " + e.getMessage());
            }
        }
        if (visibilityManager != null) {
            visibilityManager.cleanup();
        }
    }
    
    public PlayerSelectorGUI getPlayerSelectorGUI() {
        return new PlayerSelectorGUI(plugin);
    }
    
    public SpectatorPlusPlus getPlugin() {
        return plugin;
    }
    
    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
