package com.sellmultiplier.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public class GeneralUtils {

    private static String prefix = "[SellMultiplier] ";

    public static void log(String message) { Bukkit.getConsoleSender().sendMessage(prefix + message); }

    public static void logError(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§c" + message);
    }

    public static void logWarning(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§e" + message);
    }

    public static void sendPermissionMessage(Player player, Set<String> messages) {
        if (messages.isEmpty()) {
            player.sendMessage("§cYou dont have any sell multipliers!");
            return;
        }
        player.sendMessage("§dCurrent sell multipliers:");
        messages.stream()
                .map(message -> "§e- " + message)
                .forEach(player::sendMessage);
    }

    public static String getPrefix() {
        return prefix;
    }

}
