package com.sellmultiplier.commands;

import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.PermissionsManager;
import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminCommands implements CommandExecutor {

    private final ConfigManager configManager;
    private final PermissionsManager permissionsManager;

    public AdminCommands(ConfigManager configManager, PermissionsManager permissionsManager) {
        this.configManager = configManager;
        this.permissionsManager = permissionsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (!player.hasPermission("sell.multipliers.reload")) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0 || !"reload".equalsIgnoreCase(args[0])) {
            Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
            GeneralUtils.log("Permissions for " + player.getName());
            // Regex pattern to match 'sell.multiplier.*'
            Pattern pattern = Pattern.compile("^sell\\.multiplier\\..*");
            // Counter for occurrences
            int count = 0;
            for (PermissionAttachmentInfo perm : perms) {
                String permission = perm.getPermission();
                GeneralUtils.log(permission);
                // Check if permission matches pattern
                Matcher matcher = pattern.matcher(permission);
                if (matcher.find()) {
                    count++;
                }
            }
            // Log the count
            GeneralUtils.log("Count of 'sell.multiplier.*' permissions: " + count);
            return false;
        }


        configManager.reloadConfig();
        permissionsManager.loadMultiplierPerms();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded sellmultiplier config");
        return true;
    }
}
