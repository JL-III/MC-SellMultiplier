package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.math.BigDecimal;

public class ConfigManager {
    // The instance of the plugin
    private final Plugin plugin;
    // The section in the configuration file for sell multipliers
    private ConfigurationSection sellMultipliers;
    // The section in the configuration file for multiplier stacking
    private ConfigurationSection multiplierStacking;
    private boolean stackingEnabled;
    private double baseValue;
    // The message to be displayed from the configuration file
    private String message;

    // Constructor to initialize plugin and get sections from the config file
    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.multiplierStacking = plugin.getConfig().getConfigurationSection("sell-multiplier-stacking");
        this.stackingEnabled = multiplierStacking.getBoolean("enable");
        this.baseValue = multiplierStacking.getDouble("base-value");
        this.message = plugin.getConfig().getString("message");
    }

    // Method to get a specific multiplier from the config
    public BigDecimal getMultiplierFromConfig(String name) {
        // By default, the multiplier is 1
        BigDecimal multiplier = BigDecimal.ONE;

        // Check if sell multipliers are configured
        if (sellMultipliers == null) {
            GeneralUtils.logWarning("No sell-multipliers specified in config! Defaulting to 1");
            return multiplier;
        }

        // Get the multiplier string for the given name
        String multiplierString = sellMultipliers.getString(name);
        if (multiplierString == null) {
            GeneralUtils.logWarning("No multiplier specified for '" + name + "'! Defaulting to 1");
            return multiplier;
        }

        try {
            // Try to parse the multiplier string as a BigDecimal
            multiplier = BigDecimal.valueOf(Double.parseDouble(multiplierString));
        } catch (Exception e) {
            // If the parsing fails, log a warning and return a default multiplier
            GeneralUtils.logWarning("Invalid multiplier '" + multiplierString + "' specified for '" + name + "'. Defaulting multiplier to 1.");
            return multiplier;
        }

        return multiplier;
    }

    public BigDecimal getStackingMultiplier() {
        BigDecimal multiplier = BigDecimal.ONE;
        try {
            // Try to parse the multiplier string as a BigDecimal
            multiplier = BigDecimal.valueOf(getStackingBaseValue());
        } catch (Exception e) {
            // If the parsing fails, log a warning and return a default multiplier
            GeneralUtils.logWarning("Something went wrong in stacking multiplier, returning default multiplier");
            return multiplier;
        }
        return multiplier;
    }

    // Method to reload the configuration file
    public void reloadConfig() {
        // Reload the config from the disk
        plugin.reloadConfig();
        // Refresh the configuration sections and message from the reloaded config
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.multiplierStacking = plugin.getConfig().getConfigurationSection("sell-multiplier-stacking");
        this.stackingEnabled = multiplierStacking.getBoolean("enable");
        this.baseValue = multiplierStacking.getDouble("base-value");
        this.message = plugin.getConfig().getString("message");
    }

    // Getter methods to access private fields
    public ConfigurationSection getSellMultipliers() {
        return sellMultipliers;
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