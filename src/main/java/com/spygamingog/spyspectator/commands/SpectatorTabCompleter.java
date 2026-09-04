package com.spygamingog.spyspectator.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpectatorTabCompleter implements TabCompleter {

    private static final List<String> BASE_OPTIONS = Arrays.asList("leave", "lobby", "reload", "switch");
    private static final List<String> SELECTORS = Arrays.asList("@s", "@p", "@a", "@r");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> options = new ArrayList<>(BASE_OPTIONS);
            
            // Add selectors and online player names
            options.addAll(SELECTORS);
            for (Player p : Bukkit.getOnlinePlayers()) {
                options.add(p.getName());
            }

            StringUtil.copyPartialMatches(args[0], options, completions);
            Collections.sort(completions);
            return completions;
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("lobby")) {
                if (sender.hasPermission("spyspectator.admin")) {
                    List<String> options = Arrays.asList("set", "remove");
                    StringUtil.copyPartialMatches(args[1], options, completions);
                    Collections.sort(completions);
                    return completions;
                }
            } else if (sub.equals("switch") || sub.equals("leave")) {
                if (sender.hasPermission("spyspectator.admin.switch")) {
                    List<String> options = new ArrayList<>(SELECTORS);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        options.add(p.getName());
                    }
                    StringUtil.copyPartialMatches(args[1], options, completions);
                    Collections.sort(completions);
                    return completions;
                }
            }
        }

        return Collections.emptyList();
    }
}
