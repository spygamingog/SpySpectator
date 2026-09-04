package com.spygamingog.spyspectator.commands;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectatorCommand implements CommandExecutor {

    private final SpySpectator plugin;

    public SpectatorCommand(SpySpectator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SpectatorManager manager = plugin.getSpectatorManager();

        // 1. Zero arguments: toggle self
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console usage: /spectator <reload|switch|leave> [player|@selector]");
                return true;
            }
            Player player = (Player) sender;
            if (manager.isSpectator(player)) {
                player.sendMessage(manager.getMessage("already-spectator", "&cYou are already in spectator mode. Use /spectator leave to exit."));
            } else {
                manager.enableSpectator(player);
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        // 2. /spectator reload
        if (sub.equals("reload")) {
            if (!sender.hasPermission("spyspectator.admin")) {
                sender.sendMessage(manager.getMessage("no-permission", "&cYou don't have permission!"));
                return true;
            }
            manager.reload();
            sender.sendMessage(manager.getMessage("config-reloaded", "&aConfiguration reloaded successfully!"));
            return true;
        }

        // 3. /spectator leave [player|@selector]
        if (sub.equals("leave")) {
            if (args.length > 1) {
                if (!sender.hasPermission("spyspectator.admin.switch")) {
                    sender.sendMessage(manager.getMessage("no-permission", "&cYou don't have permission!"));
                    return true;
                }
                List<Player> targets = resolveTargets(sender, args[1]);
                if (targets.isEmpty()) {
                    sender.sendMessage(manager.getMessage("player-not-found", "&cPlayer or target not found!"));
                    return true;
                }
                for (Player target : targets) {
                    if (manager.isSpectator(target)) {
                        manager.disableSpectator(target, false);
                        sender.sendMessage(manager.formatMessage("switch-success", "&aSuccessfully removed &e{player}&a from spectator mode.", target.getName()));
                    }
                }
                return true;
            }

            // Self leave
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console must specify a player: /spectator leave <player|@selector>");
                return true;
            }
            Player player = (Player) sender;
            if (!manager.isSpectator(player)) {
                player.sendMessage(manager.getMessage("not-spectator", "&cYou are not in spectator mode."));
            } else {
                manager.disableSpectator(player, false);
            }
            return true;
        }

        // 4. /spectator switch <player|@selector>
        if (sub.equals("switch")) {
            if (!sender.hasPermission("spyspectator.admin.switch")) {
                sender.sendMessage(manager.getMessage("no-permission", "&cYou don't have permission!"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /spectator switch <player|@selector>");
                return true;
            }
            List<Player> targets = resolveTargets(sender, args[1]);
            if (targets.isEmpty()) {
                sender.sendMessage(manager.getMessage("player-not-found", "&cPlayer or target not found!"));
                return true;
            }
            for (Player target : targets) {
                if (manager.isSpectator(target)) {
                    manager.disableSpectator(target, false);
                } else {
                    manager.enableSpectator(target);
                }
                sender.sendMessage(manager.formatMessage("switch-success", "&aSuccessfully toggled spectator mode for &e{player}&a.", target.getName()));
            }
            return true;
        }

        // 5. /spectator lobby [set|remove]
        if (sub.equals("lobby")) {
            if (args.length > 1) {
                if (!sender.hasPermission("spyspectator.admin")) {
                    sender.sendMessage(manager.getMessage("no-permission", "&cNo permission."));
                    return true;
                }
                if (args[1].equalsIgnoreCase("set")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Only players can set the lobby location.");
                        return true;
                    }
                    Player p = (Player) sender;
                    manager.setLobby(p.getLocation());
                    p.sendMessage(manager.getMessage("lobby-set", "&aSpectator lobby location set!"));
                    return true;
                } else if (args[1].equalsIgnoreCase("remove")) {
                    manager.setLobby(null);
                    sender.sendMessage(manager.getMessage("lobby-removed", "&cSpectator lobby location removed!"));
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can teleport to the lobby.");
                    return true;
                }
                Player p = (Player) sender;
                if (manager.getLobby() != null) {
                    p.teleportAsync(manager.getLobby());
                    p.sendMessage(ChatColor.GREEN + "Teleported to spectator lobby.");
                } else {
                    p.sendMessage(ChatColor.RED + "No spectator lobby set.");
                }
                return true;
            }
        }

        // 6. Direct target spectating: /spectate <player|@selector> or /spectator <player|@selector>
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("spyspectator.spectate.others")) {
                player.sendMessage(manager.getMessage("no-permission", "&cYou don't have permission to spectate others!"));
                return true;
            }

            List<Player> targets = resolveTargets(sender, args[0]);
            if (targets.isEmpty()) {
                player.sendMessage(manager.getMessage("player-not-found", "&cPlayer or target not found!"));
                return true;
            }

            Player target = targets.get(0);
            if (target.equals(player)) {
                player.sendMessage(manager.getMessage("cannot-spectate-self", "&cYou cannot spectate yourself!"));
                return true;
            }

            if (!manager.isSpectator(player)) {
                manager.enableSpectator(player);
            }

            // If first-person enabled in config, enter first-person spectating
            if (plugin.getConfig().getBoolean("first-person-spectating.enabled", true)) {
                manager.startSpectatingTarget(player, target);
            } else {
                player.teleportAsync(target.getLocation());
                player.sendMessage(manager.formatMessage("now-spectating", "&aNow spectating &e{player}&a!", target.getName()));
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown command syntax. Use /spectator <reload|switch|leave> [player|@selector]");
        }

        return true;
    }

    /**
     * Resolves target players using Paper entity selectors (@s, @p, @a, @r, etc.) or player names.
     */
    public static List<Player> resolveTargets(CommandSender sender, String selectorOrName) {
        if (selectorOrName == null || selectorOrName.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Player> players = new ArrayList<>();

        // 1. Check for Target Selectors (@s, @p, @a, @r, @e)
        if (selectorOrName.startsWith("@")) {
            try {
                List<Entity> selected = Bukkit.selectEntities(sender, selectorOrName);
                for (Entity entity : selected) {
                    if (entity instanceof Player) {
                        players.add((Player) entity);
                    }
                }
                if (!players.isEmpty()) {
                    return players;
                }
            } catch (IllegalArgumentException e) {
                // Invalid selector syntax
            }
        }

        // 2. Direct player lookup
        Player player = Bukkit.getPlayer(selectorOrName);
        if (player != null && player.isOnline()) {
            players.add(player);
        }

        return players;
    }
}
