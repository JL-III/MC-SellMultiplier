package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private final Plugin plugin;
    private Map<String, BigDecimal> sellMultipliers = new HashMap<>();
    private ConfigurationSection multiplierStacking;
    private boolean stackingEnabled;
    private double baseValue;
    private String message;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadAndValidateConfig();
    }

    public BigDecimal getMultiplierFromConfig(String name) {
        return sellMultipliers.getOrDefault(name, BigDecimal.ONE);
    }

    public BigDecimal getStackingMultiplier() {
        return BigDecimal.valueOf(baseValue);
    }

    private void loadAndValidateConfig() {
        ConfigurationSection multiplierSection = plugin.getConfig().getConfigurationSection("sell-multipliers");

        if (multiplierSection == null) {
            GeneralUtils.logWarning("No sell-multipliers specified in config! Defaulting to 1 for 'default'");
            sellMultipliers.put("default", BigDecimal.ONE);
        } else {
            for (String key : multiplierSection.getKeys(false)) {
                try {
                    sellMultipliers.put(key, BigDecimal.valueOf(Double.parseDouble(multiplierSection.getString(key))));
                } catch (NumberFormatException e) {
                    GeneralUtils.logWarning("Invalid multiplier '" + multiplierSection.getString(key) + "' specified for '" + key + "'. Defaulting multiplier to 1.");
                    sellMultipliers.put(key, BigDecimal.ONE);
                }
            }
        }
        this.multiplierStacking = plugin.getConfig().getConfigurationSection("sell-multiplier-stacking");

        if (this.multiplierStacking == null) {
            GeneralUtils.logWarning("No sell-multiplier-stacking specified in config! Defaulting to disabled and base value 1.");
            this.stackingEnabled = false;
            this.baseValue = 1.0;
        } else {
            this.stackingEnabled = multiplierStacking.getBoolean("enable");
            validateAndLoadBaseValue();
        }

        this.message = plugin.getConfig().getString("message");
        if (this.message == null) {
            GeneralUtils.logWarning("No message specified in config! Defaulting to empty.");
            this.message = "";
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        sellMultipliers.clear();
        loadAndValidateConfig();
    }

    private void validateAndLoadBaseValue() {
        Object baseValueObj = multiplierStacking.get("base-value");
        if (baseValueObj instanceof String) {
            try {
                this.baseValue = Double.parseDouble((String) baseValueObj);
            } catch (NumberFormatException e) {
                GeneralUtils.logWarning("Stacking base value in config is not a valid number. Defaulting to 1.0");
                this.baseValue = 1.0;
            }
        } else if (baseValueObj instanceof Double) {
            this.baseValue = (Double) baseValueObj;
        } else {
            GeneralUtils.logWarning("Stacking base value in config is not a valid type. It should be a number or a string representing a number. Defaulting to 1.0");
            this.baseValue = 1.0;
        }
    }

    public Map<String, BigDecimal> getSellMultipliers() {
        return sellMultipliers;
    }

    public Set<String> getMultiplierNames() {
        return Collections.unmodifiableSet(sellMultipliers.keySet());
    }


    public ConfigurationSection getMultiplierStacking() {
        return multiplierStacking;
    }

    public boolean getStackingEnabled() {
        return stackingEnabled;
    }

    public double getStackingBaseValue() {
        return baseValue;
    }

    public String getMessage() {
        return message;
    }
}