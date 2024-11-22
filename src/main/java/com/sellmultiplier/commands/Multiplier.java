package com.sellmultiplier.commands;

import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Multiplier implements CommandExecutor, TabCompleter {
    private final MultiplierManager multiplierManager;

    public Multiplier(MultiplierManager multiplierManager) {
        this.multiplierManager = multiplierManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission(Util.CHECK_SELF_PERMISSION) || !commandSender.hasPermission(Util.CHECK_OTHER_PERMISSION)) return new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                if (commandSender.hasPermission(Util.CHECK_OTHER_PERMISSION)) {
                    return new ArrayList<>(){{
                        add("check");
                    }};
                }
            }
            case 2 -> {
                if (commandSender.hasPermission(Util.CHECK_OTHER_PERMISSION)) {
                    return new ArrayList<>(){{
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            add(player.getName());
                        }
                    }};
                }
            }
            default -> {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0 -> {
                if (!sender.hasPermission(Util.CHECK_SELF_PERMISSION)) {
                    sender.sendMessage(Util.NO_PERMISSION);
                    return true;
                }
                Util.sendPermissionMessage(sender, multiplierManager.getStringsForPlayerPermCheck(sender));
                return true;
            }
            case 2 -> {
                if (!sender.hasPermission(Util.CHECK_OTHER_PERMISSION)) {
                    sender.sendMessage(Util.NO_PERMISSION);
                    return true;
                }
                if (!args[0].equalsIgnoreCase("check")) return false;
                try {
                    Util.sendPermissionMessage(sender, multiplierManager.getMultiplierPermissions(Bukkit.getPlayer(args[1])));
                    return true;
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
            }
            default -> {
                return false;
            }
        }

    }
}
