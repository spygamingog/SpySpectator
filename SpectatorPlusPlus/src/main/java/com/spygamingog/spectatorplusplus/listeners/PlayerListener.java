package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public PlayerListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!spectatorManager.isSpectator(player)) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        
        if (item.getType() == Material.COMPASS) {
            handleCompassClick(player, item, event);
        } else if (item.getType() == Material.ENDER_EYE) {
            handleEnderEyeClick(player, item, event);
        } else if (item.getType() == Material.PAPER) {
            handlePaperClick(player, item, event);
        } else if (item.getType() == Material.RED_BED) {
            handleBedClick(player, item, event);
        }
    }
    
    private void handleCompassClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);

        if (!item.hasItemMeta()) {
            player.sendMessage("DEBUG: Item has no meta");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            player.sendMessage("DEBUG: Item has no display name");
            return;
        }

        String displayName = meta.getDisplayName();
        String strippedName = org.bukkit.ChatColor.stripColor(displayName);

        player.sendMessage("DEBUG: Display name: " + displayName);
        player.sendMessage("DEBUG: Stripped name: " + strippedName);

        if (strippedName.equals("Spectator Compass") || 
            strippedName.contains("Spectator Compass")) {
                
            player.sendMessage("DEBUG: Opening GUI...");
            plugin.getSpectatorManager().getSpectatorCompassGUI().open(player);
        } else {
            player.sendMessage("DEBUG: Not a spectator compass");
        }
    }
    
    private void handleEnderEyeClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        
        if (!item.hasItemMeta()) return;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;
        
        String displayName = meta.getDisplayName();
        if (!displayName.contains("Toggle Spectator Visibility")) return;
        
        spectatorManager.toggleSpectatorVisibility(player);
    }
    private void handlePaperClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);

        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;

        String displayName = org.bukkit.ChatColor.stripColor(meta.getDisplayName());

        if (displayName.equals("Toggle Spectator Chat") || 
            displayName.contains("Toggle Spectator Chat")) {
                
            spectatorManager.toggleSpectatorChat(player);
        }
    }
    private void handleBedClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        
        if (!item.hasItemMeta()) return;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;
        
        String displayName = meta.getDisplayName();
        if (!displayName.contains("Leave Spectator Mode")) return;
        
        spectatorManager.leaveSpectator(player);
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (spectatorManager.isSpectator(player) && spectatorManager.isSpectating(player)) {
            if (event.isSneaking()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {   
                    if (player.isOnline()) {
                        spectatorManager.stopSpectating(player);
                        player.sendMessage("§eStopped spectating. You're still in spectator mode.");
                    }
                });
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (!spectatorManager.isSpectator(player)) return;

        String title = event.getView().getTitle();

        if (title.contains("Spectate Players")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && 
                event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                    
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String playerName = meta.getDisplayName().replace("§a", "").replace("§2", "").replace("§e", "");
                    playerName = org.bukkit.ChatColor.stripColor(playerName);   

                    Player target = plugin.getServer().getPlayerExact(playerName);

                    if (target != null && !spectatorManager.isSpectator(target)) {  
                        player.closeInventory();
                        spectatorManager.spectatePlayer(player, target);
                    }
                }
            }
        } else if (event.getClickedInventory() == player.getInventory()) {
            event.setCancelled(true);
        }
    }
}