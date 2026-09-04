package com.spygamingog.spyspectator.listeners;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spyspectator.gui.SpectatorGUI;
import com.spygamingog.spyspectator.gui.SpectatorSettingsGUI;
import com.spygamingog.spyspectator.utils.SchedulerUtils;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

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
        
        if (getManager().isSpectator(player)) {
            getManager().enableSpectator(player, true);
            event.setJoinMessage(null);
        } else {
            getManager().updateVisibility(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectatingTarget(player)) {
            getManager().stopSpectatingTarget(player);
        }
        if (getManager().isSpectator(player)) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectator(player)) {
            SchedulerUtils.runLater(plugin, player, () -> {
                if (player.isOnline() && getManager().isSpectator(player)) {
                    getManager().enableSpectator(player, true);
                    getManager().updateVisibility(player);
                }
            }, 5L);
        }
    }
    
    // --- Chunk Loading Prevention (Non-blocking, fixes synchronous chunk loading lag) ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            Location to = event.getTo();
            Location from = event.getFrom();
            if (to == null || to.getWorld() == null) return;

            int toChunkX = to.getBlockX() >> 4;
            int toChunkZ = to.getBlockZ() >> 4;
            int fromChunkX = from.getBlockX() >> 4;
            int fromChunkZ = from.getBlockZ() >> 4;
            
            if (toChunkX != fromChunkX || toChunkZ != fromChunkZ) {
                // Check if chunk is loaded WITHOUT forcing chunk generation/loading
                if (!to.getWorld().isChunkLoaded(toChunkX, toChunkZ)) {
                    event.setCancelled(true);
                    event.setTo(from);
                    event.getPlayer().sendMessage(getManager().getMessage("cannot-load-chunk", "§cYou cannot fly into unloaded chunks!"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            Location to = event.getTo();
            if (to == null || to.getWorld() == null) return;

            int toChunkX = to.getBlockX() >> 4;
            int toChunkZ = to.getBlockZ() >> 4;
            if (!to.getWorld().isChunkLoaded(toChunkX, toChunkZ)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(getManager().getMessage("cannot-load-chunk", "§cYou cannot fly into unloaded chunks!"));
            }
        }
    }
    
    // --- Chat Isolation ---
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            if (!getManager().isChatEnabled(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(getManager().getMessage("chat-disabled", "§cYou have spectator chat disabled. Enable it to chat."));
                return;
            }

            event.getRecipients().removeIf(recipient -> {
                if (!getManager().isSpectator(recipient)) return true;
                if (!recipient.getWorld().equals(event.getPlayer().getWorld())) return true;
                if (!getManager().isChatEnabled(recipient)) return true;
                if (getManager().isIgnored(recipient.getUniqueId(), event.getPlayer().getUniqueId())) return true;
                return false;
            });
            
            event.setFormat("§8[Spec] §7" + event.getPlayer().getName() + ": §f%2$s");
        }
    }
    
    // --- GameMode Switch Handling ---
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectator(player)) {
            // If in first person camera mode, let it stay
            if (getManager().isSpectatingTarget(player) && event.getNewGameMode() == GameMode.SPECTATOR) {
                return;
            }
            if (event.getNewGameMode() != GameMode.ADVENTURE) {
                getManager().disableSpectator(player, false, false);
                player.sendMessage(ChatColor.YELLOW + "GameMode changed! You have left custom Spectator Mode.");
            }
        }
    }

    // --- First-Person Spectating Interactivity ---

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (getManager().isSpectator(player)) {
            event.setCancelled(true);

            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                if (plugin.getConfig().getBoolean("first-person-spectating.right-click-to-spectate", true)) {
                    getManager().startSpectatingTarget(player, target);
                }
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && getManager().isSpectatingTarget(player)) {
            if (plugin.getConfig().getBoolean("first-person-spectating.stop-on-sneak", true)) {
                getManager().stopSpectatingTarget(player);
            }
        }
    }

    // --- Offhand Swap & Interaction Protections ---

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (getManager().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getManager().getMessage("item-locked", "§cThis item is locked and cannot be moved!"));
        }
    }

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
            ItemStack item = event.getItem();
            Action action = event.getAction();
            boolean isRight = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
            boolean isLeft = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

            if (item != null && (isRight || isLeft)) {
                // Compass Teleporter
                if (item.getType() == Material.COMPASS) {
                    if (isRight) gui.openGUI(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                // Visibility Settings
                if (item.getType() == Material.ENDER_EYE) {
                    if (isRight) {
                        settingsGUI.openGUI(event.getPlayer(), SpectatorSettingsGUI.SettingsType.VISIBILITY);
                    } else if (isLeft) {
                        getManager().toggleVisibility(event.getPlayer());
                    }
                    event.setCancelled(true);
                    return;
                }
                // Chat Settings
                if (item.getType() == Material.PAPER) {
                    if (isRight) {
                        settingsGUI.openGUI(event.getPlayer(), SpectatorSettingsGUI.SettingsType.CHAT);
                    } else if (isLeft) {
                        getManager().toggleChat(event.getPlayer());
                    }
                    event.setCancelled(true);
                    return;
                }
                // Leave Item
                if (item.getType() == Material.RED_BED) {
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
            String title = event.getView().getTitle();

            // Handle Teleporter GUI
            if (title.contains("Spectator Teleporter")) {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || !clicked.hasItemMeta()) return;

                ItemMeta meta = clicked.getItemMeta();

                // Check pagination navigation
                if (meta.getPersistentDataContainer().has(SpectatorGUI.PAGE_KEY, PersistentDataType.INTEGER)) {
                    Integer targetPage = meta.getPersistentDataContainer().get(SpectatorGUI.PAGE_KEY, PersistentDataType.INTEGER);
                    if (targetPage != null) {
                        gui.openGUI(player, targetPage);
                        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                    }
                    return;
                }

                // Check target head click
                if (meta.getPersistentDataContainer().has(SpectatorGUI.TARGET_UUID_KEY, PersistentDataType.STRING)) {
                    String uuidStr = meta.getPersistentDataContainer().get(SpectatorGUI.TARGET_UUID_KEY, PersistentDataType.STRING);
                    if (uuidStr != null) {
                        try {
                            UUID targetUUID = UUID.fromString(uuidStr);
                            Player target = Bukkit.getPlayer(targetUUID);
                            if (target != null && target.isOnline()) {
                                player.teleportAsync(target.getLocation()).thenRun(() -> {
                                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                                    player.sendMessage(getManager().formatMessage("now-spectating", "&aTeleported to &e{player}&a.", target.getName()));
                                });
                                player.closeInventory();
                            } else {
                                player.sendMessage(getManager().getMessage("player-not-found", "§cPlayer not found."));
                            }
                        } catch (IllegalArgumentException ignored) {}
                    }
                    return;
                }
                return;
            }
            
            // Handle Settings GUIs
            if (title.contains("Spectator Chat Settings") || title.contains("Spectator Visibility Settings")) {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();
                if (clicked == null) return;
                
                boolean isChat = title.contains("Chat");
                
                // Global Toggle (Slot 4)
                if (event.getSlot() == 4) {
                    if (isChat) getManager().toggleChat(player);
                    else getManager().toggleVisibility(player);
                    
                    settingsGUI.openGUI(player, isChat ? SpectatorSettingsGUI.SettingsType.CHAT : SpectatorSettingsGUI.SettingsType.VISIBILITY);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    return;
                }
                
                // Individual Player Toggle
                if (clicked.hasItemMeta() && clicked.getItemMeta().getPersistentDataContainer().has(SpectatorSettingsGUI.SETTING_UUID_KEY, PersistentDataType.STRING)) {
                    String uuidStr = clicked.getItemMeta().getPersistentDataContainer().get(SpectatorSettingsGUI.SETTING_UUID_KEY, PersistentDataType.STRING);
                    if (uuidStr != null) {
                        try {
                            UUID targetId = UUID.fromString(uuidStr);
                            if (isChat) getManager().toggleIgnore(player, targetId);
                            else getManager().toggleHide(player, targetId);
                            
                            settingsGUI.openGUI(player, isChat ? SpectatorSettingsGUI.SettingsType.CHAT : SpectatorSettingsGUI.SettingsType.VISIBILITY);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                return;
            }
            
            // General spectator inventory cancellation
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

    // --- Damage, Death & Health ---

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
            ((Player) event.getEntity()).setFoodLevel(20);
        }
    }
    
    @EventHandler
    public void onAirChange(org.bukkit.event.entity.EntityAirChangeEvent event) {
        if (event.getEntity() instanceof Player && getManager().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (getManager().isSpectator(event.getEntity())) {
            event.getDrops().clear();
            event.setDeathMessage(null);
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
