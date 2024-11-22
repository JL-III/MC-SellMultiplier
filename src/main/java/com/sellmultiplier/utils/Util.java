package com.sellmultiplier.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Util {
    public static final String CHECK_SELF_PERMISSION = "sell-multiplier.check.self";
    public static final String CHECK_OTHER_PERMISSION = "sell-multiplier.check.other";

    // default message
    public static final String MESSAGE = "§dBlessing of the Oracle (%s) has given you an extra §c($%s) §dDenarii!";

    // base multiplier value
    public static final double BASE_VALUE = 0.10;

    private static final String PREFIX = "[SellMultiplier] ";

    public static void log(String message) { Bukkit.getConsoleSender().sendMessage(PREFIX + message); }

    public static void sendPermissionMessage(CommandSender sender, Set<String> messages) {
        if (messages.isEmpty()) {
            sender.sendMessage("§cYou dont have any sell multipliers!");
            return;
        }
        sender.sendMessage("§dCurrent sell multipliers:");
        messages.stream()
                .map(message -> "§e- " + message)
                .forEach(sender::sendMessage);
    }

    public static final Component NO_PERMISSION = Component.text("No permission.").color(NamedTextColor.DARK_RED);
}
