package com.spygamingog.spyspectator.api;

import com.spygamingog.spyspectator.SpySpectator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Starts first-person camera spectating on a target player.
     * @param spectator The spectating player
     * @param target The target player to spectate
     * @return true if first-person spectating started, false otherwise
     */
    public static boolean startSpectatingTarget(Player spectator, Player target) {
        if (spectator == null || target == null) return false;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return false;
        return instance.getSpectatorManager().startSpectatingTarget(spectator, target);
    }

    /**
     * Stops first-person camera spectating and returns the player to collision-free Adventure flight.
     * @param spectator The spectating player
     */
    public static void stopSpectatingTarget(Player spectator) {
        if (spectator == null) return;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        instance.getSpectatorManager().stopSpectatingTarget(spectator);
    }

    /**
     * Checks if a spectator is currently attached to a target player in first-person camera mode.
     * @param spectator The player to check
     * @return true if currently spectating a target in first-person, false otherwise
     */
    public static boolean isSpectatingTarget(Player spectator) {
        if (spectator == null) return false;
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return false;
        return instance.getSpectatorManager().isSpectatingTarget(spectator);
    }

    /**
     * Returns the configured spectator exit lobby location.
     * @return The lobby Location, or null if unset
     */
    public static Location getLobby() {
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return null;
        return instance.getSpectatorManager().getLobby();
    }

    /**
     * Sets the configured spectator exit lobby location.
     * @param loc The new lobby Location
     */
    public static void setLobby(Location loc) {
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) return;
        instance.getSpectatorManager().setLobby(loc);
    }

    /**
     * Returns a set of all currently spectating players.
     * @return A set of players in spectator mode
     */
    public static Set<Player> getSpectators() {
        SpySpectator instance = SpySpectator.getInstance();
        if (instance == null || instance.getSpectatorManager() == null) {
            return Collections.emptySet();
        }
        return instance.getSpectatorManager().getSpectatorUUIDs().stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .collect(Collectors.toSet());
    }
}
