package com.sellmultiplier.events;

import com.sellmultiplier.utils.Multiplier;
import com.sellmultiplier.utils.Notifier;
import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.utils.GeneralUtils;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserBalanceEvent implements Listener {
    // The plugin instance
    private Plugin plugin;
    // The configuration manager instance
    private ConfigManager configManager;
    // The multiplier manager instance
    private final MultiplierManager multiplierManager;
    // A map to store aggregates of user balances
    public static final ConcurrentHashMap<UUID, BigDecimal> aggregates = new ConcurrentHashMap<>();

    // Constructor initializing plugin, config manager and multiplier manager
    public UserBalanceEvent(Plugin plugin, ConfigManager configManager, MultiplierManager multiplierManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.multiplierManager = multiplierManager;
    }

    // Event handler for when a user's balance is updated
    @EventHandler
    public void onUserBalanceUpdateEvent(UserBalanceUpdateEvent event) {
        // If the cause of balance update isn't due to selling command, return and do nothing
        if (event.getCause() != UserBalanceUpdateEvent.Cause.COMMAND_SELL) {
            return;
        }

        if (configManager.getStackingEnabled()) {
            GeneralUtils.log("Stacking enabled");
            double multiplier = multiplierManager.getStackedMultiplier(event.getPlayer());  // no division by 10 now
            BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());
            if (BigDecimal.valueOf(multiplier).compareTo(BigDecimal.ONE) > 0 && diff.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal multipliedDiff = diff.multiply(BigDecimal.valueOf(multiplier));
                event.setNewBalance(event.getOldBalance().add(multipliedDiff));
                BigDecimal amountAdded = multipliedDiff.subtract(diff);
                GeneralUtils.log("sell-multiplier bonus of $" + amountAdded.setScale(2, RoundingMode.HALF_UP) +
                        " applied for " + event.getPlayer().getName() + " (permission: " + multiplier + ")");
                // If the multiplier key isn't "default"
                if (multiplier > 0) {
                    // Check if the user's UUID is already in the aggregate map
                    if (aggregates.containsKey(event.getPlayer().getUniqueId())) {
                        // If yes, add the newly added amount to the aggregate amount for that user
                        BigDecimal aggregate = aggregates.get(event.getPlayer().getUniqueId());
                        aggregate = aggregate.add(amountAdded);
                        aggregates.put(event.getPlayer().getUniqueId(), aggregate);
                    } else {
                        // If not, add the new user and the amount to the map
                        aggregates.put(event.getPlayer().getUniqueId(), amountAdded);
                    }

                    // Create a new notifier task to notify the user and run it after a delay
                    new Notifier(configManager, event.getPlayer(), Double.toString(multiplier)).runTaskLater(plugin, 1);
                }
            }
            return;
        }

        // Get the multiplier for the player
        Multiplier multiplier = multiplierManager.getMultiplier(event.getPlayer());
        // Calculate the difference between new balance and old balance
        BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());

        // If multiplier value is greater than one and difference is greater than zero
        if (multiplier.getValue().compareTo(BigDecimal.ONE) > 0 && diff.compareTo(BigDecimal.ZERO) > 0) {
            // Multiply the difference with multiplier value
            BigDecimal multipliedDiff = diff.multiply(multiplier.getValue());
            // Set the new balance of the event
            event.setNewBalance(event.getOldBalance().add(multipliedDiff));
            // Calculate the additional amount added due to multiplier
            BigDecimal amountAdded = multipliedDiff.subtract(diff);

            // Log the information about the added bonus
            GeneralUtils.log("sell-multiplier bonus of $" + amountAdded.setScale(2, RoundingMode.HALF_UP) +
                    " applied for " + event.getPlayer().getName() + " (permission: " + multiplier.getKey().toUpperCase() + ")");

            // If the multiplier key isn't "default"
            if (!"default".equalsIgnoreCase(multiplier.getKey())) {
                // Check if the user's UUID is already in the aggregate map
                if (aggregates.containsKey(event.getPlayer().getUniqueId())) {
                    // If yes, add the newly added amount to the aggregate amount for that user
                    BigDecimal aggregate = aggregates.get(event.getPlayer().getUniqueId());
                    aggregate = aggregate.add(amountAdded);
                    aggregates.put(event.getPlayer().getUniqueId(), aggregate);
                } else {
                    // If not, add the new user and the amount to the map
                    aggregates.put(event.getPlayer().getUniqueId(), amountAdded);
                }

                // Create a new notifier task to notify the user and run it after a delay
                new Notifier(configManager, event.getPlayer(), multiplier.getKey()).runTaskLater(plugin, 1);
            }
        }
    }

}
