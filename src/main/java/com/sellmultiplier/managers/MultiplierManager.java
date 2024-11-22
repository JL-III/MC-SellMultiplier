package com.sellmultiplier.managers;

import com.sellmultiplier.utils.Multiplier;
import com.sellmultiplier.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiplierManager {

    public Multiplier getMultiplier(Player player) {
        Set<String> multiplierPermissions = getMultiplierPermissions(player);
        // Fetch the base multiplier from the config and adjust it
        BigDecimal baseMultiplier = BigDecimal.valueOf(Util.BASE_VALUE);
        return new Multiplier(
                Integer.toString(multiplierPermissions.size()),
                BigDecimal.valueOf(1).add(BigDecimal.valueOf(multiplierPermissions.size()).multiply(baseMultiplier))
        );
    }

    public Set<String> getStringsForPlayerPermCheck(CommandSender sender) {
        Set<String> result = new HashSet<>();
        Set<String> multiplierPermissions = getMultiplierPermissions(sender);

        for (String permission : multiplierPermissions) {
            // Add the permission suffix to the result set, with the first letter capitalized
            String permissionSuffix = permission.substring("sell.multiplier.".length());
            permissionSuffix = Character.toUpperCase(permissionSuffix.charAt(0)) + permissionSuffix.substring(1);
            result.add(permissionSuffix);
        }

        return result;
    }

    public Set<String> getMultiplierPermissions(CommandSender sender) {
        Set<String> multiplierPermissions = new HashSet<>();
        Pattern pattern = Pattern.compile("^sell\\.multiplier\\.(.*)");

        for (PermissionAttachmentInfo perm : sender.getEffectivePermissions()) {
            String permission = perm.getPermission();
            // Check if permission matches pattern
            Matcher matcher = pattern.matcher(permission);
            if (matcher.find()) {
                multiplierPermissions.add(permission);
            }
        }
        return multiplierPermissions;
    }

}

