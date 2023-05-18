package com.sellmultiplier.commands;

import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.managers.PermissionsManager;
import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private final ConfigManager configManager;
    private final PermissionsManager permissionsManager;
    private final MultiplierManager multiplierManager;

    public AdminCommands(ConfigManager configManager, PermissionsManager permissionsManager, MultiplierManager multiplierManager) {
        this.configManager = configManager;
        this.permissionsManager = permissionsManager;
        this.multiplierManager = multiplierManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (!player.hasPermission("sell.multipliers.admin")) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
            try {
                GeneralUtils.sendPermissionMessage(player, multiplierManager.getStringsForPlayerPermCheck(Bukkit.getPlayer(args[1])));
                return true;
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
        }

        configManager.reloadConfig();
        permissionsManager.loadMultiplierPerms();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded sellmultiplier config");
        return true;
    }
}
