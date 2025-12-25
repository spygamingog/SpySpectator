package com.spygamingog.spectatorplusplus.data;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public class WorldSetManager {
    private final SpectatorPlusPlus plugin;
    private final ConfigManager configManager;
    private final Map<String, Set<String>> worldSets; // Base world name -> Set of world names
    
    public WorldSetManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.worldSets = new HashMap<>();
    }
    
    public void loadWorldSets() {
        worldSets.clear();
        
        if (configManager.isAutoDetectWorldSets()) {
            autoDetectWorldSets();
        }
    }
    
    private void autoDetectWorldSets() {
        String netherSuffix = configManager.getNetherSuffix();
        String endSuffix = configManager.getEndSuffix();
        
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            
            // Check if this world is already part of a set
            if (isWorldInAnySet(worldName)) {
                continue;
            }
            
            // Check if it's a nether or end world
            if (worldName.endsWith(netherSuffix) || worldName.endsWith(endSuffix)) {
                continue;
            }
            
            // Try to find nether and end worlds for this base world
            Set<String> set = new HashSet<>();
            set.add(worldName);
            
            String netherWorldName = worldName + netherSuffix;
            String endWorldName = worldName + endSuffix;
            
            if (Bukkit.getWorld(netherWorldName) != null) {
                set.add(netherWorldName);
            }
            
            if (Bukkit.getWorld(endWorldName) != null) {
                set.add(endWorldName);
            }
            
            worldSets.put(worldName, set);
        }
    }
    
    private boolean isWorldInAnySet(String worldName) {
        return worldSets.values().stream()
            .anyMatch(set -> set.contains(worldName));
    }
    
    public Set<String> getWorldSet(String worldName) {
        // Find which set this world belongs to
        for (Set<String> set : worldSets.values()) {
            if (set.contains(worldName)) {
                return set;
            }
        }
        
        // If not found, return a set with just this world
        Set<String> singleSet = new HashSet<>();
        singleSet.add(worldName);
        return singleSet;
    }
    
    public Set<String> getPlayersInSameSet(org.bukkit.entity.Player player) {
        Set<String> worldSet = getWorldSet(player.getWorld().getName());
        Set<String> playerNames = new HashSet<>();
        
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (worldSet.contains(p.getWorld().getName())) {
                playerNames.add(p.getName());
            }
        }
        
        return playerNames;
    }
    
    public boolean areWorldsInSameSet(String world1, String world2) {
        Set<String> set1 = getWorldSet(world1);
        return set1.contains(world2);
    }
}
