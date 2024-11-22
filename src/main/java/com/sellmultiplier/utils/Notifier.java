package com.sellmultiplier.utils;

import com.sellmultiplier.events.UserBalanceEvent;
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

        BigDecimal aggregate = UserBalanceEvent.aggregates.get(player.getUniqueId());

        if (aggregate == null) {
            return;
        }

        if (aggregate.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        player.sendMessage(Util.getBonusMessage(
                perm.toUpperCase(Locale.ROOT),
                aggregate.setScale(2, RoundingMode.HALF_UP))
        );
        UserBalanceEvent.aggregates.remove(player.getUniqueId());
    }
}
