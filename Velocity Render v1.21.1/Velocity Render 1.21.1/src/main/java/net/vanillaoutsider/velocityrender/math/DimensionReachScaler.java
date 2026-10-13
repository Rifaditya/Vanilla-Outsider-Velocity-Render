// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

/**
 * Pure stateless algorithmic utility for scaling forward lookahead chunk reach
 * across distinct dimensions (Nether, End, dense modded dimensions).
 *
 * <p>Operates with zero heap allocations on hot-path server tick evaluations.
 */
public final class DimensionReachScaler {

    public static final int MIN_CLAMP_PCT = 0;
    public static final int MAX_CLAMP_PCT = Integer.MAX_VALUE;
    public static final int MIN_REACH_FLOOR = 2;

    private DimensionReachScaler() {
    }

    /**
     * Calculates the clamped forward chunk reach based on the dimension scaling percentage.
     *
     * @param baseReach the base calculated lookahead reach in chunks (from speed, lead multiplier, and MSPT)
     * @param clampPct  the scaling percentage applied to this dimension (e.g. 60 for Nether, 80 for dense, 0 for mute)
     * @return the clamped reach in chunks (0 if baseReach <= 0 or clampPct <= 0; guaranteed >= 2 if baseReach >= 2 and clampPct > 0)
     */
    public static int calculateClampedReach(int baseReach, int clampPct) {
        if (baseReach <= 0 || clampPct <= 0) {
            return 0;
        }

        int effectiveClamp = Math.max(MIN_CLAMP_PCT, Math.min(MAX_CLAMP_PCT, clampPct));
        int scaled = (int) Math.min((long) Integer.MAX_VALUE, Math.round((double) baseReach * ((double) effectiveClamp / 100.0)));

        return Math.max(MIN_REACH_FLOOR, scaled);
    }
}
