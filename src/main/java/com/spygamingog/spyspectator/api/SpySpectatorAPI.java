package com.spygamingog.spyspectator.api;

import com.spygamingog.spyspectator.SpySpectator;
import org.bukkit.entity.Player;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

public final class SpySpectatorAPI {

    private SpySpectatorAPI() {}

    /**
     * Checks if a player is currently in custom spectator mode.
     * @param player The player to check
     * @return true if the player is a spectator, false otherwise
     */
    public static boolean isSpectator(Player player) {
        if (player == null) return false;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return false;
        return instance.getSpectatorManager().isSpectator(player);
    }

    /**
     * Enables custom spectator mode for a player.
     * Fires PlayerSpectateEvent. If the event is cancelled, spectator mode is not enabled.
     * @param player The player to put into spectator mode
     */
    public static void enableSpectator(Player player) {
        if (player == null) return;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        instance.getSpectatorManager().enableSpectator(player);
    }

    /**
     * Disables custom spectator mode for a player.
     * Fires PlayerUnspectateEvent. If the event is cancelled, spectator mode is not disabled.
     * @param player The player to remove from spectator mode
     */
    public static void disableSpectator(Player player) {
        if (player == null) return;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        // toLobby=false, resetGameMode=true
        instance.getSpectatorManager().disableSpectator(player, false);
    }

    /**
     * Disables custom spectator mode for a player with additional options.
     * Fires PlayerUnspectateEvent. If the event is cancelled, spectator mode is not disabled.
     * @param player The player to remove from spectator mode
     * @param toLobby If true, teleports the player to the spectator lobby; if false, teleports them to their return location.
     */
    public static void disableSpectator(Player player, boolean toLobby) {
        if (player == null) return;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        instance.getSpectatorManager().disableSpectator(player, toLobby);
    }

    /**
     * Disables custom spectator mode for a player with full options.
     * Fires PlayerUnspectateEvent. If the event is cancelled, spectator mode is not disabled.
     * @param player The player to remove from spectator mode
     * @param toLobby If true, teleports the player to the spectator lobby; if false, teleports them to their return location.
     * @param resetGameMode If true, resets the player's game mode to Survival.
     */
    public static void disableSpectator(Player player, boolean toLobby, boolean resetGameMode) {
        if (player == null) return;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        instance.getSpectatorManager().disableSpectator(player, toLobby, resetGameMode);
    }

    /**
     * Returns a set of all currently spectating players.
     * @return A set of players in spectator mode
     */
    public static Set<Player> getSpectators() {
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) {
            return java.util.Collections.emptySet();
        }
        return instance.getSpectatorManager().getSpectatorUUIDs().stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .collect(Collectors.toSet());
    }
}
