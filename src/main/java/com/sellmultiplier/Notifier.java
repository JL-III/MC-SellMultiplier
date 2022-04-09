package com.sellmultiplier;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Notifier extends BukkitRunnable {
    private final Player player;
    private final String perm;

    public Notifier(Player player, String perm) {
        this.player = player;
        this.perm = perm;
    }

    @Override
    public void run() {
        String message = SellMultiplier.configuration.getString("message");
        if (message == null) {
            SellMultiplier.logger.warning("No message is set in config!");
            return;
        }

        BigDecimal aggregate = SellMultiplier.aggregates.get(player.getUniqueId());
        if (aggregate == null) {
            return;
        }

        message = String.format(message, perm.toUpperCase(Locale.ROOT), aggregate.setScale(2, RoundingMode.HALF_UP));
        player.sendMessage(message);
        SellMultiplier.aggregates.remove(player.getUniqueId());
    }
}
