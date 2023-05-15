package com.sellmultiplier.utils;

import org.bukkit.Bukkit;

public class GeneralUtils {

    private static String prefix = "[SellMultiplier] ";

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void logError(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§c" + message);
    }

    public static void logWarning(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§e" + message);
    }

    public static String getPrefix() {
        return prefix;
    }

}
