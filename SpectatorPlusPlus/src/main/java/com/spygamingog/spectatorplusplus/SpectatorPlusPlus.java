package com.spygamingog.spectatorplusplus;

import com.spygamingog.spectatorplusplus.commands.CommandManager;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import com.spygamingog.spectatorplusplus.listeners.*;
import com.spygamingog.spectatorplusplus.tasks.SpectatorInventorySync;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpectatorPlusPlus extends JavaPlugin {
    private static SpectatorPlusPlus instance;
    private ConfigManager configManager;
    private WorldSetManager worldSetManager;
    private SpectatorManager spectatorManager;
    private CommandManager commandManager;
    
    @Override
    public void onEnable() {
        instance = this;
        printBanner();
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        worldSetManager = new WorldSetManager(this);
        worldSetManager.loadWorldSets();
        spectatorManager = new SpectatorManager(this);
        commandManager = new CommandManager(this);
        commandManager.registerCommands();
        registerListeners();
        new SpectatorInventorySync(this).runTaskTimer(this, 0L, 2L);
        
        getLogger().info("Spectator++ v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Plugin by " + getDescription().getAuthors().toString());
        getLogger().info("First-person spectating, GUI selector, and toggle features active!");
    }

    private void printBanner() {
        getLogger().info("╔══════════════════════════════════════╗");
        getLogger().info("║      SpectatorPlusPlus v1.0.1        ║");
        getLogger().info("║    Advanced Spectator System         ║");
        getLogger().info("║      by SpyGamingOG                  ║");
        getLogger().info("╚══════════════════════════════════════╝");
    }
    
   @Override
    public void onDisable() {
        if (spectatorManager != null) {
            spectatorManager.disableAllSpectators();
        }
        
        saveConfig();
        
        getLogger().info("Spectator++ has been disabled!");
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AdvancementListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemLockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FlightListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlacePreventionListener(this), this); // NEW
    }
    
    public static SpectatorPlusPlus getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public WorldSetManager getWorldSetManager() {
        return worldSetManager;
    }
    
    public SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
}