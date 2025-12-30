package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpectatorMainCommand implements CommandExecutor, TabCompleter {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    private final ConfigManager configManager;
    
    public SpectatorMainCommand(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "leave":
                handleLeave(sender);
                break;
                
            case "switch":
                handleSwitch(sender, args);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "lobby":
                handleLobby(sender, args);
                break;
                
            default:
                sendUsage(sender);
                break;
        }
        
        return true;
    }
    
    private void handleLeave(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return;
        }
        
        Player player = (Player) sender;
        if (!player.hasPermission("spectatorplusplus.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }
        
        if (spectatorManager.isSpectator(player)) {
            try {
                spectatorManager.leaveSpectator(player);
                player.sendMessage(ChatColor.GREEN + "Left spectator mode!");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Player must be in spectator mode")) {
                    spectatorManager.forceCleanup(player);
                    player.sendMessage(ChatColor.YELLOW + "Spectator state was corrupted. Force cleaned up.");
                    player.sendMessage(ChatColor.GREEN + "You are now back to normal mode.");
                } else {
                    throw e;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not in spectator mode!");
        }
    }
    
    private void handleSwitch(CommandSender sender, String[] args) {
        if (!sender.hasPermission("spectatorplusplus.admin.switch")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /spectator switch <player>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
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
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("spectatorplusplus.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }
        
        configManager.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
    }
    
    private void handleLobby(CommandSender sender, String[] args) {
        if (!sender.hasPermission("spectatorplusplus.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /spectator lobby <set|remove>");
            return;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return;
        }
        
        Player player = (Player) sender;
        
        if (args[1].equalsIgnoreCase("set")) {
            Location loc = player.getLocation();
            configManager.setLobbyLocation(loc);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("lobby-set")));
        } else if (args[1].equalsIgnoreCase("remove")) {
            configManager.removeLobbyLocation();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("lobby-removed")));
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /spectator lobby <set|remove>");
        }
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /spectator <leave|switch|reload|lobby>");
        if (sender.hasPermission("spectatorplusplus.use")) {
            sender.sendMessage(ChatColor.GRAY + "  leave - Leave spectator mode");
        }
        if (sender.hasPermission("spectatorplusplus.admin.switch")) {
            sender.sendMessage(ChatColor.GRAY + "  switch <player> - Switch player spectator status");
        }
        if (sender.hasPermission("spectatorplusplus.admin")) {
            sender.sendMessage(ChatColor.GRAY + "  reload - Reload configuration");
            sender.sendMessage(ChatColor.GRAY + "  lobby <set|remove> - Manage spectator lobby");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            
            if (sender.hasPermission("spectatorplusplus.use")) {
                subcommands.add("leave");
            }
            if (sender.hasPermission("spectatorplusplus.admin.switch")) {
                subcommands.add("switch");
            }
            if (sender.hasPermission("spectatorplusplus.admin")) {
                subcommands.add("reload");
                subcommands.add("lobby");
            }
            
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("switch")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("lobby")) {
                if ("set".startsWith(args[1].toLowerCase())) {
                    completions.add("set");
                }
                if ("remove".startsWith(args[1].toLowerCase())) {
                    completions.add("remove");
                }
            }
        }
        
        return completions;
    }
}