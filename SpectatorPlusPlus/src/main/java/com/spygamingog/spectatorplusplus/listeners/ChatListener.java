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
            // Spectator chatting
            String format = ChatColor.GRAY + "[Spectator] " + ChatColor.RESET + 
                sender.getName() + ": " + event.getMessage();
            
            // Only send to other spectators and admins
            event.getRecipients().clear();
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                if (spectatorManager.isSpectator(recipient) || 
                    recipient.hasPermission("spectatorplusplus.admin")) {
                    event.getRecipients().add(recipient);
                }
            }
            
            event.setFormat(format);
        } else {
            // Non-spectator chatting
            // Spectators should still see normal chat
            // No changes needed, spectators remain as recipients
        }
    }
}
