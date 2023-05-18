package com.sellmultiplier.events;

import com.sellmultiplier.utils.Multiplier;
import com.sellmultiplier.utils.Notifier;
import com.sellmultiplier.managers.ConfigManager;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.utils.GeneralUtils;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.entity.Player;
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
        if (event.getCause() != UserBalanceUpdateEvent.Cause.COMMAND_SELL) {
            return;
        }
        Multiplier multiplier;
        if (configManager.getStackingEnabled()) {
            multiplier = multiplierManager.getStackedMultiplier(event.getPlayer());
        } else {
            multiplier = multiplierManager.getMultiplier(event.getPlayer());
        }
        applyAndLogMultiplierBonus(event, multiplier);
    }

    public void applyAndLogMultiplierBonus(UserBalanceUpdateEvent event, Multiplier multiplier) {
        BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());
        if (multiplier.getValue().compareTo(BigDecimal.ONE) > 0 && diff.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multipliedDiff = diff.multiply(multiplier.getValue());
            event.setNewBalance(event.getOldBalance().add(multipliedDiff));
            BigDecimal amountAdded = multipliedDiff.subtract(diff);
            GeneralUtils.log("sell-multiplier bonus of $" + amountAdded.setScale(2, RoundingMode.HALF_UP) +
                    " applied for " + event.getPlayer().getName() + " (permission: " + multiplier.getKey().toUpperCase() + ")");
            // If the multiplier key isn't "default"
            if (!"default".equalsIgnoreCase(multiplier.getKey())) {
                processAggregates(event.getPlayer(), multiplier.getKey(), amountAdded);
            }
        }
    }

    public void processAggregates(Player player, String multiplierKey, BigDecimal amountAdded) {
        // Check if the user's UUID is already in the aggregate map
        if (aggregates.containsKey(player.getUniqueId())) {
            // If yes, add the newly added amount to the aggregate amount for that user
            BigDecimal aggregate = aggregates.get(player.getUniqueId());
            aggregate = aggregate.add(amountAdded);
            aggregates.put(player.getUniqueId(), aggregate);
        } else {
            // If not, add the new user and the amount to the map
            aggregates.put(player.getUniqueId(), amountAdded);
        }
        // Create a new notifier task to notify the user and run it after a delay
        new Notifier(configManager, player, multiplierKey).runTaskLater(plugin, 1);
    }

}
