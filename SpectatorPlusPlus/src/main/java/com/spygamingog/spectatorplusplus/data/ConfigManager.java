package com.spygamingog.spectatorplusplus.data;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final SpectatorPlusPlus plugin;
    private FileConfiguration config;
    
    public ConfigManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public Location getLobbyLocation() {
        if (config.contains("lobby.world")) {
            World world = Bukkit.getWorld(config.getString("lobby.world"));
            if (world != null) {
                return new Location(
                    world,
                    config.getDouble("lobby.x"),
                    config.getDouble("lobby.y"),
                    config.getDouble("lobby.z"),
                    (float) config.getDouble("lobby.yaw"),
                    (float) config.getDouble("lobby.pitch")
                );
            }
        }
        World lobbyWorld = Bukkit.getWorld(getSpectatorLobbyWorld());
        if (lobbyWorld != null) {
            return lobbyWorld.getSpawnLocation();
        }
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }
    
    public void setLobbyLocation(Location location) {
        config.set("lobby.world", location.getWorld().getName());
        config.set("lobby.x", location.getX());
        config.set("lobby.y", location.getY());
        config.set("lobby.z", location.getZ());
        config.set("lobby.yaw", location.getYaw());
        config.set("lobby.pitch", location.getPitch());
        plugin.saveConfig();
    }
    
    public void removeLobbyLocation() {
        config.set("lobby", null);
        plugin.saveConfig();
    }
    
    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage not found: " + key);
    }
    
    public boolean isAutoDetectWorldSets() {
        return config.getBoolean("world-sets.auto-detect", true);
    }
    
    public String getNetherSuffix() {
        return config.getString("world-sets.suffix-nethers", "_nether");
    }
    
    public String getEndSuffix() {
        return config.getString("world-sets.suffix-ends", "_the_end");
    }
    
    public String getSpectatorLobbyWorld() {
        return config.getString("spectator-lobby.world", "spectator_lobby");
    }
    
    public boolean isSpectatorLobbyWorld(String worldName) {
        return worldName.equalsIgnoreCase(getSpectatorLobbyWorld());
    }
}