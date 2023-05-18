package com.sellmultiplier.managers;

import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class PermissionsManager {

    private final ConfigManager configManager;
    private Set<String> multiplierPerms;

    public PermissionsManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void loadPerms() {
        loadMultiplierPerms();
    }

    public void loadMultiplierPerms() {
        Set<String> multiplierNames = configManager.getMultiplierNames();
        if (multiplierNames.isEmpty()) {
            GeneralUtils.logWarning("No sell-multiplier specified in config!");
            return;
        }
        this.multiplierPerms = multiplierNames;
    }

    public boolean playerHasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions(Player player) {
        return player.getEffectivePermissions();
    }

    public Set<String> getMultiplierPerms() {
        return multiplierPerms;
    }

}
