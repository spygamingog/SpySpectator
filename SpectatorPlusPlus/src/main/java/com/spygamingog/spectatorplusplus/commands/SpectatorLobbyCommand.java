package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorLobbyCommand implements CommandExecutor {
    private final SpectatorPlusPlus plugin;
    private final ConfigManager configManager;
    
    public SpectatorLobbyCommand(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spectatorplusplus.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /spectator lobby <set|remove>");
            sender.sendMessage(ChatColor.RED + "Usage: /spectator reload");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            configManager.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("lobby") && args.length > 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }
            
            Player player = (Player) sender;
            
            if (args[1].equalsIgnoreCase("set")) {
                Location loc = player.getLocation();
                configManager.setLobbyLocation(loc);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.getMessage("lobby-set")));
                return true;
            }
            
            if (args[1].equalsIgnoreCase("remove")) {
                configManager.removeLobbyLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.getMessage("lobby-removed")));
                return true;
            }
        }
        
        return false;
    }
}
