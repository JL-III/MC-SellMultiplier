package com.sellmultiplier.managers;

import com.sellmultiplier.utils.Multiplier;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.math.BigDecimal;
import java.util.HashMap;
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

    // Per-permission bonus values loaded from config, keyed by lower-cased suffix.
    private final Map<String, BigDecimal> bonuses = new HashMap<>();
    private BigDecimal defaultBonus = BigDecimal.valueOf(0.10);

    public MultiplierManager(FileConfiguration config) {
        load(config);
    }

    /**
     * (Re)loads the configured per-permission bonus values. Any sell.multiplier.*
     * permission without an explicit entry uses 'default-bonus'.
     */
    public void load(FileConfiguration config) {
        bonuses.clear();
        defaultBonus = BigDecimal.valueOf(config.getDouble("default-bonus", 0.10));

        ConfigurationSection section = config.getConfigurationSection("sell-multipliers");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                bonuses.put(
                        key.toLowerCase(Locale.ROOT), BigDecimal.valueOf(section.getDouble(key)));
            }
        }
    }

    /**
     * Builds the additive sell multiplier for a player. The total is
     * {@code 1 + the sum of every matching permission's configured bonus}.
     */
    public Multiplier getMultiplier(Player player) {
        BigDecimal bonus = BigDecimal.ZERO;
        for (String suffix : getMultiplierSuffixes(player)) {
            bonus = bonus.add(bonuses.getOrDefault(suffix, defaultBonus));
        }
        BigDecimal value = BigDecimal.ONE.add(bonus);
        return new Multiplier(formatPercent(bonus), value);
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
        Set<String> result = new HashSet<>();
        for (String suffix : getMultiplierSuffixes(sender)) {
            BigDecimal bonus = bonuses.getOrDefault(suffix, defaultBonus);
            String label = Character.toUpperCase(suffix.charAt(0)) + suffix.substring(1);
            result.add(label + " (+" + formatPercent(bonus) + ")");
        }
        return result;
    }

    /** Formats a bonus fraction as a percentage string, e.g. 0.25 -> "25%". */
    private static String formatPercent(BigDecimal bonus) {
        return bonus.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
    }
}
