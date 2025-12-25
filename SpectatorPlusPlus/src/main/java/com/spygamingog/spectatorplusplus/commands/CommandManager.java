package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandManager {
    private final SpectatorPlusPlus plugin;
    
    public CommandManager(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        registerCommand("spectate", new SpectateCommand(plugin));
        registerCommand("spectator", new SpectatorLobbyCommand(plugin));
    }
    
    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        }
    }
}
