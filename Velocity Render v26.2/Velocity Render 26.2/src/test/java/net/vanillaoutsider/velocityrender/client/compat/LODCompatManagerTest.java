// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.compat;

import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LODCompatManager & VelocityTrajectoryAPI Tests")
class LODCompatManagerTest {

    @BeforeEach
    void setUp() {
        LODCompatManager.resetForTests(false, false);
    }

    @Test
    @DisplayName("Default state is inactive with zero trajectory bias")
    void testDefaultInactiveState() {
        assertFalse(VelocityTrajectoryAPI.isTrajectoryActive());
        assertEquals(LODTrajectoryState.INACTIVE, VelocityTrajectoryAPI.getActiveTrajectory());

        double[] focus = VelocityTrajectoryAPI.getLookaheadFocus();
        assertEquals(0.0, focus[0]);
        assertEquals(0.0, focus[1]);
        assertEquals(0.0, focus[2]);

        assertEquals(Double.MAX_VALUE, VelocityTrajectoryAPI.calculateBiasedDistanceSqr(100.0, 50.0, 100.0));
        assertEquals("None (Vanilla Meshing)", VelocityTrajectoryAPI.getIntegrationSummary());
    }

    @Test
    @DisplayName("Safe initialization without third-party mods present")
    void testInitWithoutThirdPartyMods() {
        assertDoesNotThrow(LODCompatManager::init);
        assertFalse(LODCompatManager.isDistantHorizonsPresent());
        assertFalse(LODCompatManager.isBobbyPresent());
        assertFalse(LODCompatManager.isDistantHorizonsHooked());
        assertFalse(LODCompatManager.isBobbyHooked());
    }

    @Test
    @DisplayName("Updating trajectory propagates cleanly to VelocityTrajectoryAPI queries")
    void testUpdateTrajectoryAndAPIQuery() {
        var activeState = LODTrajectoryCalculator.createState(
                100.0, 64.0, 100.0,
                0.0, 0.0, 1.0, // Moving North (+Z)
                2.0,           // 2.0 b/t
                1.0,           // 100% lead
                0.20
        );

        LODCompatManager.updateTrajectory(activeState);

        assertTrue(VelocityTrajectoryAPI.isTrajectoryActive());
        assertEquals(activeState, VelocityTrajectoryAPI.getActiveTrajectory());

        double[] focus = VelocityTrajectoryAPI.getLookaheadFocus();
        assertEquals(100.0, focus[0]);
        assertEquals(64.0, focus[1]);
        assertEquals(100.0 + 512.0, focus[2], 1e-6); // 2.0 * 256.0 = 512.0

        // Forward chunk (Z = 200) vs rear chunk (Z = 0)
        double frontDist = VelocityTrajectoryAPI.calculateBiasedDistanceSqr(100.0, 64.0, 200.0);
        double rearDist = VelocityTrajectoryAPI.calculateBiasedDistanceSqr(100.0, 64.0, 0.0);
        assertTrue(frontDist < rearDist, "Forward chunk must be prioritized over rear chunk");
    }

    @Test
    @DisplayName("Null trajectory update gracefully resets to inactive state")
    void testNullSafetyOnUpdate() {
        var state = LODTrajectoryCalculator.createState(
                0.0, 0.0, 0.0,
                1.0, 0.0, 0.0,
                1.0, 1.0, 0.20
        );
        LODCompatManager.updateTrajectory(state);
        assertTrue(VelocityTrajectoryAPI.isTrajectoryActive());

        LODCompatManager.updateTrajectory(null);
        assertFalse(VelocityTrajectoryAPI.isTrajectoryActive());
        assertEquals(LODTrajectoryState.INACTIVE, VelocityTrajectoryAPI.getActiveTrajectory());
    }

    @Test
    @DisplayName("Mocked mod presence produces accurate integration summaries")
    void testMockedModPresenceIntegrationSummary() {
        LODCompatManager.resetForTests(true, false);
        assertTrue(LODCompatManager.isDistantHorizonsPresent());
        assertFalse(LODCompatManager.isBobbyPresent());
        assertTrue(VelocityTrajectoryAPI.getIntegrationSummary().contains("Distant Horizons"));

        LODCompatManager.resetForTests(false, true);
        assertFalse(LODCompatManager.isDistantHorizonsPresent());
        assertTrue(LODCompatManager.isBobbyPresent());
        assertTrue(VelocityTrajectoryAPI.getIntegrationSummary().contains("Bobby"));

        LODCompatManager.resetForTests(true, true);
        String bothSummary = VelocityTrajectoryAPI.getIntegrationSummary();
        assertTrue(bothSummary.contains("Distant Horizons"));
        assertTrue(bothSummary.contains("Bobby"));
    }
}
