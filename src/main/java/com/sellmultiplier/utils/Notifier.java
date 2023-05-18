package com.sellmultiplier.utils;

import com.sellmultiplier.SellMultiplier;
import com.sellmultiplier.events.UserBalanceEvent;
import com.sellmultiplier.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Notifier extends BukkitRunnable {
    // ConfigManager is responsible for handling the configuration of the plugin
    private final ConfigManager configManager;

    // The Player instance represents a player in the game
    private final Player player;

    // The 'perm' string represents a player's permission
    private final String perm;

    // The constructor initializes ConfigManager, Player, and perm from parameters
    public Notifier(ConfigManager configManager, Player player, String perm) {
        this.configManager = configManager;
        this.player = player;
        this.perm = perm;
    }

    // Overriding the run() method from BukkitRunnable, which gets called when the Notifier is run
    @Override
    public void run() {
        // Fetches a message from the configManager
        String message = configManager.getMessage();

        // If the message is null, logs a warning and returns early
        if (message == null) {
            SellMultiplier.logger.warning("No message is set in config!");
            return;
        }

        // Fetches the aggregate amount associated with the player's UUID from the UserBalanceEvent aggregates
        BigDecimal aggregate = UserBalanceEvent.aggregates.get(player.getUniqueId());

        // If the aggregate is null, returns early
        if (aggregate == null) {
            return;
        }

        // If the aggregate is zero (when rounded to 2 decimal places), returns early
        if (aggregate.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // Formats the message with the permission string in uppercase and the aggregate rounded to 2 decimal places
        message = String.format(message, perm.toUpperCase(Locale.ROOT), aggregate.setScale(2, RoundingMode.HALF_UP));

        // Sends the formatted message to the player
        player.sendMessage(message);

        // Removes the aggregate associated with the player's UUID from the UserBalanceEvent aggregates
        UserBalanceEvent.aggregates.remove(player.getUniqueId());
    }
}
