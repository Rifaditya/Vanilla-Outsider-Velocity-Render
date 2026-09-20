// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketBudgetAllocatorTest {

    @Test
    @DisplayName("Single flyer receives full reach ceiling within server budget")
    void testSingleFlyerFullCeiling() {
        // 1 flyer flying at 1.5 b/t, total sum = 1.5, budget = 64, reach cap = 16
        int quota = TicketBudgetAllocator.calculatePlayerQuota(1.5, 1.5, 1, 64, 16);
        assertEquals(16, quota, "Single flyer should receive full reach cap when budget is generous");
    }

    @Test
    @DisplayName("Two symmetrical flyers split budget evenly up to max reach")
    void testTwoSymmetricalFlyers() {
        // 2 flyers at 1.0 b/t each, total sum = 2.0, budget = 24, reach cap = 16
        // Raw share = round((1.0 / 2.0) * 24) = 12
        int quotaP1 = TicketBudgetAllocator.calculatePlayerQuota(1.0, 2.0, 2, 24, 16);
        int quotaP2 = TicketBudgetAllocator.calculatePlayerQuota(1.0, 2.0, 2, 24, 16);

        assertEquals(12, quotaP1);
        assertEquals(12, quotaP2);
        assertEquals(24, quotaP1 + quotaP2, "Sum of quotas should match total budget");
    }

    @Test
    @DisplayName("Asymmetrical flyers receive speed-weighted proportional quotas")
    void testAsymmetricalFlyers() {
        // Flyer A: 1.5 b/t (75%), Flyer B: 0.5 b/t (25%), total sum = 2.0, budget = 32, reach cap = 32
        // Raw A = round(0.75 * 32) = 24
        // Raw B = round(0.25 * 32) = 8
        int quotaA = TicketBudgetAllocator.calculatePlayerQuota(1.5, 2.0, 2, 32, 32);
        int quotaB = TicketBudgetAllocator.calculatePlayerQuota(0.5, 2.0, 2, 32, 32);

        assertEquals(24, quotaA);
        assertEquals(8, quotaB);
        assertEquals(32, quotaA + quotaB);
    }

    @Test
    @DisplayName("Dynamic safety floor of 4 tickets is enforced during moderate budget pressure")
    void testSafetyFloorEnforcement() {
        // Flyer slow: 0.1 b/t out of 10.0 b/t total across 4 flyers, budget = 64, reach cap = 16
        // Raw share = round((0.1 / 10.0) * 64) = 1
        // Dynamic floor = 4 (since 64 >= 4 * 4 = 16)
        int quota = TicketBudgetAllocator.calculatePlayerQuota(0.1, 10.0, 4, 64, 16);
        assertEquals(4, quota, "Should enforce minimum safety floor of 4 tickets");
    }

    @Test
    @DisplayName("Extreme flyer saturation clamps dynamic floor without exceeding budget")
    void testExtremeFlyerSaturation() {
        // 20 flyers competing for 64 tickets (20 * 4 = 80 > 64)
        // Dynamic floor = max(1, 64 / 20) = 3
        int quota = TicketBudgetAllocator.calculatePlayerQuota(0.01, 10.0, 20, 64, 16);
        assertEquals(3, quota, "Extreme saturation should dynamically scale floor to prevent overbooking");
    }

    @Test
    @DisplayName("Invalid or zero speed parameters return zero tickets")
    void testInvalidParametersReturnZero() {
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(0.0, 10.0, 2, 64, 16));
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(-1.0, 10.0, 2, 64, 16));
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(1.0, 0.0, 2, 64, 16));
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(1.0, 10.0, 0, 64, 16));
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(1.0, 10.0, 2, 0, 16));
        assertEquals(0, TicketBudgetAllocator.calculatePlayerQuota(1.0, 10.0, 2, 64, 0));
    }
}
