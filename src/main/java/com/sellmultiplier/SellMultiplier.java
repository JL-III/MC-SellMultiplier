package com.sellmultiplier;

import com.sellmultiplier.commands.AdminCommands;
import com.sellmultiplier.events.UserBalanceEvent;
import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.managers.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SellMultiplier extends JavaPlugin {


    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();

        ConfigManager configManager = new ConfigManager(this);
        PermissionsManager permissionsManager = new PermissionsManager(configManager);
        MultiplierManager multiplierManager = new MultiplierManager(configManager, permissionsManager);

        permissionsManager.loadPerms();
        Objects.requireNonNull(getCommand("sellmultiplier")).setExecutor(new AdminCommands(configManager, permissionsManager));
        Bukkit.getPluginManager().registerEvents(new UserBalanceEvent(this, configManager, multiplierManager), this);
    }

}
