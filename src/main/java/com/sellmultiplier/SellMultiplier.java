package com.sellmultiplier;

import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SellMultiplier extends JavaPlugin implements Listener, CommandExecutor {
    private Set<String> multiplierPerms;
    public static final ConcurrentHashMap<UUID, BigDecimal> aggregates = new ConcurrentHashMap<>();
    public static Logger logger;
    public static FileConfiguration configuration;

    public SellMultiplier() {
        logger = getLogger();
        configuration = getConfig();
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
        loadMultiplierPerms();
        getCommand("sellmultiplier").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onUserBalanceUpdateEvent(UserBalanceUpdateEvent event) {
        if (event.getCause() != UserBalanceUpdateEvent.Cause.COMMAND_SELL) {
            return;
        }

        Multiplier multiplier = getMultiplier(event.getPlayer());
        BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());

        if (multiplier.getValue().compareTo(BigDecimal.ONE) > 0 && diff.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multipliedDiff = diff.multiply(multiplier.getValue());
            event.setNewBalance(event.getOldBalance().add(multipliedDiff));
            BigDecimal amountAdded = multipliedDiff.subtract(diff);

            logger.info("sell-multiplier bonus of $" + amountAdded.setScale(2, RoundingMode.HALF_UP) +
                    " applied for " + event.getPlayer().getName() + " (permission: " + multiplier.getKey().toUpperCase() + ")");

            if (!"default".equalsIgnoreCase(multiplier.getKey())) {
                if (aggregates.containsKey(event.getPlayer().getUniqueId())) {
                    BigDecimal aggregate = aggregates.get(event.getPlayer().getUniqueId());
                    aggregate = aggregate.add(amountAdded);
                    aggregates.put(event.getPlayer().getUniqueId(), aggregate);
                } else {
                    aggregates.put(event.getPlayer().getUniqueId(), amountAdded);
                }

                new Notifier(event.getPlayer(), multiplier.getKey()).runTaskLater(this, 1);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (!player.hasPermission("sell.multipliers.reload")) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0 || !"reload".equalsIgnoreCase(args[0])) {
            return false;
        }

        reloadConfig();
        loadMultiplierPerms();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded sellmultiplier config");
        return true;
    }

    private void loadMultiplierPerms() {
        ConfigurationSection section = this.getConfig().getConfigurationSection("sell-multipliers");
        if (section == null) {
            getLogger().warning("No sell-multiplier specified in config!");
            return;
        }
        this.multiplierPerms = section.getKeys(false);
    }

    private BigDecimal getMultiplierFromConfig(String name) {
        BigDecimal multiplier = BigDecimal.ONE;
        ConfigurationSection section = this.getConfig().getConfigurationSection("sell-multipliers");
        if (section == null) {
            getLogger().warning("No sell-multipliers specified in config! Defaulting to 1");
            return multiplier;
        }

        String multiplierString = section.getString(name);
        if (multiplierString == null) {
            getLogger().warning("No multiplier specified for '" + name + "'! Defaulting to 1");
            return multiplier;
        }

        try {
            multiplier = BigDecimal.valueOf(Double.parseDouble(multiplierString));
        } catch (Exception e) {
            getLogger().warning("Invalid multiplier '" + multiplierString + "' specified for '" + name + "'. Defaulting multiplier to 1.");
            return multiplier;
        }

        return multiplier;
    }

    private Multiplier getMultiplier(Player player) {
        String key = "default";
        BigDecimal value = getMultiplierFromConfig("default");

        if (player == null || multiplierPerms == null) {
            return new Multiplier(key, value);
        }

        for (String multiplierPerm : multiplierPerms) {
            if (player.hasPermission("sell.multiplier." + multiplierPerm)) {
                BigDecimal permValue = getMultiplierFromConfig(multiplierPerm);
                if (permValue.compareTo(value) > 0) {
                    key = multiplierPerm;
                    value = permValue;
                }
            }
        }

        return new Multiplier(key, value);
    }
}
