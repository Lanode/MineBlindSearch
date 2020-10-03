package tk.augmeneco.MineBlindSearch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

public class Utils {
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(String.format("%s[Observer]%s %s%s",
                ChatColor.GREEN, ChatColor.GOLD, message, ChatColor.WHITE));
    }

    public static void broadcastToOps(String message) {
        Bukkit.getServer().broadcast(String.format("%s[Observer]%s %s%s",
                ChatColor.GREEN, ChatColor.DARK_RED, message, ChatColor.WHITE), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
    }

    public static void error(String message) {
        Bukkit.broadcastMessage(String.format("%s%s%s",
                ChatColor.RED, message, ChatColor.WHITE));
    }
}
