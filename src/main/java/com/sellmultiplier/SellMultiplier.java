package com.sellmultiplier;

import com.sellmultiplier.commands.AdminCommands;
import com.sellmultiplier.events.UserBalanceEvent;
import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.managers.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class SellMultiplier extends JavaPlugin {
    public static Logger logger;
    private ConfigManager configManager;
    private PermissionsManager permissionsManager;
    private MultiplierManager multiplierManager;

    public SellMultiplier() {
        logger = getLogger();
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();

        configManager = new ConfigManager(this);
        permissionsManager = new PermissionsManager(configManager);
        multiplierManager = new MultiplierManager(configManager, permissionsManager);

        permissionsManager.loadMultiplierPerms();
        Objects.requireNonNull(getCommand("sellmultiplier")).setExecutor(new AdminCommands(configManager, permissionsManager));
        Bukkit.getPluginManager().registerEvents(new UserBalanceEvent(this, configManager, multiplierManager), this);
    }

}
