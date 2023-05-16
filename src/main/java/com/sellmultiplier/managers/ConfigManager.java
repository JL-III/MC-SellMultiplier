package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.math.BigDecimal;

public class ConfigManager {

    private ConfigurationSection sellMultipliers;
    private String message;
    private final Plugin plugin;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.message = plugin.getConfig().getString("message");
    }

    public BigDecimal getMultiplierFromConfig(String name) {
        BigDecimal multiplier = BigDecimal.ONE;

        if (sellMultipliers == null) {
            GeneralUtils.logWarning("No sell-multipliers specified in config! Defaulting to 1");
            return multiplier;
        }

        String multiplierString = sellMultipliers.getString(name);
        if (multiplierString == null) {
            GeneralUtils.logWarning("No multiplier specified for '" + name + "'! Defaulting to 1");
            return multiplier;
        }

        try {
            multiplier = BigDecimal.valueOf(Double.parseDouble(multiplierString));
        } catch (Exception e) {
            GeneralUtils.logWarning("Invalid multiplier '" + multiplierString + "' specified for '" + name + "'. Defaulting multiplier to 1.");
            return multiplier;
        }

        return multiplier;
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.message = plugin.getConfig().getString("message");
    }

    public ConfigurationSection getSellMultipliers() {
        return sellMultipliers;
    }

    public String getMessage() {
        return message;
    }

}
