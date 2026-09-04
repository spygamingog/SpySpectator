package com.spygamingog.spyspectator.gui;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spycore.api.SpyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SpectatorGUI {

    private final SpySpectator plugin;
    public static final NamespacedKey TARGET_UUID_KEY = new NamespacedKey("spyspectator", "target_uuid");
    public static final NamespacedKey PAGE_KEY = new NamespacedKey("spyspectator", "gui_page");

    public SpectatorGUI(SpySpectator plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        openGUI(player, 0);
    }

    public void openGUI(Player player, int page) {
        List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
        online.remove(player);

        // Filter out spectators if config says not to show them
        if (!plugin.getConfig().getBoolean("compass-gui.show-spectators", false)) {
            online.removeIf(p -> plugin.getSpectatorManager().isSpectator(p));
        }

        int pageSize = 45; // Slots 0-44 for players, bottom row 45-53 for navigation
        int maxPages = Math.max(1, (int) Math.ceil((double) online.size() / pageSize));
        int currentPage = Math.max(0, Math.min(page, maxPages - 1));

        String rawTitle = plugin.getConfig().getString("compass-gui.title", "&8Spectator Teleporter");
        String title = ChatColor.translateAlternateColorCodes('&', rawTitle) + (maxPages > 1 ? " (" + (currentPage + 1) + "/" + maxPages + ")" : "");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, online.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Player target = online.get(i);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName(ChatColor.YELLOW + target.getName());

                // Store UUID directly in PDC for 100% reliable click resolution
                meta.getPersistentDataContainer().set(TARGET_UUID_KEY, PersistentDataType.STRING, target.getUniqueId().toString());

                List<String> lore = new ArrayList<>();
                try {
                    String worldName = SpyAPI.getAliasForWorld(target.getWorld());
                    lore.add(ChatColor.GRAY + "World: " + ChatColor.WHITE + worldName);
                } catch (Throwable t) {
                    lore.add(ChatColor.GRAY + "World: " + ChatColor.WHITE + target.getWorld().getName());
                }

                if (plugin.getConfig().getBoolean("compass-gui.show-health", true)) {
                    int health = (int) Math.ceil(target.getHealth());
                    lore.add(ChatColor.GRAY + "Health: " + ChatColor.RED + health + "❤");
                }

                if (plugin.getConfig().getBoolean("compass-gui.show-gamemode", true)) {
                    lore.add(ChatColor.GRAY + "Mode: " + ChatColor.AQUA + target.getGameMode().name());
                }

                lore.add(ChatColor.DARK_GRAY + "Click to teleport / spectate");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }

            gui.setItem(slot++, head);
        }

        // Navigation Bar on Row 6 (slots 45-53)
        if (currentPage > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta pMeta = prev.getItemMeta();
            if (pMeta != null) {
                pMeta.setDisplayName(ChatColor.YELLOW + "« Previous Page");
                pMeta.getPersistentDataContainer().set(PAGE_KEY, PersistentDataType.INTEGER, currentPage - 1);
                prev.setItemMeta(pMeta);
            }
            gui.setItem(45, prev);
        }

        if (currentPage < maxPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nMeta = next.getItemMeta();
            if (nMeta != null) {
                nMeta.setDisplayName(ChatColor.YELLOW + "Next Page »");
                nMeta.getPersistentDataContainer().set(PAGE_KEY, PersistentDataType.INTEGER, currentPage + 1);
                next.setItemMeta(nMeta);
            }
            gui.setItem(53, next);
        }

        player.openInventory(gui);
    }
}
