package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorAdminCommand implements CommandExecutor {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public SpectatorAdminCommand(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /spectator <leave|switch|reload|lobby>");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("leave")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("spectatorplusplus.use")) {
                player.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            if (spectatorManager.isSpectator(player)) {
                spectatorManager.leaveSpectator(player);
                player.sendMessage(ChatColor.GREEN + "Left spectator mode!");
            } else {
                player.sendMessage(ChatColor.RED + "You are not in spectator mode!");
            }
            return true;
        }
        
        if (args[0].equalsIgnoreCase("switch")) {
            if (!sender.hasPermission("spectatorplusplus.admin.switch")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /spectator switch <player>");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            
            if (spectatorManager.isSpectator(target)) {
                spectatorManager.leaveSpectator(target);
                sender.sendMessage(ChatColor.GREEN + target.getName() + " has been removed from spectator mode!");
                target.sendMessage(ChatColor.GREEN + "You have been removed from spectator mode by an admin!");
            } else {
                spectatorManager.enterSpectator(target);
                sender.sendMessage(ChatColor.GREEN + target.getName() + " has been put into spectator mode!");
                target.sendMessage(ChatColor.GREEN + "You have been put into spectator mode by an admin!");
            }
            return true;
        }
        
        return false;
    }
}