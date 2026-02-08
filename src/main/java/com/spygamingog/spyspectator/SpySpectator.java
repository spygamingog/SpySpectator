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
        
        // Initialize Manager
        this.spectatorManager = new SpectatorManager(this);
        
        // Register Commands
        getCommand("spectator").setExecutor(new SpectatorCommand(this));
        getCommand("spectator").setTabCompleter(new SpectatorTabCompleter());
        
        // Register Listeners
        getServer().getPluginManager().registerEvents(new SpectatorListener(this), this);
        
        getLogger().info("SpySpectator 2.0.0 enabled!");
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
