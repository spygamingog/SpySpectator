package com.spygamingog.spyspectator.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FoliaSchedulerHelper {
    public static void runLater(Plugin plugin, Player player, Runnable task, long delayTicks) {
        player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
    }
}
