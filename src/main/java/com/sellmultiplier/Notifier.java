package com.sellmultiplier;

import com.sellmultiplier.events.UserBalanceEvent;
import com.sellmultiplier.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Notifier extends BukkitRunnable {
    private final ConfigManager configManager;
    private final Player player;
    private final String perm;

    public Notifier(ConfigManager configManager, Player player, String perm) {
        this.configManager = configManager;
        this.player = player;
        this.perm = perm;
    }

    @Override
    public void run() {
        String message = configManager.getMessage();
        if (message == null) {
            SellMultiplier.logger.warning("No message is set in config!");
            return;
        }

        BigDecimal aggregate = UserBalanceEvent.aggregates.get(player.getUniqueId());
        if (aggregate == null) {
            return;
        }

        if (aggregate.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        message = String.format(message, perm.toUpperCase(Locale.ROOT), aggregate.setScale(2, RoundingMode.HALF_UP));
        player.sendMessage(message);
        UserBalanceEvent.aggregates.remove(player.getUniqueId());
    }
}
