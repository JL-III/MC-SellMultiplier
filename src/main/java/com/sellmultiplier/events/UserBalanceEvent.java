package com.sellmultiplier.events;

import com.sellmultiplier.Multiplier;
import com.sellmultiplier.Notifier;
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
    private Plugin plugin;
    private ConfigManager configManager;
    private final MultiplierManager multiplierManager;
    public static final ConcurrentHashMap<UUID, BigDecimal> aggregates = new ConcurrentHashMap<>();

    public UserBalanceEvent(Plugin plugin, ConfigManager configManager, MultiplierManager multiplierManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.multiplierManager = multiplierManager;
    }

    @EventHandler
    public void onUserBalanceUpdateEvent(UserBalanceUpdateEvent event) {
        if (event.getCause() != UserBalanceUpdateEvent.Cause.COMMAND_SELL) {
            return;
        }

        Multiplier multiplier = multiplierManager.getMultiplier(event.getPlayer());
        BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());

        if (multiplier.getValue().compareTo(BigDecimal.ONE) > 0 && diff.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multipliedDiff = diff.multiply(multiplier.getValue());
            event.setNewBalance(event.getOldBalance().add(multipliedDiff));
            BigDecimal amountAdded = multipliedDiff.subtract(diff);

            GeneralUtils.log("sell-multiplier bonus of $" + amountAdded.setScale(2, RoundingMode.HALF_UP) +
                    " applied for " + event.getPlayer().getName() + " (permission: " + multiplier.getKey().toUpperCase() + ")");

            if (!"default".equalsIgnoreCase(multiplier.getKey())) {
                if (aggregates.containsKey(event.getPlayer().getUniqueId())) {
                    BigDecimal aggregate = aggregates.get(event.getPlayer().getUniqueId());
                    aggregate = aggregate.add(amountAdded);
                    aggregates.put(event.getPlayer().getUniqueId(), aggregate);
                } else {
                    aggregates.put(event.getPlayer().getUniqueId(), amountAdded);
                }

                new Notifier(configManager, event.getPlayer(), multiplier.getKey()).runTaskLater(plugin, 1);
            }
        }
    }

}
