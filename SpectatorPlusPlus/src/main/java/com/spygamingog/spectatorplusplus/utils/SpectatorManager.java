package com.spygamingog.spectatorplusplus.utils;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import com.spygamingog.spectatorplusplus.gui.SpectatorCompassGUI;
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
    private final Map<UUID, UUID> spectatingTargets;
    private final Map<UUID, Location> originalLocations;
    private final Map<UUID, GameMode> originalGameModes;
    private final Map<UUID, ItemStack[]> originalInventories;
    private final Map<UUID, ItemStack[]> originalArmor;
    private final Map<UUID, Boolean> spectatorVisibilityPrefs;
    private final Map<UUID, Boolean> spectatorChatPrefs;
    
    public SpectatorManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.worldSetManager = plugin.getWorldSetManager();
        this.visibilityManager = new VisibilityManager(this);
        this.spectators = new HashSet<>();
        this.spectatingTargets = new HashMap<>();
        this.originalLocations = new HashMap<>();
        this.originalGameModes = new HashMap<>();
        this.originalInventories = new HashMap<>();
        this.originalArmor = new HashMap<>();
        this.spectatorVisibilityPrefs = new HashMap<>();
        this.spectatorChatPrefs = new HashMap<>();
    }
    
    public void enterSpectator(Player player) {
        if (isSpectator(player)) {
            player.sendMessage(ChatColor.RED + "You are already in spectator mode!");
            return;
        }
        
        try {
            UUID playerId = player.getUniqueId();
            spectators.add(playerId);
            
            originalLocations.put(playerId, player.getLocation().clone());
            originalGameModes.put(playerId, player.getGameMode());
            originalInventories.put(playerId, player.getInventory().getContents().clone());
            originalArmor.put(playerId, player.getInventory().getArmorContents().clone());
            
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(configManager.canFly());
            player.setFlying(configManager.canFly());
            player.setFlySpeed(configManager.getFlySpeed());
            player.setWalkSpeed(configManager.getWalkSpeed());
            player.setCollidable(!configManager.passThroughEntities());
            player.setInvulnerable(true);
            player.setSilent(true);
        
            if (configManager.giveInvisibility()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
                    Integer.MAX_VALUE, 0, false, false, false));
            }
            
            player.getInventory().clear();
            giveSpectatorItems(player);
            
            if (configManager.giveNightVision()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 
                    Integer.MAX_VALUE, 0, false, false, false));
            }
            
            visibilityManager.updateAllVisibility();
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("enter-spectator")));
            player.sendMessage(ChatColor.GRAY + "Use compass to select players to spectate");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error entering spectator mode for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
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
            
            if (isSpectating(player)) {
                stopSpectating(player);
            }
            
            spectators.remove(playerId);
            
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
            
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            
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
            
            Location lobby = configManager.getLobbyLocation();
            if (lobby != null && lobby.getWorld() != null) {
                player.teleport(lobby);
                player.sendMessage(ChatColor.GREEN + "Teleported to spectator lobby!");
            } else if (originalLocations.containsKey(playerId)) {
                Location original = originalLocations.get(playerId);
                if (original != null && original.getWorld() != null) {
                    player.teleport(original);
                }
            }
            
            visibilityManager.updateAllVisibility();
            
            originalLocations.remove(playerId);
            originalGameModes.remove(playerId);
            originalInventories.remove(playerId);
            originalArmor.remove(playerId);
            spectatorVisibilityPrefs.remove(playerId);
            spectatorChatPrefs.remove(playerId);
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("leave-spectator")));
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error leaving spectator mode for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while leaving spectator mode!");
        }
    }
    
    public void spectatePlayer(Player spectator, Player target) {
        if (spectator == null || target == null) {
            if (spectator != null) spectator.sendMessage(ChatColor.RED + "Invalid target!");
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
            spectatingTargets.put(spectator.getUniqueId(), target.getUniqueId());
            
            Location previousLocation = spectator.getLocation().clone();
            originalLocations.put(spectator.getUniqueId(), previousLocation);
            
            spectator.setGameMode(GameMode.SPECTATOR);
            
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (spectator.isOnline() && target.isOnline() && isSpectating(spectator)) {
                    try {
                        spectator.setSpectatorTarget(target);
                        
                        spectator.teleport(target.getLocation());
                        
                        target.hidePlayer(plugin, spectator);
                        
                        spectator.sendMessage(ChatColor.GREEN + "Now spectating " + target.getName() + " (first-person view)");
                        spectator.sendMessage(ChatColor.GRAY + "Sneak to stop spectating");
                        
                        plugin.getLogger().info(spectator.getName() + " is now spectating " + target.getName());
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error setting spectator target: " + e.getMessage());
                        stopSpectating(spectator);
                        spectator.sendMessage(ChatColor.RED + "Failed to spectate player!");
                    }
                }
            }, 2L);
            
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

            spectatingTargets.remove(spectatorId);

            if (target != null && target.isOnline()) {
                target.showPlayer(plugin, spectator);
            }

            if (spectator.getGameMode() == GameMode.SPECTATOR) {
                try {
                    spectator.setSpectatorTarget(null);
                } catch (IllegalArgumentException e) {
                }
            }

            spectator.setGameMode(GameMode.ADVENTURE);

            restoreSpectatorProperties(spectator);

            spectator.sendMessage("§eStopped spectating" + 
                (target != null ? " " + target.getName() : ""));
        }
    }

    private void restoreSpectatorProperties(Player player) {
        if (isSpectator(player)) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.1f);
            player.setWalkSpeed(0.2f);
            player.setCollidable(false);
            player.setInvulnerable(true);
            player.setSilent(true);
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
                Integer.MAX_VALUE, 0, false, false, false));
                
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 
                Integer.MAX_VALUE, 0, false, false, false));
                
            giveSpectatorItems(player);
                
            visibilityManager.updateAllVisibility();
        }
    }
    
    private void giveSpectatorItems(Player player) {
        player.getInventory().clear();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName("§6Spectator Compass");
        compassMeta.setLore(Arrays.asList(
            "§7Right-click to select players to spectate",
            "§8Locked item"
        ));
        compass.setItemMeta(compassMeta);
        player.getInventory().setItem(configManager.getCompassSlot(), compass);

        ItemStack eye = new ItemStack(Material.ENDER_EYE);
        ItemMeta eyeMeta = eye.getItemMeta();
        eyeMeta.setDisplayName("§dToggle Spectator Visibility");
        eyeMeta.setLore(Arrays.asList(
            "§7Right-click to show/hide other spectators",
            "§7Current: §aVisible",
            "§8Locked item"
        ));
        eye.setItemMeta(eyeMeta);
        player.getInventory().setItem(configManager.getVisibilityToggleSlot(), eye);

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName("§bToggle Spectator Chat");
        paperMeta.setLore(Arrays.asList(
            "§7Right-click to enable/disable spectator chat",
            "§7Current: §aEnabled",
            "§8Locked item"
        ));
        paper.setItemMeta(paperMeta);
        player.getInventory().setItem(configManager.getChatToggleSlot(), paper);

        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = bed.getItemMeta();
        bedMeta.setDisplayName("§cLeave Spectator Mode");
        bedMeta.setLore(Arrays.asList(
            "§7Right-click to leave spectator mode",
            "§8Locked item"
        ));
        bed.setItemMeta(bedMeta);
        player.getInventory().setItem(configManager.getLeaveSlot(), bed);
    }
    
    public void toggleSpectatorVisibility(Player player) {
        UUID playerId = player.getUniqueId();
        boolean current = spectatorVisibilityPrefs.getOrDefault(playerId, true);
        boolean newValue = !current;
        
        spectatorVisibilityPrefs.put(playerId, newValue);
        
        updateVisibilityItem(player, newValue);
        
        if (visibilityManager != null) {
            visibilityManager.updateAllVisibility();
        }
    }

    public boolean isSpectatorVisibilityEnabled(Player player) {
        return spectatorVisibilityPrefs.getOrDefault(player.getUniqueId(), true);
    }
    
    public void setSpectatorVisibilityEnabled(Player player, boolean enabled) {
        spectatorVisibilityPrefs.put(player.getUniqueId(), enabled);
        updateVisibilityItem(player, enabled);
        if (visibilityManager != null) {
            visibilityManager.updateAllVisibility();
        }
    }

    private void updateVisibilityItem(Player player, boolean isVisible) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.ENDER_EYE && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName().contains("Toggle Spectator Visibility")) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Right-click to show/hide other spectators");
                    lore.add(ChatColor.GRAY + "Current: " + 
                        (isVisible ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
                    lore.add(ChatColor.DARK_GRAY + "Locked item - cannot be placed");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    
                    player.getInventory().setItem(i, item);
                    break;
                }
            }
        }
    }

    public void toggleSpectatorChat(Player player) {
        UUID playerId = player.getUniqueId();
        boolean current = spectatorChatPrefs.getOrDefault(playerId, true);
        boolean newValue = !current;
        
        spectatorChatPrefs.put(playerId, newValue);
        
        updateChatItem(player, newValue);
        
        if (newValue) {
            player.sendMessage(ChatColor.GREEN + "Spectator chat enabled");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Spectator chat disabled");
        }
    }
    private void updateChatItem(Player player, boolean isEnabled) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName().contains("Toggle Spectator Chat")) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Right-click to enable/disable spectator chat");
                    lore.add(ChatColor.GRAY + "Current: " + 
                        (isEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
                    lore.add(ChatColor.DARK_GRAY + "Locked item - cannot be placed");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    
                    player.getInventory().setItem(i, item);
                    break;
                }
            }
        }
    }
        
    public boolean canSeeSpectatorChat(Player player) {
        return spectatorChatPrefs.getOrDefault(player.getUniqueId(), true);
    }
    
    public Set<Player> getSpectators() {
        Set<Player> spectatorPlayers = new HashSet<>();
        
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                spectatorPlayers.add(player);
            }
        }
        
        return spectatorPlayers;
    }
    
    public void disableAllSpectators() {
        Set<Player> spectatorPlayers = getSpectators();
        
        for (Player spectator : spectatorPlayers) {
            try {
                leaveSpectator(spectator);
            } catch (Exception e) {
                plugin.getLogger().warning("Error disabling spectator " + spectator.getName() + ": " + e.getMessage());
            }
        }
        
        spectators.clear();
        spectatingTargets.clear();
        originalLocations.clear();
        originalGameModes.clear();
        originalInventories.clear();
        originalArmor.clear();
        spectatorVisibilityPrefs.clear();
        spectatorChatPrefs.clear();
        
        if (visibilityManager != null) {
            visibilityManager.cleanup();
        }
    }
    
    public boolean isActuallySpectator(Player player) {
        UUID playerId = player.getUniqueId();
        
        boolean hasItems = false;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && item.hasItemMeta()) {
                String name = item.getItemMeta().getDisplayName();
                if (name != null && (name.contains("Spectator Compass") || 
                    name.contains("Leave Spectator Mode"))) {
                    hasItems = true;
                    break;
                }
            }
        }
        
        return spectators.contains(playerId) || hasItems;
    }

    public boolean isSpectator(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (spectators.contains(playerId)) {
            return true;
        }
        
        boolean hasSpectatorItems = false;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName()) {
                    String name = meta.getDisplayName();
                    if (name.contains("Spectator Compass") || 
                        name.contains("Leave Spectator Mode") ||
                        name.contains("Toggle Spectator Visibility") ||
                        name.contains("Toggle Spectator Chat")) {
                        hasSpectatorItems = true;
                        break;
                    }
                }
            }
        }
        
        boolean isSpectatingSomeone = spectatingTargets.containsKey(playerId);
        
        return hasSpectatorItems || isSpectatingSomeone;
    }
    
    public boolean isSpectating(Player player) {
        return spectatingTargets.containsKey(player.getUniqueId());
    }
    
    public void forceCleanup(Player player) {
        UUID playerId = player.getUniqueId();
        
        spectators.remove(playerId);
        spectatingTargets.remove(playerId);
        originalLocations.remove(playerId);
        originalGameModes.remove(playerId);
        originalInventories.remove(playerId);
        originalArmor.remove(playerId);
        spectatorVisibilityPrefs.remove(playerId);
        spectatorChatPrefs.remove(playerId);
        
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setCollidable(true);
        player.setInvulnerable(false);
        player.setSilent(false);
        
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        
        player.getInventory().clear();
        
        visibilityManager.updateAllVisibility();
        
        plugin.getLogger().warning("Force cleaned spectator state for " + player.getName());
    }

    public Player getSpectatingTarget(Player spectator) {
        UUID targetId = spectatingTargets.get(spectator.getUniqueId());
        return targetId != null ? Bukkit.getPlayer(targetId) : null;
    }
    
    public SpectatorCompassGUI getSpectatorCompassGUI() {
        return new SpectatorCompassGUI(plugin);
    }
    
    public SpectatorPlusPlus getPlugin() {
        return plugin;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public WorldSetManager getWorldSetManager() {
        return worldSetManager;
    }
    
    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
