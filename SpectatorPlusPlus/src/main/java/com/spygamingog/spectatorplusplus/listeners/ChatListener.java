package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public ChatListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        
        if (spectatorManager.isSpectator(sender)) {
            String format = org.bukkit.ChatColor.GRAY + "[Spectator] " + org.bukkit.ChatColor.RESET + 
                sender.getName() + ": " + event.getMessage();
            
            event.getRecipients().clear();
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                if (spectatorManager.isSpectator(recipient)) {
                    if (spectatorManager.canSeeSpectatorChat(recipient)) {
                        event.getRecipients().add(recipient);
                    }
                } else if (recipient.hasPermission("spectatorplusplus.admin")) {
                    event.getRecipients().add(recipient);
                }
            }
            
            event.setFormat(format);
        } else {
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                if (spectatorManager.isSpectator(recipient) && !spectatorManager.canSeeSpectatorChat(recipient)) {
                    event.getRecipients().remove(recipient);
                }
            }
        }
    }
}