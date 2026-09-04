package com.spygamingog.spyspectator.utils;

import com.spygamingog.spyspectator.SpySpectator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpectatorManager {

    private final SpySpectator plugin;
    private final Set<UUID> spectators = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Location> returnLocations = new ConcurrentHashMap<>();
    private final Map<UUID, ItemStack[]> savedInventories = new ConcurrentHashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new ConcurrentHashMap<>();
    
    // First-person spectating tracking: spectator UUID -> target UUID
    private final Map<UUID, UUID> firstPersonTargets = new ConcurrentHashMap<>();

    // Preferences
    private final Set<UUID> chatDisabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> visibilityDisabled = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Set<UUID>> ignoredChatPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> hiddenSpectators = new ConcurrentHashMap<>();

    private Location lobbyLocation;
    private final File dataFile;
    private YamlConfiguration dataConfig;

    public SpectatorManager(SpySpectator plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spectators.yml");
        loadLobby();
        loadSpectators();
    }

    public void reload() {
        plugin.reloadConfig();
        loadLobby();
    }

    public String getMessage(String path, String defaultMessage) {
        String msg = plugin.getConfig().getString("messages." + path, defaultMessage);
        return ChatColor.translateAlternateColorCodes('&', msg != null ? msg : defaultMessage);
    }

    public String formatMessage(String path, String defaultMessage, String player) {
        String msg = getMessage(path, defaultMessage);
        return msg.replace("{player}", player != null ? player : "");
    }

    public void enableSpectator(Player player) {
        enableSpectator(player, false);
    }

    public void enableSpectator(Player player, boolean isJoin) {
        if (!isJoin && spectators.contains(player.getUniqueId())) return;

        if (!isJoin) {
            com.spygamingog.spyspectator.api.events.PlayerSpectateEvent event = new com.spygamingog.spyspectator.api.events.PlayerSpectateEvent(player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
        }

        // Save return location and inventories only if newly entering
        if (!isJoin) {
            returnLocations.put(player.getUniqueId(), player.getLocation());
            savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
            savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            player.getInventory().clear();
        }
        
        spectators.add(player.getUniqueId());

        // Apply Spectator State
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setCollidable(false);
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setSilent(true);

        // Speed settings from config
        float flySpeed = (float) plugin.getConfig().getDouble("spectator.fly-speed", 0.1);
        float walkSpeed = (float) plugin.getConfig().getDouble("spectator.walk-speed", 0.2);
        player.setFlySpeed(Math.max(0.0f, Math.min(1.0f, flySpeed)));
        player.setWalkSpeed(Math.max(0.0f, Math.min(1.0f, walkSpeed)));
        
        // Full Health, Hunger, Saturation, Air
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setRemainingAir(player.getMaximumAir());
        
        // Metadata
        player.setMetadata("spyspectator", new FixedMetadataValue(plugin, true));

        // Self visibility fix
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        
        // Night Vision
        if (plugin.getConfig().getBoolean("spectator.give-night-vision", true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
        }

        // Hide from others
        updateVisibility(player);
        
        // Give Spectator Items
        giveSpectatorItems(player);
        
        // Preferences
        chatDisabled.remove(player.getUniqueId());
        visibilityDisabled.remove(player.getUniqueId());
        
        if (!isJoin) {
            player.sendMessage(getMessage("enter-spectator", "&aYou are now in spectator mode!"));
            saveSpectators();
        }
    }

    public void disableSpectator(Player player, boolean toLobby) {
        disableSpectator(player, toLobby, true);
    }

    public void disableSpectator(Player player, boolean toLobby, boolean resetGameMode) {
        if (!spectators.contains(player.getUniqueId())) return;

        // If currently in first-person spectate, stop first
        if (firstPersonTargets.containsKey(player.getUniqueId())) {
            stopSpectatingTarget(player);
        }

        com.spygamingog.spyspectator.api.events.PlayerUnspectateEvent event = new com.spygamingog.spyspectator.api.events.PlayerUnspectateEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        spectators.remove(player.getUniqueId());
        player.removeMetadata("spyspectator", plugin);

        // Restore State
        if (resetGameMode) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setCollidable(true);
        player.setInvulnerable(false);
        player.setCanPickupItems(true);
        player.setSilent(false);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        
        // Remove Effects
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        // Update visibility for everyone
        updateVisibility(player);
        
        // Clear Spectator Items & Restore Inventory
        player.getInventory().clear();
        if (savedInventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(savedInventories.remove(player.getUniqueId()));
            player.getInventory().setArmorContents(savedArmor.remove(player.getUniqueId()));
        }

        // Async Teleportation for Folia & Paper compatibility
        if (resetGameMode) {
            if (toLobby && lobbyLocation != null) {
                player.teleportAsync(lobbyLocation);
            } else {
                Location ret = returnLocations.remove(player.getUniqueId());
                if (ret != null) {
                    player.teleportAsync(ret);
                }
            }
        } else {
            returnLocations.remove(player.getUniqueId());
        }
        
        player.sendMessage(getMessage("leave-spectator", "&cYou left spectator mode!"));
        saveSpectators();
    }

    // --- First-Person Spectating ---

    public boolean startSpectatingTarget(Player spectator, Player target) {
        if (!plugin.getConfig().getBoolean("first-person-spectating.enabled", true)) {
            return false;
        }
        if (spectator.equals(target)) {
            spectator.sendMessage(getMessage("cannot-spectate-self", "&cYou cannot spectate yourself!"));
            return false;
        }
        if (isSpectator(target)) {
            spectator.sendMessage(getMessage("cannot-spectate-spectator", "&cYou cannot spectate another spectator!"));
            return false;
        }

        firstPersonTargets.put(spectator.getUniqueId(), target.getUniqueId());
        
        // Switch to vanilla spectator camera mode attached to target
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.setSpectatorTarget(target);
        spectator.sendMessage(formatMessage("now-spectating", "&aNow spectating &e{player}&a! Sneak to stop.", target.getName()));
        return true;
    }

    public void stopSpectatingTarget(Player spectator) {
        UUID targetId = firstPersonTargets.remove(spectator.getUniqueId());
        spectator.setSpectatorTarget(null);
        
        // Restore custom adventure spectator mode
        if (isSpectator(spectator)) {
            spectator.setGameMode(GameMode.ADVENTURE);
            spectator.setAllowFlight(true);
            spectator.setFlying(true);
            giveSpectatorItems(spectator);
            
            String targetName = targetId != null && Bukkit.getPlayer(targetId) != null ? Bukkit.getPlayer(targetId).getName() : "player";
            spectator.sendMessage(formatMessage("stopped-spectating", "&cStopped spectating &e{player}&c.", targetName));
        }
    }

    public boolean isSpectatingTarget(Player spectator) {
        return firstPersonTargets.containsKey(spectator.getUniqueId());
    }

    // --- Hotbar Items from Config ---

    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();

        // Chat Settings
        int chatSlot = plugin.getConfig().getInt("hotbar-items.chat-toggle-slot", 2);
        String chatName = plugin.getConfig().getString("spectator-items.chat-toggle.name", "&b&lChat Settings");
        List<String> chatLore = plugin.getConfig().getStringList("spectator-items.chat-toggle.lore");
        player.getInventory().setItem(chatSlot, createItem(Material.PAPER, chatName, chatLore));

        // Visibility Settings
        int visSlot = plugin.getConfig().getInt("hotbar-items.visibility-toggle-slot", 6);
        String visName = plugin.getConfig().getString("spectator-items.visibility-toggle.name", "&a&lVisibility Settings");
        List<String> visLore = plugin.getConfig().getStringList("spectator-items.visibility-toggle.lore");
        player.getInventory().setItem(visSlot, createItem(Material.ENDER_EYE, visName, visLore));

        // Compass / Teleporter
        int compSlot = plugin.getConfig().getInt("hotbar-items.compass-slot", 4);
        String compName = plugin.getConfig().getString("spectator-items.compass.name", "&6&lPlayer Teleporter");
        List<String> compLore = plugin.getConfig().getStringList("spectator-items.compass.lore");
        player.getInventory().setItem(compSlot, createItem(Material.COMPASS, compName, compLore));

        // Leave Item
        int leaveSlot = plugin.getConfig().getInt("hotbar-items.leave-slot", 8);
        String leaveName = plugin.getConfig().getString("spectator-items.leave-spectator.name", "&c&lLeave Spectator Mode");
        List<String> leaveLore = plugin.getConfig().getStringList("spectator-items.leave-spectator.lore");
        player.getInventory().setItem(leaveSlot, createItem(Material.RED_BED, leaveName, leaveLore));
    }

    private ItemStack createItem(Material material, String name, List<String> loreList) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> formattedLore = new ArrayList<>();
            for (String line : loreList) {
                formattedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(formattedLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public Set<UUID> getSpectatorUUIDs() {
        return Collections.unmodifiableSet(spectators);
    }

    public void setLobby(Location loc) {
        this.lobbyLocation = loc;
        if (loc != null) {
            plugin.getConfig().set("spectator_lobby.world", loc.getWorld().getName());
            plugin.getConfig().set("spectator_lobby.x", loc.getX());
            plugin.getConfig().set("spectator_lobby.y", loc.getY());
            plugin.getConfig().set("spectator_lobby.z", loc.getZ());
            plugin.getConfig().set("spectator_lobby.yaw", loc.getYaw());
            plugin.getConfig().set("spectator_lobby.pitch", loc.getPitch());
        } else {
            plugin.getConfig().set("spectator_lobby.world", null);
        }
        plugin.saveConfig();
    }
    
    public Location getLobby() {
        return lobbyLocation;
    }

    private void loadLobby() {
        String worldName = plugin.getConfig().getString("spectator_lobby.world");
        if (worldName != null && Bukkit.getWorld(worldName) != null) {
            double x = plugin.getConfig().getDouble("spectator_lobby.x", 0.5);
            double y = plugin.getConfig().getDouble("spectator_lobby.y", 64.0);
            double z = plugin.getConfig().getDouble("spectator_lobby.z", 0.5);
            float yaw = (float) plugin.getConfig().getDouble("spectator_lobby.yaw", 0.0);
            float pitch = (float) plugin.getConfig().getDouble("spectator_lobby.pitch", 0.0);
            this.lobbyLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            this.lobbyLocation = null;
        }
    }

    public void updateVisibility(Player target) {
        boolean isTargetSpectator = isSpectator(target);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(target.getUniqueId())) continue;

            boolean isOnlineSpectator = isSpectator(online);

            if (isTargetSpectator && !isOnlineSpectator) {
                online.hidePlayer(plugin, target);
            } else if (isTargetSpectator && isOnlineSpectator) {
                if (online.getWorld().equals(target.getWorld()) && 
                    !visibilityDisabled.contains(online.getUniqueId()) &&
                    !isHidden(online.getUniqueId(), target.getUniqueId())) {
                    online.showPlayer(plugin, target);
                } else {
                    online.hidePlayer(plugin, target);
                }
            } else {
                online.showPlayer(plugin, target);
            }

            if (isOnlineSpectator && !isTargetSpectator) {
                target.hidePlayer(plugin, online);
            } else if (isOnlineSpectator && isTargetSpectator) {
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
            player.sendMessage(getMessage("chat-enabled", "&aSpectator chat enabled."));
        } else {
            chatDisabled.add(player.getUniqueId());
            player.sendMessage(getMessage("chat-disabled", "&eSpectator chat disabled."));
        }
    }
    
    public boolean isVisibilityEnabled(Player player) {
        return !visibilityDisabled.contains(player.getUniqueId());
    }
    
    public void toggleVisibility(Player player) {
        if (visibilityDisabled.contains(player.getUniqueId())) {
            visibilityDisabled.remove(player.getUniqueId());
            player.sendMessage(getMessage("spectators-visible", "&aNow showing other spectators."));
        } else {
            visibilityDisabled.add(player.getUniqueId());
            player.sendMessage(getMessage("spectators-hidden", "&eNow hiding other spectators."));
        }
        updateVisibility(player);
    }
    
    public boolean isIgnored(UUID viewer, UUID target) {
        return ignoredChatPlayers.getOrDefault(viewer, Collections.emptySet()).contains(target);
    }
    
    public void toggleIgnore(Player viewer, UUID target) {
        Set<UUID> ignored = ignoredChatPlayers.computeIfAbsent(viewer.getUniqueId(), k -> ConcurrentHashMap.newKeySet());
        if (ignored.contains(target)) {
            ignored.remove(target);
            viewer.sendMessage(ChatColor.GREEN + "Unignored player chat.");
        } else {
            ignored.add(target);
            viewer.sendMessage(ChatColor.RED + "Ignored player chat.");
        }
    }
    
    public boolean isHidden(UUID viewer, UUID target) {
        return hiddenSpectators.getOrDefault(viewer, Collections.emptySet()).contains(target);
    }
    
    public void toggleHide(Player viewer, UUID target) {
        Set<UUID> hidden = hiddenSpectators.computeIfAbsent(viewer.getUniqueId(), k -> ConcurrentHashMap.newKeySet());
        if (hidden.contains(target)) {
            hidden.remove(target);
            viewer.sendMessage(ChatColor.GREEN + "Player is now visible.");
        } else {
            hidden.add(target);
            viewer.sendMessage(ChatColor.RED + "Player is now hidden.");
        }
        updateVisibility(viewer);
    }

    public void cleanup() {
        // Safe persistence on server shutdown: save all state including inventories to disk
        saveSpectators();
    }

    public synchronized void saveSpectators() {
        dataConfig = new YamlConfiguration();
        List<String> uuidList = new ArrayList<>();
        
        for (UUID uuid : spectators) {
            String uStr = uuid.toString();
            uuidList.add(uStr);
            
            Location loc = returnLocations.get(uuid);
            if (loc != null) {
                dataConfig.set("locations." + uStr, loc);
            }
            
            // Lossless Base64 serialization of inventories and armor
            ItemStack[] inv = savedInventories.get(uuid);
            if (inv != null && inv.length > 0) {
                dataConfig.set("inventories." + uStr, InventorySerializer.itemStackArrayToBase64(inv));
            }
            
            ItemStack[] armor = savedArmor.get(uuid);
            if (armor != null && armor.length > 0) {
                dataConfig.set("armor." + uStr, InventorySerializer.itemStackArrayToBase64(armor));
            }
        }
        
        dataConfig.set("spectators", uuidList);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save spectators.yml: " + e.getMessage());
        }
    }

    public synchronized void loadSpectators() {
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
                
                // Lossless Base64 deserialization of inventories and armor
                String invBase64 = dataConfig.getString("inventories." + s);
                if (invBase64 != null && !invBase64.isEmpty()) {
                    ItemStack[] inv = InventorySerializer.itemStackArrayFromBase64(invBase64);
                    if (inv.length > 0) {
                        savedInventories.put(uuid, inv);
                    }
                }
                
                String armorBase64 = dataConfig.getString("armor." + s);
                if (armorBase64 != null && !armorBase64.isEmpty()) {
                    ItemStack[] armor = InventorySerializer.itemStackArrayFromBase64(armorBase64);
                    if (armor.length > 0) {
                        savedArmor.put(uuid, armor);
                    }
                }
            } catch (IllegalArgumentException e) {
                // Ignore invalid UUIDs
            }
        }
        
        // Re-apply spectator mode for any currently online spectators
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (spectators.contains(p.getUniqueId())) {
                enableSpectator(p, true);
            }
        }
    }
}
