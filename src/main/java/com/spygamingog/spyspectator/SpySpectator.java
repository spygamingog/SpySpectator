package com.spygamingog.spyspectator;

import com.spygamingog.spyspectator.commands.SpectatorCommand;
import com.spygamingog.spyspectator.commands.SpectatorTabCompleter;
import com.spygamingog.spyspectator.listeners.SpectatorListener;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpySpectator extends JavaPlugin {

    private static SpySpectator instance;
    private SpectatorManager spectatorManager;

    @Override
    public void onEnable() {
        instance = this;

        // Ensure default config exists
        saveDefaultConfig();

        // Initialize Manager
        this.spectatorManager = new SpectatorManager(this);

        // Register Commands
        SpectatorCommand executor = new SpectatorCommand(this);
        SpectatorTabCompleter tabCompleter = new SpectatorTabCompleter();

        if (getCommand("spectator") != null) {
            getCommand("spectator").setExecutor(executor);
            getCommand("spectator").setTabCompleter(tabCompleter);
        }

        if (getCommand("spectate") != null) {
            getCommand("spectate").setExecutor(executor);
            getCommand("spectate").setTabCompleter(tabCompleter);
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new SpectatorListener(this), this);

        getLogger().info("SpySpectator 3.0.1 enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (spectatorManager != null) {
            spectatorManager.cleanup();
        }
        getLogger().info("SpySpectator disabled!");
    }

    public static SpySpectator getInstance() {
        return instance;
    }

    public SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }
}
