package com.sellmultiplier.events;

import com.earth2me.essentials.Essentials;
import com.sellmultiplier.managers.MultiplierManager;
import com.sellmultiplier.utils.Multiplier;
import com.sellmultiplier.utils.Util;
import java.math.BigDecimal;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Appends a projected (multiplier-adjusted) value to Essentials' /worth output.
 * Essentials still prints the authoritative base worth; we only bolt on an extra
 * line, and only when the player can use /worth and actually has a bonus.
 */
public class WorthListener implements Listener {
    private final Plugin plugin;
    private final Essentials essentials;
    private final MultiplierManager multiplierManager;

    public WorthListener(Plugin plugin, Essentials essentials, MultiplierManager multiplierManager) {
        this.plugin = plugin;
        this.essentials = essentials;
        this.multiplierManager = multiplierManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorthCommand(PlayerCommandPreprocessEvent event) {
        if (!isWorthCommand(event.getMessage())) return;

        Player player = event.getPlayer();
        // Respect Essentials' own permission: if the player can't use /worth, add nothing.
        if (!player.hasPermission(Util.WORTH_PERMISSION)) return;

        // Only bolt on data when the player actually has a bonus.
        Multiplier multiplier = multiplierManager.getMultiplier(player);
        if (multiplier.getValue().compareTo(BigDecimal.ONE) <= 0) return;

        BigDecimal base = aggregateInventoryWorth(player);
        if (base.signum() <= 0) return;

        BigDecimal projected = base.multiply(multiplier.getValue());
        BigDecimal bonus = projected.subtract(base);

        // Send a tick later so our line appears after Essentials' own /worth output.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                player.sendMessage(
                        Util.getProjectedWorthMessage(
                                multiplier.getKey(), essentials, projected, bonus));
            }
        }.runTaskLater(plugin, 1L);
    }

    /** Matches /worth and its common aliases, including the essentials: namespace. */
    private boolean isWorthCommand(String message) {
        String label = message.startsWith("/") ? message.substring(1) : message;
        int space = label.indexOf(' ');
        if (space != -1) label = label.substring(0, space);
        label = label.toLowerCase(Locale.ROOT);
        if (label.startsWith("essentials:")) label = label.substring("essentials:".length());
        return switch (label) {
            case "worth", "eworth", "price", "eprice" -> true;
            default -> false;
        };
    }

    /** Sum of the base sell value of every sellable item in the player's inventory. */
    private BigDecimal aggregateInventoryWorth(Player player) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null) continue;
            // Price a single unit, then scale by the real amount, so we don't depend
            // on whether Essentials' getPrice factors in the stack size.
            ItemStack one = stack.clone();
            one.setAmount(1);
            BigDecimal unit = essentials.getWorth().getPrice(essentials, one);
            if (unit == null || unit.signum() < 0) continue;
            total = total.add(unit.multiply(BigDecimal.valueOf(stack.getAmount())));
        }
        return total;
    }
}
