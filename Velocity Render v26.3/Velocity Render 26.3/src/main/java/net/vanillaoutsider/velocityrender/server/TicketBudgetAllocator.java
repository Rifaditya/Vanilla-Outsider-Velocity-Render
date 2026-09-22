// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

/**
 * Pure stateless algorithmic utility for calculating fair multi-player ticket budgets
 * and speed-weighted quota distribution under Minecraft 26.3.
 *
 * <p>Operates with zero heap allocations on the 20 TPS server tick hot path.
 */
public final class TicketBudgetAllocator {

    private TicketBudgetAllocator() {
    }

    /**
     * Calculates the fair forward ticket quota for an active flyer based on their speed share,
     * protecting active players from total corridor collapse via a dynamic safety floor.
     *
     * @param playerSpeed       the instantaneous speed of the player in blocks per tick (b/t)
     * @param totalSpeedSum     the sum of speeds across all currently active flyers in b/t
     * @param activeFlyerCount  the total number of flyers meeting the minimum speed threshold
     * @param serverBudget      the configured server-wide ticket budget cap (e.g. 64)
     * @param maxAllowedReach   the upper reach limit dictated by lead multiplier and MSPT
     * @return the number of forward tickets allocated to this player (0 if inactive/invalid)
     */
    public static int calculatePlayerQuota(
            double playerSpeed,
            double totalSpeedSum,
            int activeFlyerCount,
            int serverBudget,
            int maxAllowedReach
    ) {
        if (playerSpeed <= 0.0 || totalSpeedSum <= 0.0 || activeFlyerCount <= 0 || serverBudget <= 0 || maxAllowedReach <= 0) {
            return 0;
        }

        // Dynamic safety floor: 4 tickets per flyer if budget permits, otherwise split budget evenly down to 1
        long totalFloorRequired = (long) activeFlyerCount * 4L;
        int floor = (serverBudget >= totalFloorRequired)
                ? 4
                : Math.max(1, serverBudget / activeFlyerCount);

        // Proportional speed-weighted raw share with overflow safety
        double ratio = Math.max(0.0, Math.min(1.0, playerSpeed / totalSpeedSum));
        long rawCalculated = Math.round(ratio * (double) serverBudget);
        int rawQuota = (int) Math.min((long) Integer.MAX_VALUE, rawCalculated);

        // Clamped quota: guaranteed floor, capped by max allowed reach
        return Math.max(floor, Math.min(rawQuota, maxAllowedReach));
    }
}
