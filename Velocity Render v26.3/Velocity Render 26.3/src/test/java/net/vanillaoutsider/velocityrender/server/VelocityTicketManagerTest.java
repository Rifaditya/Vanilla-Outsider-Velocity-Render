// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VelocityTicketManagerTest {

    @Test
    @DisplayName("Query methods should return safe defaults for unregistered player UUIDs")
    void testUnregisteredPlayerQueryDefaults() {
        UUID randomUuid = UUID.randomUUID();

        Assertions.assertEquals(0.0f, VelocityTicketManager.getPlayerTurnRate(randomUuid), 0.001f);
        Assertions.assertEquals(0, VelocityTicketManager.getPlayerTurnSign(randomUuid));
        Assertions.assertEquals(0, VelocityTicketManager.getActiveTicketCount(randomUuid));
        Assertions.assertEquals(0.0, VelocityTicketManager.getPlayerSpeed(randomUuid), 0.001);
        Assertions.assertEquals(0.0, VelocityTicketManager.getPlayerVerticalDelta(randomUuid), 0.001);
        Assertions.assertEquals(0.0f, VelocityTicketManager.getPlayerPitch(randomUuid), 0.001f);
        Assertions.assertEquals(0, VelocityTicketManager.getPlayerQuota(randomUuid));
        Assertions.assertEquals(0, VelocityTicketManager.getPlayerDynamicReach(randomUuid));
        Assertions.assertEquals(100, VelocityTicketManager.getEffectiveDimensionClamp(null));
        Assertions.assertEquals(0, VelocityTicketManager.getTotalServerTickets());
        Assertions.assertEquals(0, VelocityTicketManager.getActiveFlyerCount());
    }

    @Test
    @DisplayName("End-to-end post-budget dimension reach scaling math matches specification")
    void testDimensionReachEndToEndCalculation() {
        // Base reach = 16 chunks (e.g., high speed flight in Overworld)
        int baseReach = 16;
        Assertions.assertEquals(10, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(baseReach, 60));
        Assertions.assertEquals(13, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(baseReach, 80));
        Assertions.assertEquals(16, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(baseReach, 100));
        Assertions.assertEquals(2, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(baseReach, 10));

        // Constrained quota reach = 8 chunks (e.g., multiplayer load shedding)
        int constrainedReach = 8;
        Assertions.assertEquals(5, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(constrainedReach, 60));
        Assertions.assertEquals(6, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(constrainedReach, 80));
        Assertions.assertEquals(8, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(constrainedReach, 100));

        // Safety floor protection on small base reach
        Assertions.assertEquals(2, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(2, 60));
        Assertions.assertEquals(0, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(0, 60));

        // Uncapped dimension reach (> 100%) and selective mute (0%)
        Assertions.assertEquals(24, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(16, 150));
        Assertions.assertEquals(0, net.vanillaoutsider.velocityrender.math.DimensionReachScaler.calculateClampedReach(16, 0));
    }
}
