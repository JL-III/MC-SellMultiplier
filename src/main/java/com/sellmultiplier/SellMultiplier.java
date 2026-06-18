package com.sellmultiplier;

import com.sellmultiplier.commands.Multiplier;
import com.sellmultiplier.config.ConfigManager;
import com.sellmultiplier.events.UserBalanceEvent;
import com.sellmultiplier.managers.MultiplierManager;
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
        MultiplierManager multiplierManager = new MultiplierManager(configManager);

        Multiplier multiplierCommand = new Multiplier(multiplierManager, configManager);
        var multiplierCmd = Objects.requireNonNull(getCommand("multiplier"));
        multiplierCmd.setExecutor(multiplierCommand);
        multiplierCmd.setTabCompleter(multiplierCommand);

        Bukkit.getPluginManager().registerEvents(new UserBalanceEvent(this, multiplierManager), this);
    }
}
