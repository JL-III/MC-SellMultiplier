package com.sellmultiplier.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Notifier extends BukkitRunnable {
    private final Player player;
    private final String perm;
    private final BigDecimal aggregate;

    public Notifier(Player player, String perm, @NotNull BigDecimal aggregate) {
        this.player = player;
        this.perm = perm;
        this.aggregate = aggregate;
    }

    @Override
    public void run() {

        if (aggregate.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        player.sendMessage(String.format(
                        Util.MESSAGE,
                        perm.toUpperCase(Locale.ROOT),
                        aggregate.setScale(
                                2,
                                RoundingMode.HALF_UP
                        ))
        );
    }
}
