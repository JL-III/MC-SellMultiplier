package com.sellmultiplier.config;

import com.playtheatria.jliii.generalutils.result.Err;
import com.playtheatria.jliii.generalutils.result.Ok;
import com.playtheatria.jliii.generalutils.result.Result;
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

        switch (getMultipliersSection()) {
            case Ok<ConfigurationSection, Exception> ok -> {
                ConfigurationSection section = ok.value();
                for (String key : section.getKeys(false)) {
                    if (!section.isDouble(key) && !section.isInt(key)) {
                        Util.log("Ignoring 'sell-multipliers." + key + "': value '"
                                + section.get(key) + "' is not a number.");
                        continue;
                    }
                    multipliers.put(
                            key.toLowerCase(Locale.ROOT),
                            BigDecimal.valueOf(section.getDouble(key)));
                }
                Util.log("Loaded " + multipliers.size() + " sell multiplier(s) from config.");
            }
            case Err<ConfigurationSection, Exception> err -> Util.log(err.error().getMessage());
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    /**
     * Retrieves the 'sell-multipliers' section, wrapping Bukkit's nullable return in
     * a Result so callers never have to deal with null.
     */
    private Result<ConfigurationSection, Exception> getMultipliersSection() {
        ConfigurationSection section =
                plugin.getConfig().getConfigurationSection("sell-multipliers");
        if (section == null) {
            return new Err<>(
                    new Exception(
                            "No 'sell-multipliers' section in config.yml; no sell multipliers"
                                    + " are configured."));
        }
        return new Ok<>(section);
    }

    /** Per-permission bonus values, keyed by lower-cased sell.multiplier.&lt;suffix&gt;. */
    public Map<String, BigDecimal> getMultipliers() {
        return multipliers;
    }
}
