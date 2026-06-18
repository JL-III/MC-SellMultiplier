package com.sellmultiplier.config;

import com.sellmultiplier.SellMultiplier;
import com.sellmultiplier.utils.Util;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public final class ConfigManager {
    private final SellMultiplier plugin;

    // Per-permission bonus values, keyed by lower-cased sell.multiplier.<suffix>.
    private final Map<String, BigDecimal> multipliers = new HashMap<>();

    public ConfigManager(SellMultiplier plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        multipliers.clear();

        ConfigurationSection section =
                plugin.getConfig().getConfigurationSection("sell-multipliers");
        if (section == null) {
            Util.log("No 'sell-multipliers' section in config.yml; no sell multipliers are "
                    + "configured.");
            return;
        }

        for (String key : section.getKeys(false)) {
            if (!section.isDouble(key) && !section.isInt(key)) {
                Util.log("Ignoring 'sell-multipliers." + key + "': value '" + section.get(key)
                        + "' is not a number.");
                continue;
            }
            multipliers.put(
                    key.toLowerCase(Locale.ROOT), BigDecimal.valueOf(section.getDouble(key)));
        }
        Util.log("Loaded " + multipliers.size() + " sell multiplier(s) from config.");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    /** Per-permission bonus values, keyed by lower-cased sell.multiplier.&lt;suffix&gt;. */
    public Map<String, BigDecimal> getMultipliers() {
        return multipliers;
    }
}
