package com.sellmultiplier.managers;

import com.sellmultiplier.Multiplier;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class MultiplierManager {

    private final ConfigManager configManager;
    private final PermissionsManager permissionsManager;

    public MultiplierManager(ConfigManager configManager, PermissionsManager permissionsManager) {
        this.configManager = configManager;
        this.permissionsManager = permissionsManager;
    }

    public Multiplier getMultiplier(Player player) {
        String key = "default";
        BigDecimal value = configManager.getMultiplierFromConfig("default");

        if (player == null || permissionsManager.getMultiplierPerms() == null) {
            return new Multiplier(key, value);
        }

        for (String multiplierPerm : permissionsManager.getMultiplierPerms()) {
            if (player.hasPermission("sell.multiplier." + multiplierPerm)) {
                BigDecimal permValue = configManager.getMultiplierFromConfig(multiplierPerm);
                if (permValue.compareTo(value) > 0) {
                    key = multiplierPerm;
                    value = permValue;
                }
            }
        }

        return new Multiplier(key, value);
    }

}
