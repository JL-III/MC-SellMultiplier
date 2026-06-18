package com.sellmultiplier.managers;

import com.sellmultiplier.config.ConfigManager;
import com.sellmultiplier.utils.Multiplier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiplierManager {

    private static final String PERMISSION_PREFIX = "sell.multiplier.";
    private static final Pattern PERMISSION_PATTERN =
            Pattern.compile("^" + Pattern.quote(PERMISSION_PREFIX) + "(.+)$");

    private final ConfigManager configManager;

    public MultiplierManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Builds the additive sell multiplier for a player. The total is
     * {@code 1 + the sum of every matching permission's configured bonus}.
     */
    public Multiplier getMultiplier(Player player) {
        Map<String, BigDecimal> multipliers = configManager.getMultipliers();
        BigDecimal bonus = BigDecimal.ZERO;
        for (String suffix : getMultiplierSuffixes(player)) {
            if (multipliers.containsKey(suffix)) {
                bonus = bonus.add(multipliers.get(suffix));
            }
        }
        return new Multiplier(formatPercent(bonus), BigDecimal.ONE.add(bonus));
    }

    /** Returns the lower-cased suffixes of every granted sell.multiplier.* permission. */
    public Set<String> getMultiplierSuffixes(CommandSender sender) {
        Set<String> suffixes = new HashSet<>();
        for (PermissionAttachmentInfo perm : sender.getEffectivePermissions()) {
            if (!perm.getValue()) continue;
            Matcher matcher = PERMISSION_PATTERN.matcher(perm.getPermission());
            if (matcher.matches()) {
                suffixes.add(matcher.group(1).toLowerCase(Locale.ROOT));
            }
        }
        return suffixes;
    }

    /** Human-readable list of a sender's multipliers, e.g. "Weekend (+25%)". */
    public Set<String> getStringsForPlayerPermCheck(CommandSender sender) {
        Map<String, BigDecimal> multipliers = configManager.getMultipliers();
        Set<String> result = new HashSet<>();
        for (String suffix : getMultiplierSuffixes(sender)) {
            if (!multipliers.containsKey(suffix)) continue;
            String label = Character.toUpperCase(suffix.charAt(0)) + suffix.substring(1);
            result.add(label + " (+" + formatPercent(multipliers.get(suffix)) + ")");
        }
        return result;
    }

    /** Formats a bonus fraction as a percentage string, e.g. 0.25 -> "25%". */
    private static String formatPercent(BigDecimal bonus) {
        return bonus.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
    }
}
