package com.spygamingog.spyspectator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpectatorTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> options = new ArrayList<>(Arrays.asList("leave", "lobby"));
            StringUtil.copyPartialMatches(args[0], options, completions);
            Collections.sort(completions);
            return completions;
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("lobby")) {
            if (sender.hasPermission("spyspectator.admin")) {
                List<String> completions = new ArrayList<>();
                List<String> options = Arrays.asList("set", "remove");
                StringUtil.copyPartialMatches(args[1], options, completions);
                Collections.sort(completions);
                return completions;
            }
        }
        
        return Collections.emptyList();
    }
}
