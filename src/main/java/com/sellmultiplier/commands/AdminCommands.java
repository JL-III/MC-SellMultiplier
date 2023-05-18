package com.sellmultiplier.commands;

import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.PermissionsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        configManager.reloadConfig();
        permissionsManager.loadMultiplierPerms();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded sellmultiplier config");
        return true;
    }
}
