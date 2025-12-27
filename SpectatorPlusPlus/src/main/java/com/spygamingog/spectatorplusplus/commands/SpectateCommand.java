package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public SpectateCommand(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            if (!player.hasPermission("spectatorplusplus.use")) {
                player.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            if (spectatorManager.isSpectator(player)) {
                spectatorManager.leaveSpectator(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfigManager().getMessage("leave-spectator")));
            } else {
                spectatorManager.enterSpectator(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfigManager().getMessage("enter-spectator")));
            }
            return true;
        }
        
        if (args.length == 1) {
            if (!player.hasPermission("spectatorplusplus.spectate.others")) {
                player.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("player-not-found")));
                return true;
            }
            
            if (!spectatorManager.isSpectator(player)) {
                spectatorManager.enterSpectator(player);
            }
            
            spectatorManager.spectatePlayer(player, target);
            return true;
        }
        
        return false;
    }
}