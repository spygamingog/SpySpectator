package com.spygamingog.spectatorplusplus.data;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
        if (config.contains("spectator_lobby.world")) {
            World world = Bukkit.getWorld(config.getString("spectator_lobby.world"));
            if (world != null) {
                return new Location(
                    world,
                    config.getDouble("spectator_lobby.x"),
                    config.getDouble("spectator_lobby.y"),
                    config.getDouble("spectator_lobby.z"),
                    (float) config.getDouble("spectator_lobby.yaw"),
                    (float) config.getDouble("spectator_lobby.pitch")
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
        config.set("spectator_lobby.world", location.getWorld().getName());
        config.set("spectator_lobby.x", location.getX());
        config.set("spectator_lobby.y", location.getY());
        config.set("spectator_lobby.z", location.getZ());
        config.set("spectator_lobby.yaw", location.getYaw());
        config.set("spectator_lobby.pitch", location.getPitch());
        plugin.saveConfig();
    }
    
    public void removeLobbyLocation() {
        config.set("spectator_lobby", null);
        plugin.saveConfig();
    }
    
    public String getSpectatorLobbyWorld() {
        return config.getString("spectator_lobby.world", "spectator_lobby");
    }
    
    public boolean canSeeAllWorldsFromLobby() {
        return config.getBoolean("spectator_lobby.can-see-all-worlds", true);
    }
    
    public boolean showAllPlayersInTabFromLobby() {
        return config.getBoolean("spectator_lobby.show-all-players-in-tab", true);
    }
    
    public boolean teleportToLobbyOnLeave() {
        return config.getBoolean("spectator_lobby.teleport-to-on-leave", true);
    }
    
    public float getFlySpeed() {
        return (float) config.getDouble("spectator.fly-speed", 0.1);
    }

    public float getWalkSpeed() {
        return (float) config.getDouble("spectator.walk-speed", 0.2);
    }

    public boolean canFly() {
        return config.getBoolean("spectator.can-fly", true);
    }

    public boolean doubleJumpToFly() {
        return config.getBoolean("spectator.double-jump-to-fly", true);
    }

    public boolean invisibleToOthers() {
        return config.getBoolean("spectator.invisible-to-others", true);
    }

    public boolean canSeeOtherSpectators() {
        return config.getBoolean("spectator.can-see-other-spectators", true);
    }

    public boolean canSeeNormalPlayers() {
        return config.getBoolean("spectator.can-see-normal-players", true);
    }

    public boolean passThroughEntities() {
        return config.getBoolean("spectator.pass-through-entities", true);
    }

    public boolean normalWalking() {
        return config.getBoolean("spectator.normal-walking", true);
    }

    public boolean lockSpectatorItems() {
        return config.getBoolean("spectator.lock-spectator-items", true);
    }

    public boolean restoreOriginalGamemode() {
        return config.getBoolean("spectator.restore-original-gamemode", true);
    }

    public boolean chatVisibleToSpectatorsOnly() {
        return config.getBoolean("spectator.chat-visible-to-spectators-only", true);
    }
    
    public boolean firstPersonEnabled() {
        return config.getBoolean("first-person-spectating.enabled", true);
    }
    
    public boolean cameraInsideBody() {
        return config.getBoolean("first-person-spectating.camera-inside-body", true);
    }
    
    public boolean syncInventory() {
        return config.getBoolean("first-person-spectating.sync-inventory", true);
    }
    
    public boolean stopOnSneak() {
        return config.getBoolean("first-person-spectating.stop-on-sneak", true);
    }
    
    public boolean rightClickToSpectate() {
        return config.getBoolean("first-person-spectating.right-click-to-spectate", true);
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
    
    public List<String> getIgnoreWorlds() {
        return config.getStringList("world-sets.ignore-worlds");
    }
    
    public boolean isIgnoredWorld(String worldName) {
        return getIgnoreWorlds().contains(worldName.toLowerCase());
    }
    
    public boolean showSpectatorsInGUI() {
        return config.getBoolean("compass-gui.show-spectators", false);
    }

    public boolean showHealthInGUI() {
        return config.getBoolean("compass-gui.show-health", true);
    }

    public boolean showGamemodeInGUI() {
        return config.getBoolean("compass-gui.show-gamemode", true);
    }

    public boolean showWorldInGUI() {
        return config.getBoolean("compass-gui.show-world", true);
    }
    public boolean giveNightVision() {
        return config.getBoolean("spectator.give-night-vision", true);
    }
    public boolean giveInvisibility() {
        return config.getBoolean("spectator.give-invisibility", true);
    }

    public int getCompassGUIRows() {
        int rows = config.getInt("compass-gui.rows", 6);
        return Math.max(1, Math.min(6, rows));
    }

    public String getCompassGUITitle() {
        return config.getString("compass-gui.title", "&6Spectate Players");
    }
    
    public int getCompassSlot() {
        return config.getInt("hotbar-items.compass-slot", 0);
    }
    
    public int getVisibilityToggleSlot() {
        return config.getInt("hotbar-items.visibility-toggle-slot", 2);
    }
    
    public int getChatToggleSlot() {
        return config.getInt("hotbar-items.chat-toggle-slot", 4);
    }
    
    public int getLeaveSlot() {
        return config.getInt("hotbar-items.leave-slot", 8);
    }
    
    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage not found: " + key);
    }
    
    public boolean multiverseEnabled() {
        return config.getBoolean("multiverse.enabled", false);
    }
    
    public boolean useMVTeleport() {
        return config.getBoolean("multiverse.use-mv-teleport", false);
    }
    
    public boolean respectMVInventories() {
        return config.getBoolean("multiverse.respect-mv-inventories", false);
    }
    
    public boolean debugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }
    
    public boolean logSpectatorEvents() {
        return config.getBoolean("debug.log-spectator-events", false);
    }
    
    public boolean logSpectatingEvents() {
        return config.getBoolean("debug.log-spectating-events", false);
    }
    
    public boolean isSpectatorLobbyWorld(String worldName) {
        return worldName.equalsIgnoreCase(getSpectatorLobbyWorld());
    }

    public float getFlightSpeed() {
        return getFlySpeed();
    }
    
    public boolean getCanFly() {
        return config.getBoolean("spectator.can-fly", true);
    }

    public boolean getDoubleJumpToFly() {
        return config.getBoolean("spectator.double-jump-to-fly", true);
    }
}
