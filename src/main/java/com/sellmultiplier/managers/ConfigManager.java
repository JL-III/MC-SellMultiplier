package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.math.BigDecimal;

public class ConfigManager {

    // The section in the configuration file for sell multipliers
    private ConfigurationSection sellMultipliers;
    // The section in the configuration file for multiplier stacking
    private ConfigurationSection multiplierStacking;
    // The message to be displayed from the configuration file
    private String message;
    // The instance of the plugin
    private final Plugin plugin;

    // Constructor to initialize plugin and get sections from the config file
    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.multiplierStacking = plugin.getConfig().getConfigurationSection("sell-multiplier-stacking");
        this.message = plugin.getConfig().getString("message");
    }

    // Method to get a specific multiplier from the config
    public BigDecimal getMultiplierFromConfig(String name) {
        // By default, the multiplier is 1
        BigDecimal multiplier = BigDecimal.ONE;

        // Check if multiplier stacking is enabled
        if (multiplierStacking.getBoolean("enabled")) {
            GeneralUtils.log("Using multiplier stacking");

            // Get the base value from the config
            String multiplierString = multiplierStacking.getString("base-value");
            if (multiplierString == null) {
                GeneralUtils.logWarning("No base-value specified in config! Defaulting to 1");
                return multiplier;
            }
            try {
                // Try to parse the multiplier string as a BigDecimal
                multiplier = BigDecimal.valueOf(Double.parseDouble(multiplierString));
                GeneralUtils.log("Base value is " + multiplier);
            } catch (Exception e) {
                // If the parsing fails, log a warning and return a default multiplier
                GeneralUtils.logWarning("Invalid multiplier '" + multiplierString + "' specified for '" + name + "'. Defaulting multiplier to 1.");
                return multiplier;
            }
            return multiplier;
        } else {
            // If multiplier stacking is not enabled

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
    }

    // Method to reload the configuration file
    public void reloadConfig() {
        // Reload the config from the disk
        plugin.reloadConfig();
        // Refresh the configuration sections and message from the reloaded config
        this.sellMultipliers = plugin.getConfig().getConfigurationSection("sell-multipliers");
        this.multiplierStacking = plugin.getConfig().getConfigurationSection("sell-multiplier-stacking");
        this.message = plugin.getConfig().getString("message");
    }

    // Getter methods to access private fields
    public ConfigurationSection getSellMultipliers() {
        return sellMultipliers;
    }

    public ConfigurationSection getMultiplierStacking() {
        return multiplierStacking;
    }

    public String getMessage() {
        return message;
    }
}