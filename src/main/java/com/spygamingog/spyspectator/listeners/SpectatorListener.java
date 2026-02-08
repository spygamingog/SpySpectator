package com.spygamingog.spyspectator.listeners;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spyspectator.gui.SpectatorGUI;
import com.spygamingog.spyspectator.gui.SpectatorSettingsGUI;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Chunk;

public class SpectatorListener implements Listener {

    private final SpySpectator plugin;
    private final SpectatorGUI gui;
    private final SpectatorSettingsGUI settingsGUI;

    public SpectatorListener(SpySpectator plugin) {
        this.plugin = plugin;
        this.gui = new SpectatorGUI(plugin);
        this.settingsGUI = new SpectatorSettingsGUI(plugin);
    }

    private SpectatorManager getManager() {
        return plugin.getSpectatorManager();
    }

    // --- Visibility & Join/Quit/WorldChange ---

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Restore spectator mode if they were in it
        if (getManager().isSpectator(player)) {
            getManager().enableSpectator(player, true);
            event.setJoinMessage(null); // Silence join message
        } else {
            // Update visibility for new player (hide existing spectators from them)
            getManager().updateVisibility(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            // Do not disable, just save state (handled by manager save)
            event.setQuitMessage(null); // Silence quit message
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectator(player)) {
            // Re-apply spectator mode (items, flight, gamemode)
            // Use delay to ensure world change is fully processed and override other plugins (e.g. SpyCore, SpyHunts)
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && getManager().isSpectator(player)) {
                    getManager().enableSpectator(player, true);
                    getManager().updateVisibility(player);
                }
            }, 5L);
        }
    }
    
    // --- Chunk Loading Prevention ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            Chunk toChunk = event.getTo().getChunk();
            Chunk fromChunk = event.getFrom().getChunk();
            
            if (!toChunk.equals(fromChunk)) {
                if (!toChunk.isLoaded()) {
                    event.setCancelled(true);
                    // Bouncing back slightly to prevent getting stuck on edge
                    event.setTo(event.getFrom()); 
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            if (!event.getTo().getChunk().isLoaded()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot teleport to unloaded chunks in Spectator Mode.");
            }
        }
    }
    
    // --- Chat Isolation ---
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            // Check if chat is enabled for sender
            if (!getManager().isChatEnabled(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou have spectator chat disabled. Enable it to chat.");
                return;
            }

            // Spectator chatting: Only visible to other spectators
            // Filter recipients:
            // 1. Must be Spectator
            // 2. Must be in same World Group (Same World for now)
            // 3. Must have Chat Enabled
            // 4. Must NOT have ignored the sender
            
            event.getRecipients().removeIf(recipient -> {
                if (!getManager().isSpectator(recipient)) return true; // Remove non-spectators
                if (!recipient.getWorld().equals(event.getPlayer().getWorld())) return true; // Remove different world
                if (!getManager().isChatEnabled(recipient)) return true; // Remove disabled chat
                if (getManager().isIgnored(recipient.getUniqueId(), event.getPlayer().getUniqueId())) return true; // Remove if ignored
                return false;
            });
            
            event.setFormat("§8[Spec] §7" + event.getPlayer().getName() + ": §f%2$s");
        } else {
            // Normal player chatting: Hidden from spectators? 
            // Usually spectators CAN see normal chat. User said: "spectator's chat sin't supposed to be shown to non spectators"
            // This implies: Spec -> Normal = BLOCKED.
            // It does NOT explicitly say Normal -> Spec = BLOCKED.
            // Standard behavior is Specs see everything.
            // So we leave this part alone.
        }
    }
    
    // --- GameMode Switch Handling ---
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectator(player)) {
            // If switching to something other than Adventure (our Spectator mode default),
            // assume they want to leave spectator mode but keep the new gamemode.
            // Note: Our spectator mode uses ADVENTURE. 
            // If they switch TO Adventure, we ignore (might be re-applying).
            // If they switch TO Spectator (Vanilla), we might want to allow it or treat as leave?
            // "switch you from our spectator to that mode" implies leaving our system.
            
            if (event.getNewGameMode() != GameMode.ADVENTURE) {
                // Disable spectator mode logic, but DO NOT reset gamemode (let the event happen)
                // and DO NOT teleport back (assume they want to be where they are)
                getManager().disableSpectator(player, false, false);
                player.sendMessage("§eGameMode changed! You have left custom Spectator Mode.");
            }
        }
    }

    // --- Interactions & Protections ---

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (getManager().isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (getManager().isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            // Handle Spectator Items
            ItemStack item = event.getItem();
            Action action = event.getAction();
            boolean isRight = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
            boolean isLeft = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

            if (item != null && (isRight || isLeft)) {
                if (item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Teleporter")) {
                    if (isRight) gui.openGUI(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                if (item.getType() == Material.ENDER_EYE && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Visibility")) {
                    if (isRight) {
                        settingsGUI.openGUI(event.getPlayer(), SpectatorSettingsGUI.SettingsType.VISIBILITY);
                    } else if (isLeft) {
                        getManager().toggleVisibility(event.getPlayer());
                    }
                    event.setCancelled(true);
                    return;
                }
                if (item.getType() == Material.PAPER && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Chat")) {
                    if (isRight) {
                        settingsGUI.openGUI(event.getPlayer(), SpectatorSettingsGUI.SettingsType.CHAT);
                    } else if (isLeft) {
                        getManager().toggleChat(event.getPlayer());
                    }
                    event.setCancelled(true);
                    return;
                }
                if (item.getType() == Material.RED_BED && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Leave")) {
                    if (isRight) getManager().disableSpectator(event.getPlayer(), true);
                    event.setCancelled(true);
                    return;
                }
            }
            
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (getManager().isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            // Handle GUI Clicks
            String title = event.getView().getTitle();
            
            if (title.equals("§8Spectator Teleporter")) {
                event.setCancelled(true); // Prevent taking items
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                    if (event.getCurrentItem().hasItemMeta()) {
                        String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2); // Remove color code
                        Player target = plugin.getServer().getPlayer(name);
                        if (target != null) {
                            player.teleport(target);
                            player.sendMessage("§aTeleported to " + target.getName());
                            player.closeInventory();
                        } else {
                            player.sendMessage("§cPlayer not found.");
                        }
                    }
                }
                return;
            }
            
            if (title.equals("§8Spectator Chat Settings") || title.equals("§8Spectator Visibility Settings")) {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();
                if (clicked == null) return;
                
                boolean isChat = title.contains("Chat");
                
                // Global Toggle
                if (event.getSlot() == 4) {
                    if (isChat) getManager().toggleChat(player);
                    else getManager().toggleVisibility(player);
                    
                    // Refresh GUI
                    settingsGUI.openGUI(player, isChat ? SpectatorSettingsGUI.SettingsType.CHAT : SpectatorSettingsGUI.SettingsType.VISIBILITY);
                    return;
                }
                
                // Individual Toggle
                if (clicked.getType() == Material.PLAYER_HEAD && clicked.hasItemMeta()) {
                    String name = clicked.getItemMeta().getDisplayName().substring(2);
                    Player target = plugin.getServer().getPlayer(name);
                    if (target != null) {
                        if (isChat) getManager().toggleIgnore(player, target.getUniqueId());
                        else getManager().toggleHide(player, target.getUniqueId());
                        
                        // Refresh GUI
                        settingsGUI.openGUI(player, isChat ? SpectatorSettingsGUI.SettingsType.CHAT : SpectatorSettingsGUI.SettingsType.VISIBILITY);
                    }
                }
                return;
            }
            
            if (getManager().isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (getManager().isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && getManager().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // --- Damage & Health ---

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && getManager().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDealDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && getManager().isSpectator((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && getManager().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
            ((Player) event.getEntity()).setFoodLevel(20); // Force full food
        }
    }
    
    // --- Air/Water Breathing ---
    
    @EventHandler
    public void onAirChange(org.bukkit.event.entity.EntityAirChangeEvent event) {
        if (event.getEntity() instanceof Player && getManager().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true); // Prevent air loss
        }
    }

    // --- Mobs Ignoring Spectator ---

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target instanceof Player && getManager().isSpectator((Player) target)) {
            event.setCancelled(true);
            event.setTarget(null);
        }
    }
}
