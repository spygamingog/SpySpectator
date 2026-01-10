package com.spygamingog.spectatorplusplus.commands;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenGUICommand implements CommandExecutor {
    private final SpectatorPlusPlus plugin;
    
    public OpenGUICommand(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("spectatorplusplus.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }
        
        plugin.getSpectatorManager().getSpectatorCompassGUI().open(player);
        player.sendMessage(ChatColor.GREEN + "Opened Player Selector GUI!");
        
        return true;
    }
}