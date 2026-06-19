package com.sellmultiplier.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Set;

public class Util {
    public static final String CHECK_SELF_PERMISSION = "sell-multiplier.check.self";
    public static final String CHECK_OTHER_PERMISSION = "sell-multiplier.check.other";
    public static final String RELOAD_PERMISSION = "sell-multiplier.reload";

    private static final String BONUS_MESSAGE_TEMPLATE = "<gradient:{mainHexColor}:{secondaryHexColor}>The <color:{accentHexColor}>Oracle's</color:{accentHexColor}> Boon (<color:{accentHexColor}>+{perm}</color:{accentHexColor}>) has given you an extra <color:{accentHexColor}>(${amount})</color:{accentHexColor}>!";

    // default message
    public static Component getBonusMessage(String perm, BigDecimal amount) {
        return getBonusMessage(perm, amount, "#67e9a8", "#1fa86b", "#ffd119");
    };

    private static Component getBonusMessage(String perm, BigDecimal amount, String mainHexColor, String secondaryHexColor, String accentHexColor) {
        String formattedMessage = BONUS_MESSAGE_TEMPLATE
                .replace("{mainHexColor}", mainHexColor)
                .replace("{secondaryHexColor}", secondaryHexColor)
                .replace("{accentHexColor}", accentHexColor)
                .replace("{perm}", perm)
                .replace("{amount}", amount.toString());

        return MiniMessage.miniMessage().deserialize(formattedMessage);
    }


    private static final String PREFIX = "[SellMultiplier] ";

    public static void log(String message) { Bukkit.getConsoleSender().sendMessage(PREFIX + message); }

    public static void sendPermissionMessage(CommandSender sender, Set<String> messages) {
        if (messages.isEmpty()) {
            sender.sendMessage("§cYou dont have any sell multipliers!");
            return;
        }
        sender.sendMessage("§aCurrent sell multipliers:");
        messages.stream()
                .map(message -> "§e- " + message)
                .forEach(sender::sendMessage);
    }

    public static final Component NO_PERMISSION = Component.text("No permission.").color(NamedTextColor.DARK_RED);
}
