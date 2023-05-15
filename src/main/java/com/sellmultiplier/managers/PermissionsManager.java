package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class PermissionsManager {

    private final ConfigManager configManager;
    private Set<String> multiplierPerms;

    public PermissionsManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void loadMultiplierPerms() {
        ConfigurationSection section = configManager.getSellMultipliers();
        if (section == null) {
            GeneralUtils.logWarning("No sell-multiplier specified in config!");
            return;
        }
        this.multiplierPerms = section.getKeys(false);
    }

    public Set<String> getMultiplierPerms() {
        return multiplierPerms;
    }

}
