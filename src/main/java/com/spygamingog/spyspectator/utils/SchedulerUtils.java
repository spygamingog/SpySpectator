package com.spygamingog.spyspectator.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SchedulerUtils {

    private static Boolean isFolia = null;

    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
        }
        return isFolia;
    }

    public static void runLater(Plugin plugin, Player player, Runnable task, long delayTicks) {
        if (isFolia()) {
            try {
                Class.forName("com.spygamingog.spyspectator.utils.FoliaSchedulerHelper")
                        .getMethod("runLater", Plugin.class, Player.class, Runnable.class, long.class)
                        .invoke(null, plugin, player, task, delayTicks);
            } catch (Exception e) {
                Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
}
