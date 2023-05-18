package com.sellmultiplier.commands;

import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.managers.PermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements CommandExecutor {
    private MultiplierManager multiplierManager;
    
    public PlayerCommands(MultiplierManager multiplierManager) {
        this.multiplierManager = multiplierManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            for (String s : multiplierManager.getStringsForPlayerPermCheck(player)) {
                player.sendMessage(s);
            }
            return true;
        }

        return false;
    }
}
