package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CommandManager {
    private final SpectatorPlusPlus plugin;
    
    public CommandManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        registerCommand("spectate", new SpectateCommand(plugin));
        
        PluginCommand spectatorCommand = plugin.getCommand("spectator");
        if (spectatorCommand != null) {
            SpectatorMainCommand mainHandler = new SpectatorMainCommand(plugin);
            spectatorCommand.setExecutor(mainHandler);
            spectatorCommand.setTabCompleter(mainHandler);
        }
    }
    
    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        }
    }
}