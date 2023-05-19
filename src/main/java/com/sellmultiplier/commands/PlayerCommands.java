package com.sellmultiplier.commands;

import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.utils.GeneralUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands implements CommandExecutor {
    private MultiplierManager multiplierManager;

    public PlayerCommands(MultiplierManager multiplierManager) {
        this.multiplierManager = multiplierManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (!player.hasPermission("sell-multiplier.check.self")) {
            player.sendMessage("No permission.");
            return true;
        }

        if (args.length == 0) {
            GeneralUtils.sendPermissionMessage(player, multiplierManager.getStringsForPlayerPermCheck(player));
            return true;
        }

        return false;
    }
}
