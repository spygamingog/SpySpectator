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
import java.util.UUID;

public class VisibilityManager {
    private final SpectatorManager spectatorManager;
    private final SpectatorPlusPlus plugin;
    private final WorldSetManager worldSetManager;
    private final Scoreboard scoreboard;
    private final Map<String, Team> teams;
    
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
        Team spectatorTeam = scoreboard.getTeam(TEAM_SPECTATOR);
        if (spectatorTeam == null) {
            spectatorTeam = scoreboard.registerNewTeam(TEAM_SPECTATOR);
        }
        spectatorTeam.setPrefix(ChatColor.GRAY + "[Spectator] ");
        spectatorTeam.setColor(ChatColor.GRAY);
        spectatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        spectatorTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
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
        if (spectatorManager == null || player == null) return;
        
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            player.showPlayer(plugin, other);
        }
        
        if (spectatorManager.isSpectator(player)) {
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            if (spectatorTeam != null) {
                spectatorTeam.addEntry(player.getName());
            }
            
            boolean showSpectators = spectatorManager.isSpectatorVisibilityEnabled(player);
            
            if (!showSpectators) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.equals(player)) continue;
                    if (spectatorManager.isSpectator(other)) {
                        player.hidePlayer(plugin, other);
                    }
                }
            }
            
        } else {
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            if (spectatorTeam != null) {
                spectatorTeam.removeEntry(player.getName());
            }
            
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                if (spectatorManager.isSpectator(other)) {
                    boolean canShow = player.hasPermission("spectatorplusplus.admin") &&
                        spectatorManager.isSpectatorVisibilityEnabled(player);
                    if (!canShow) {
                        player.hidePlayer(plugin, other);
                    }
                }
            }
        }
    }
    
    public void updateAllVisibility() {
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerVisibility(player);
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTablistForWorldSet(player);
            updateNameTagVisibility(player);
            updatePlayerListName(player);
        }
    }
    
    public void updateTablistForWorldSet(Player player) {
        if (!player.isOnline()) return;
        
        Set<String> playersInSet = worldSetManager.getPlayersInSameSet(player);
        
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            
            if (playersInSet.contains(other.getName())) {
                if (spectatorManager.isSpectator(player)) {
                    boolean showSpectators = spectatorManager.isSpectatorVisibilityEnabled(player);
                    if (spectatorManager.isSpectator(other)) {
                        if (showSpectators) {
                            player.showPlayer(plugin, other);
                        } else {
                            player.hidePlayer(plugin, other);
                        }
                    } else {
                        player.showPlayer(plugin, other);
                    }
                } else {
                    if (spectatorManager.isSpectator(other)) {
                        boolean canShow = player.hasPermission("spectatorplusplus.admin") &&
                            spectatorManager.isSpectatorVisibilityEnabled(player);
                        if (canShow) {
                            player.showPlayer(plugin, other);
                        } else {
                            player.hidePlayer(plugin, other);
                        }
                    } else {
                        player.showPlayer(plugin, other);
                    }
                }
            } else {
                player.hidePlayer(plugin, other);
            }
        }
    }
    
    public void updateNameTagVisibility(Player player) {
        if (spectatorManager.isSpectator(player)) {
            Team hiddenTeam = teams.get(TEAM_HIDDEN);
            Team spectatorTeam = teams.get(TEAM_SPECTATOR);
            
            for (Team team : teams.values()) {
                if (team != spectatorTeam) {
                    team.removeEntry(player.getName());
                }
            }
            
            if (spectatorTeam != null) {
                spectatorTeam.addEntry(player.getName());
            }
            
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!spectatorManager.isSpectator(other) && !other.hasPermission("spectatorplusplus.admin")) {
                    if (hiddenTeam != null) {
                        hiddenTeam.addEntry(player.getName());
                    }
                    break;
                }
            }
        } else {
            Team visibleTeam = teams.get(TEAM_VISIBLE);
            
            for (Team team : teams.values()) {
                if (team != visibleTeam) {
                    team.removeEntry(player.getName());
                }
            }
            
            if (visibleTeam != null) {
                visibleTeam.addEntry(player.getName());
            }
        }
    }
    
    public void handlePlayerJoin(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updatePlayerVisibility(player);
            updateTablistForWorldSet(player);
            updateNameTagVisibility(player);
            updatePlayerListName(player);
            
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    updatePlayerVisibility(other);
                }
            }
        }, 10L);
    }
    
    public void handlePlayerQuit(Player player) {
        for (Team team : teams.values()) {
            team.removeEntry(player.getName());
        }
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player other : Bukkit.getOnlinePlayers()) {
                updatePlayerVisibility(other);
            }
        }, 5L);
    }
    
    public void handleWorldChange(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateTablistForWorldSet(player);
            updatePlayerListName(player);
            
            for (Player other : Bukkit.getOnlinePlayers()) {
                updateTablistForWorldSet(other);
            }
        }, 5L);
    }
    
    public void cleanup() {
        for (Team team : teams.values()) {
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
        }
    }
    
    public void updatePlayerListName(Player player) {
        if (spectatorManager.isSpectator(player)) {
            player.setPlayerListName(ChatColor.GRAY + "[Spectator] " + player.getName());
        } else {
            player.setPlayerListName(null);
        }
    }
}
