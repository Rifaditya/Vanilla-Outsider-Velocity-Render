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
        Assertions.assertEquals(0, VelocityTicketManager.getTotalServerTickets());
        Assertions.assertEquals(0, VelocityTicketManager.getActiveFlyerCount());
    }
}
