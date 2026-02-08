package com.spygamingog.spyspectator.commands;

import com.spygamingog.spyspectator.SpySpectator;
import com.spygamingog.spyspectator.utils.SpectatorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorCommand implements CommandExecutor {

    private final SpySpectator plugin;

    public SpectatorCommand(SpySpectator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        SpectatorManager manager = plugin.getSpectatorManager();

        if (args.length == 0) {
            // Toggle / Enter
            if (manager.isSpectator(player)) {
                player.sendMessage("§cYou are already in spectator mode. Use /spectator leave to exit.");
            } else {
                manager.enableSpectator(player);
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("leave")) {
            if (!manager.isSpectator(player)) {
                player.sendMessage("§cYou are not in spectator mode.");
            } else {
                manager.disableSpectator(player, false);
            }
            return true;
        }

        if (sub.equals("lobby")) {
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("set")) {
                    if (!player.hasPermission("spyspectator.admin")) {
                        player.sendMessage("§cNo permission.");
                        return true;
                    }
                    manager.setLobby(player.getLocation());
                    player.sendMessage("§aSpectator lobby set!");
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (!player.hasPermission("spyspectator.admin")) {
                        player.sendMessage("§cNo permission.");
                        return true;
                    }
                    manager.setLobby(null);
                    player.sendMessage("§cSpectator lobby removed!");
                }
            } else {
                // Teleport to lobby
                if (manager.getLobby() != null) {
                    player.teleport(manager.getLobby());
                    player.sendMessage("§aTeleported to spectator lobby.");
                } else {
                    player.sendMessage("§cNo spectator lobby set.");
                }
            }
            return true;
        }

        return true;
    }
}
