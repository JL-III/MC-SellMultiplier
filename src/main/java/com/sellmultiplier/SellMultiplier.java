package com.sellmultiplier;

import com.sellmultiplier.commands.Multiplier;
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

        MultiplierManager multiplierManager = new MultiplierManager();

        Objects.requireNonNull(getCommand("multiplier")).setExecutor(new Multiplier(multiplierManager));
        Bukkit.getPluginManager().registerEvents(new UserBalanceEvent(this, multiplierManager), this);
    }
}
