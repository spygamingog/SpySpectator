package com.spygamingog.spectatorplusplus.utils;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VisibilityManager {
    private final SpectatorManager spectatorManager;
    private final SpectatorPlusPlus plugin;
    private final WorldSetManager worldSetManager;
    private final Scoreboard scoreboard;
    private final Map<String, Team> teams;
    
    // Team names for different visibility groups
    private static final String TEAM_SPECTATOR = "spp_spectator";
    private static final String TEAM_HIDDEN = "spp_hidden";
    private static final String TEAM_VISIBLE = "spp_visible";
    
    public VisibilityManager(SpectatorManager spectatorManager) {
        this.spectatorManager = spectatorManager;
        this.plugin = spectatorManager.getPlugin();
        this.worldSetManager = plugin.getWorldSetManager();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.teams = new HashMap<>();
        
        initializeTeams();
    }
    
    private void initializeTeams() {
        // Create or get teams
        Team spectatorTeam = scoreboard.getTeam(TEAM_SPECTATOR);
        if (spectatorTeam == null) {
            spectatorTeam = scoreboard.registerNewTeam(TEAM_SPECTATOR);
        }
        spectatorTeam.setPrefix(ChatColor.GRAY + "[Spectator] ");
        spectatorTeam.setColor(ChatColor.GRAY);
        spectatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER); // No collision
        teams.put(TEAM_SPECTATOR, spectatorTeam);
        
        Team hiddenTeam = scoreboard.getTeam(TEAM_HIDDEN);
        if (hiddenTeam == null) {
            hiddenTeam = scoreboard.registerNewTeam(TEAM_HIDDEN);
        }
        hiddenTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        hiddenTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        teams.put(TEAM_HIDDEN, hiddenTeam);
        
        Team visibleTeam = scoreboard.getTeam(TEAM_VISIBLE);
        if (visibleTeam == null) {
            visibleTeam = scoreboard.registerNewTeam(TEAM_VISIBLE);
        }
        visibleTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        teams.put(TEAM_VISIBLE, visibleTeam);
    }
    
    public void updatePlayerVisibility(Player player) {
        if (spectatorManager == null) {
            return;
        }
    
        if (spectatorManager.isSpectator(player)) {
        // Add player to spectator team
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            if (spectatorTeam != null) {
                spectatorTeam.addEntry(player.getName());
            }
        
            // FIXED: Spectator should see everyone
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
            
                // Spectator can see everyone
                player.showPlayer(plugin, other);
            
                // Others can't see spectator unless they're also spectator/admin
                if (!spectatorManager.isSpectator(other) && !other.hasPermission("spectatorplusplus.admin")) {
                    other.hidePlayer(plugin, player);
                } else {
                    other.showPlayer(plugin, player);
                }
            }
        } else {
            // Remove from spectator team if present
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            if (spectatorTeam != null) {
                spectatorTeam.removeEntry(player.getName());
            }
            
            // Non-spectator: can't see spectators unless admin
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                
                if (spectatorManager.isSpectator(other) && !player.hasPermission("spectatorplusplus.admin")) {
                    player.hidePlayer(plugin, other);
                } else {
                    player.showPlayer(plugin, other);
                }
            }
        }
    }
    
    public void updateAllVisibility() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerVisibility(player);
        }
    }
    
    public void updateTablistForWorldSet(Player player) {
        if (!player.isOnline()) return;
        
        // Get players in the same world set
        Set<String> playersInSet = worldSetManager.getPlayersInSameSet(player);
        
        // Show/hide players in tablist based on spectator status
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            
            if (playersInSet.contains(other.getName())) {
                // Player is in the same world set
                if (spectatorManager.isSpectator(player)) {
                    // Spectator: only see other spectators and admins
                    if (spectatorManager.isSpectator(other) || other.hasPermission("spectatorplusplus.admin")) {
                        player.showPlayer(plugin, other);
                    } else {
                        player.hidePlayer(plugin, other);
                    }
                } else {
                    // Non-spectator: don't see spectators unless admin
                    if (spectatorManager.isSpectator(other) && !player.hasPermission("spectatorplusplus.admin")) {
                        player.hidePlayer(plugin, other);
                    } else {
                        player.showPlayer(plugin, other);
                    }
                }
            } else {
                // Player is in different world set - always hide
                player.hidePlayer(plugin, other);
            }
        }
    }
    
    public void updateNameTagVisibility(Player player) {
        if (spectatorManager.isSpectator(player)) {
            // Hide nametag from non-spectators
            Team hiddenTeam = teams.get(TEAM_HIDDEN);
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            
            // Remove from other teams first
            for (Team team : teams.values()) {
                if (team != spectatorTeam) {
                    team.removeEntry(player.getName());
                }
            }
            
            // Add to spectator team for other spectators to see
            if (spectatorTeam != null) {
                spectatorTeam.addEntry(player.getName());
            }
            
            // For non-spectators, add to hidden team
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!spectatorManager.isSpectator(other) && !other.hasPermission("spectatorplusplus.admin")) {
                    if (hiddenTeam != null) {
                        hiddenTeam.addEntry(player.getName());
                    }
                    break;
                }
            }
        } else {
            // Show nametag to everyone
            Team visibleTeam = teams.get(TEAM_VISIBLE);
            
            // Remove from other teams
            for (Team team : teams.values()) {
                if (team != visibleTeam) {
                    team.removeEntry(player.getName());
                }
            }
            
            // Add to visible team
            if (visibleTeam != null) {
                visibleTeam.addEntry(player.getName());
            }
        }
    }
    
    public void handlePlayerJoin(Player player) {
        // Delay to ensure player is fully joined
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updatePlayerVisibility(player);
            updateTablistForWorldSet(player);
            updateNameTagVisibility(player);
            
            // Update visibility for existing players
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    updatePlayerVisibility(other);
                }
            }
        }, 10L);
    }
    
    public void handlePlayerQuit(Player player) {
        // Remove from all teams
        for (Team team : teams.values()) {
            team.removeEntry(player.getName());
        }
        
        // Update visibility for remaining players
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player other : Bukkit.getOnlinePlayers()) {
                updatePlayerVisibility(other);
            }
        }, 5L);
    }
    
    public void handleWorldChange(Player player) {
        // Update visibility after world change
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateTablistForWorldSet(player);
            
            // Update for all players to reflect new world set
            for (Player other : Bukkit.getOnlinePlayers()) {
                updateTablistForWorldSet(other);
            }
        }, 5L);
    }
    
    public void cleanup() {
        // Clean up teams on disable
        for (Team team : teams.values()) {
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
        }
    }
}
