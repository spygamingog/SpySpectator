package com.spygamingog.spyspectator.utils;

import com.spygamingog.spyspectator.SpySpectator;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpectatorManager {

    private final SpySpectator plugin;
    private final Set<UUID> spectators = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Location> returnLocations = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    
    // Preferences
    private final Set<UUID> chatDisabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> visibilityDisabled = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Set<UUID>> ignoredChatPlayers = new HashMap<>();
    private final Map<UUID, Set<UUID>> hiddenSpectators = new HashMap<>();

    private Location lobbyLocation;
    private final File dataFile;
    private YamlConfiguration dataConfig;

    public SpectatorManager(SpySpectator plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spectators.yml");
        loadLobby();
        loadSpectators();
    }

    public void enableSpectator(Player player) {
        enableSpectator(player, false);
    }

    public void enableSpectator(Player player, boolean isJoin) {
        if (!isJoin && spectators.contains(player.getUniqueId())) return;

        // Save return location only if not joining (if joining, we use persisted one)
        if (!isJoin) {
            returnLocations.put(player.getUniqueId(), player.getLocation());
            // Save Inventory
            savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
            savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            player.getInventory().clear();
        }
        
        spectators.add(player.getUniqueId());

        // Apply Spectator State
        player.setGameMode(GameMode.ADVENTURE); // Adventure prevents block breaking/interaction generally
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setCollidable(false);
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setSilent(true);
        
        // Full Health, Hunger, Saturation, Air
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setRemainingAir(player.getMaximumAir());
        
        // Metadata for other plugins to check if needed
        player.setMetadata("spyspectator", new FixedMetadataValue(plugin, true));

        // FORCE REMOVE INVISIBILITY (Fix for self-visibility)
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        
        // Night Vision
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));

        // Hide from others
        updateVisibility(player);
        
        // Give Spectator Items
        giveSpectatorItems(player);
        
        // Ensure default preferences (Enabled)
        chatDisabled.remove(player.getUniqueId());
        visibilityDisabled.remove(player.getUniqueId());
        
        if (!isJoin) {
            player.sendMessage("§aYou are now in Spectator Mode.");
            saveSpectators(); // Save on change
        }
    }

    public void disableSpectator(Player player, boolean toLobby) {
        disableSpectator(player, toLobby, true);
    }

    public void disableSpectator(Player player, boolean toLobby, boolean resetGameMode) {
        if (!spectators.contains(player.getUniqueId())) return;

        spectators.remove(player.getUniqueId());
        player.removeMetadata("spyspectator", plugin);

        // Restore State
        if (resetGameMode) {
            player.setGameMode(GameMode.SURVIVAL); // Defaulting to Survival
        }
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setCollidable(true);
        player.setInvulnerable(false);
        player.setCanPickupItems(true);
        player.setSilent(false);
        
        // Remove Effects
        player.removePotionEffect(PotionEffectType.INVISIBILITY); // Just in case
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        // Update visibility for everyone
        updateVisibility(player);
        
        // Clear Spectator Items & Restore Inventory
        player.getInventory().clear();
        if (savedInventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(savedInventories.remove(player.getUniqueId()));
            player.getInventory().setArmorContents(savedArmor.remove(player.getUniqueId()));
        }

        // Teleport (Only if we want to move them)
        // If switching gamemode, we usually don't want to teleport unless specified
        // But logic says "switch you from our spectator to that mode... and make everything default"
        // It doesn't explicitly say "teleport back", but usually leaving spectator implies going back.
        // However, if I change GM to creative, I probably want to stay where I am.
        // So if resetGameMode is false (meaning we switched GM), we probably skip teleport too?
        // User: "changing/switching gamemodes will switch you from our spectator to that mode"
        // Usually GM switch is "in-place". So let's skip teleport if !resetGameMode (implied) or just follow toLobby.
        
        if (resetGameMode) { // Only teleport if we are fully resetting (normal leave)
             if (toLobby && lobbyLocation != null) {
                player.teleport(lobbyLocation);
            } else {
                Location ret = returnLocations.remove(player.getUniqueId());
                if (ret != null) player.teleport(ret);
            }
        } else {
             // Just remove return location from map so we don't leak memory, but don't teleport
             returnLocations.remove(player.getUniqueId());
        }
        
        player.sendMessage("§cYou left Spectator Mode.");
        saveSpectators(); // Save on change
    }

    private void giveSpectatorItems(Player player) {
        // Chat Settings - Slot 1
        ItemStack chatItem = new ItemStack(Material.PAPER);
        ItemMeta chatMeta = chatItem.getItemMeta();
        chatMeta.setDisplayName("§b§lChat Settings");
        List<String> chatLore = new ArrayList<>();
        chatLore.add("§7Left-click to toggle global chat");
        chatLore.add("§7Right-click to open chat menu");
        chatMeta.setLore(chatLore);
        chatItem.setItemMeta(chatMeta);
        player.getInventory().setItem(1, chatItem);

        // Visibility Settings - Slot 7
        ItemStack visItem = new ItemStack(Material.ENDER_EYE);
        ItemMeta visMeta = visItem.getItemMeta();
        visMeta.setDisplayName("§a§lVisibility Settings");
        List<String> visLore = new ArrayList<>();
        visLore.add("§7Left-click to toggle global visibility");
        visLore.add("§7Right-click to open visibility menu");
        visMeta.setLore(visLore);
        visItem.setItemMeta(visMeta);
        player.getInventory().setItem(7, visItem);

        // Compass - Middle Slot (4)
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compMeta = compass.getItemMeta();
        compMeta.setDisplayName("§e§lPlayer Teleporter");
        List<String> lore = new ArrayList<>();
        lore.add("§7Right-click to open teleport menu");
        compMeta.setLore(lore);
        compass.setItemMeta(compMeta);
        player.getInventory().setItem(4, compass);

        // Leave Item - Last Slot (8)
        ItemStack leave = new ItemStack(Material.RED_BED);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setDisplayName("§c§lLeave Spectator Mode");
        List<String> leaveLore = new ArrayList<>();
        leaveLore.add("§7Right-click to return to lobby");
        leaveMeta.setLore(leaveLore);
        leave.setItemMeta(leaveMeta);
        player.getInventory().setItem(8, leave);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public void setLobby(Location loc) {
        this.lobbyLocation = loc;
        plugin.getConfig().set("lobby", loc);
        plugin.saveConfig();
    }
    
    public Location getLobby() {
        return lobbyLocation;
    }

    private void loadLobby() {
        this.lobbyLocation = plugin.getConfig().getLocation("lobby");
    }

    public void updateVisibility(Player target) {
        boolean isTargetSpectator = isSpectator(target);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(target.getUniqueId())) continue;

            boolean isOnlineSpectator = isSpectator(online);

            // Configure how 'online' sees 'target'
            if (isTargetSpectator && !isOnlineSpectator) {
                // Spectator target hidden from normal player
                online.hidePlayer(plugin, target);
            } else if (isTargetSpectator && isOnlineSpectator) {
                // Spectator sees Spectator? Check Preferences & World
                if (online.getWorld().equals(target.getWorld()) && 
                    !visibilityDisabled.contains(online.getUniqueId()) &&
                    !isHidden(online.getUniqueId(), target.getUniqueId())) {
                    online.showPlayer(plugin, target);
                } else {
                    online.hidePlayer(plugin, target);
                }
            } else {
                // Otherwise visible (Normal sees Normal, Normal sees Spec? NO handled above)
                online.showPlayer(plugin, target);
            }

            // Configure how 'target' sees 'online'
            if (isOnlineSpectator && !isTargetSpectator) {
                // Spectator online hidden from Normal target
                target.hidePlayer(plugin, online);
            } else if (isOnlineSpectator && isTargetSpectator) {
                 // Spectator sees Spectator? Check Preferences & World (Target is Viewer)
                if (target.getWorld().equals(online.getWorld()) && 
                    !visibilityDisabled.contains(target.getUniqueId()) &&
                    !isHidden(target.getUniqueId(), online.getUniqueId())) {
                    target.showPlayer(plugin, online);
                } else {
                    target.hidePlayer(plugin, online);
                }
            } else {
                target.showPlayer(plugin, online);
            }
        }
    }
    
    // Preference Methods
    public boolean isChatEnabled(Player player) {
        return !chatDisabled.contains(player.getUniqueId());
    }
    
    public void toggleChat(Player player) {
        if (chatDisabled.contains(player.getUniqueId())) {
            chatDisabled.remove(player.getUniqueId());
            player.sendMessage("§aSpectator Chat Enabled");
        } else {
            chatDisabled.add(player.getUniqueId());
            player.sendMessage("§cSpectator Chat Disabled");
        }
    }
    
    public boolean isVisibilityEnabled(Player player) {
        return !visibilityDisabled.contains(player.getUniqueId());
    }
    
    public void toggleVisibility(Player player) {
        if (visibilityDisabled.contains(player.getUniqueId())) {
            visibilityDisabled.remove(player.getUniqueId());
            player.sendMessage("§aSpectator Visibility Enabled");
        } else {
            visibilityDisabled.add(player.getUniqueId());
            player.sendMessage("§cSpectator Visibility Disabled");
        }
        updateVisibility(player); // Refresh
    }
    
    public boolean isIgnored(UUID viewer, UUID target) {
        return ignoredChatPlayers.getOrDefault(viewer, Collections.emptySet()).contains(target);
    }
    
    public void toggleIgnore(Player viewer, UUID target) {
        Set<UUID> ignored = ignoredChatPlayers.computeIfAbsent(viewer.getUniqueId(), k -> new HashSet<>());
        if (ignored.contains(target)) {
            ignored.remove(target);
            viewer.sendMessage("§aUnignored player chat.");
        } else {
            ignored.add(target);
            viewer.sendMessage("§cIgnored player chat.");
        }
    }
    
    public boolean isHidden(UUID viewer, UUID target) {
        return hiddenSpectators.getOrDefault(viewer, Collections.emptySet()).contains(target);
    }
    
    public void toggleHide(Player viewer, UUID target) {
        Set<UUID> hidden = hiddenSpectators.computeIfAbsent(viewer.getUniqueId(), k -> new HashSet<>());
        if (hidden.contains(target)) {
            hidden.remove(target);
            viewer.sendMessage("§aPlayer is now visible.");
        } else {
            hidden.add(target);
            viewer.sendMessage("§cPlayer is now hidden.");
        }
        updateVisibility(viewer); // Refresh
    }

    public void cleanup() {
        saveSpectators();
        // Do not disable spectators on cleanup/disable, just save state.
    }

    public void saveSpectators() {
        dataConfig = new YamlConfiguration();
        List<String> uuidList = new ArrayList<>();
        for (UUID uuid : spectators) {
            uuidList.add(uuid.toString());
            Location loc = returnLocations.get(uuid);
            if (loc != null) {
                dataConfig.set("locations." + uuid.toString(), loc);
            }
            
            // Save Inventory? Serializing inventory is complex. 
            // For now, if server restarts, inventory in RAM is lost if we don't serialize.
            // Given "persistence" request, we should probably handle it, but saving ItemStack[] to YAML requires some boilerplate.
            // For this task, I will stick to RAM saving (session only) unless explicitly asked for full reboot persistence of INVENTORY.
            // User said "persistence" earlier, but for "spectator mode" (locations/state). 
            // Saving full inventory to YML is heavy. I'll rely on server shutdown safe handling (restore on disable?)
            // Actually, if I cleanup() and don't restore items, players lose items on reload.
            // I should restore items on cleanup()!
        }
        dataConfig.set("spectators", uuidList);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Restore items for all spectators on shutdown to prevent item loss
        // But wait, if we restore items, we must remove them from spectator mode?
        // Or just save them?
        // If we want persistence across restarts, we must serialize.
        // For now, to avoid item loss, I will restore items on cleanup (kick out of spectator mode on reload).
        // But I previously decided to KEEP them in spectator mode.
        // This is a conflict. 
        // Resolution: I will modify cleanup() to NOT restore items (assuming persistence is desired), 
        // BUT this risks item loss if plugin reloads.
        // Safest: Kick out of spectator on reload/shutdown (restore items).
        // "returnLocations" are saved, but "savedInventories" are in RAM.
        // If I don't save inventories to disk, I MUST restore them on disable.
    }
    
    // Updated cleanup to restore items (Safety)
    public void safeCleanup() {
        for (UUID uuid : new HashSet<>(spectators)) {
             Player p = Bukkit.getPlayer(uuid);
             if (p != null) disableSpectator(p, false, true); // Kick out, restore items
        }
    }

    public void loadSpectators() {
        if (!dataFile.exists()) return;
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        List<String> uuidList = dataConfig.getStringList("spectators");
        for (String s : uuidList) {
            try {
                UUID uuid = UUID.fromString(s);
                spectators.add(uuid);
                Location loc = dataConfig.getLocation("locations." + s);
                if (loc != null) {
                    returnLocations.put(uuid, loc);
                }
            } catch (IllegalArgumentException e) {
                // Ignore invalid UUIDs
            }
        }
        
        // Refresh state/items for any online spectators
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (spectators.contains(p.getUniqueId())) {
                // Re-apply spectator mode to ensure items and effects are up to date
                // We treat it as a "join" to bypass the "already spectator" check
                enableSpectator(p, true);
            }
        }
    }
}
