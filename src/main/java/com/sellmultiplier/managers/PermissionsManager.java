package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsManager {

    private final ConfigManager configManager;
    private Set<String> multiplierPerms;

    public PermissionsManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void loadMultiplierPerms() {
        ConfigurationSection multiplierStacking = configManager.getMultiplierStacking();
        if (multiplierStacking != null && multiplierStacking.getBoolean("enabled")) {
            GeneralUtils.log("Using multiplier stacking");
            this.multiplierPerms = multiplierStacking.getKeys(false).stream().filter(key -> !key.equalsIgnoreCase("enabled")).collect(Collectors.toSet());
            GeneralUtils.log("Loaded " + multiplierPerms.size() + " multipliers");
            for (String multiplierPerm : multiplierPerms) {
                GeneralUtils.log("Loaded multiplier " + multiplierPerm);
            }
            return;
        }

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
