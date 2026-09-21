// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LODTrajectoryCalculator Tests")
class LODTrajectoryCalculatorTest {

    @Test
    @DisplayName("Stationary camera defaults to inactive state with zero lead")
    void testStationaryCameraDefaultsToInactiveAndZeroLead() {
        var state = LODTrajectoryCalculator.createState(
                100.0, 70.0, -200.0,
                0.0, 0.0, 0.0,
                0.0,
                1.0,
                0.20
        );

        assertFalse(state.active());
        assertEquals(100.0, state.camX());
        assertEquals(70.0, state.camY());
        assertEquals(-200.0, state.camZ());
        assertEquals(100.0, state.focusX());
        assertEquals(70.0, state.focusY());
        assertEquals(-200.0, state.focusZ());
        assertEquals(0.0, state.leadDistance());
        assertEquals(0.0, state.speedBlocksPerTick());
        assertEquals(0.0, state.speedMetersPerSecond());
    }

    @Test
    @DisplayName("Sub-threshold speed safely falls back to inactive state")
    void testSubThresholdSpeedFallback() {
        var state = LODTrajectoryCalculator.createState(
                0.0, 100.0, 0.0,
                1.0, 0.0, 0.0,
                0.15, // Below 0.20
                1.0,
                0.20
        );

        assertFalse(state.active());
        assertEquals(0.0, state.leadDistance());
        assertEquals(0.0, state.focusX());
        assertEquals(100.0, state.focusY());
        assertEquals(0.0, state.focusZ());
    }

    @Test
    @DisplayName("High-speed lead distance scales linearly and clamps at 1024-block ceiling")
    void testHighSpeedLeadDistanceCalculationAndCeiling() {
        // 1.5 b/t * 256.0 * 1.0 = 384.0 blocks
        double normalFlightLead = LODTrajectoryCalculator.calculateLeadOffset(1.5, 1.0);
        assertEquals(384.0, normalFlightLead, 1e-6);

        // 2.0 b/t * 256.0 * 1.0 = 512.0 blocks
        double fastFlightLead = LODTrajectoryCalculator.calculateLeadOffset(2.0, 1.0);
        assertEquals(512.0, fastFlightLead, 1e-6);

        // Extreme supersonic speed: 6.0 b/t -> 1536.0, clamped at 1024.0
        double supersonicLead = LODTrajectoryCalculator.calculateLeadOffset(6.0, 1.0);
        assertEquals(1024.0, supersonicLead, 1e-6);
    }

    @Test
    @DisplayName("3D lookahead focus point correctly extrapolates along travel vector")
    void test3DFocusPointExtrapolation() {
        // Flying East (+X) at 1.0 b/t with leadMultiplier = 1.0 -> lead = 256.0 blocks
        var state = LODTrajectoryCalculator.createState(
                100.0, 64.0, 50.0,
                1.0, 0.0, 0.0,
                1.0,
                1.0,
                0.20
        );

        assertTrue(state.active());
        assertEquals(256.0, state.leadDistance(), 1e-6);
        assertEquals(356.0, state.focusX(), 1e-6);
        assertEquals(64.0, state.focusY(), 1e-6);
        assertEquals(50.0, state.focusZ(), 1e-6);
        assertEquals(20.0, state.speedMetersPerSecond(), 1e-6);
    }

    @Test
    @DisplayName("Directional LOD biased distance prioritizes forward chunks over rear chunks")
    void testDirectionalLODBiasedDistanceOrdering() {
        double camX = 0.0, camY = 100.0, camZ = 0.0;
        double dirX = 0.0, dirY = 0.0, dirZ = 1.0; // Moving North (+Z)
        double lead = 500.0;

        // Front chunk: 200 blocks ahead along +Z
        double frontBiased = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                0.0, 100.0, 200.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                lead
        );

        // Rear chunk: 200 blocks behind along -Z
        double rearBiased = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                0.0, 100.0, -200.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                lead
        );

        // Euclidean distance is identical (40,000)
        // Biased distance for front must be strictly less than rear
        assertTrue(frontBiased < rearBiased, "Front chunk should have lower biased distance than rear chunk");
        assertEquals(0.0, frontBiased, 1e-6); // Clamped at 0.0 because 40000 - 2 * 200 * 500 = -160,000 -> 0.0
        assertEquals(240000.0, rearBiased, 1e-6); // 40000 - 2 * (-200) * 500 = 240,000
    }

    @Test
    @DisplayName("Vertical pitch dives and ascents properly bias Y coordinates")
    void testVerticalPitchDivesAndAscents() {
        // Vertical nose-dive: dir = (0, -1, 0) at 1.5 b/t -> lead = 384.0 blocks
        var diveState = LODTrajectoryCalculator.createState(
                0.0, 320.0, 0.0,
                0.0, -1.0, 0.0,
                1.5,
                1.0,
                0.20
        );

        assertTrue(diveState.active());
        assertEquals(-64.0, diveState.focusY(), 1e-6); // 320 - 384 = -64
        assertEquals(0.0, diveState.focusX(), 1e-6);
        assertEquals(0.0, diveState.focusZ(), 1e-6);

        // Target at bedrock (Y = -60) should have lower distance than target at sky (Y = 320)
        double bedrockDist = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                0.0, -60.0, 0.0,
                0.0, 320.0, 0.0,
                0.0, -1.0, 0.0,
                384.0
        );
        double skyDist = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                0.0, 500.0, 0.0,
                0.0, 320.0, 0.0,
                0.0, -1.0, 0.0,
                384.0
        );

        assertTrue(bedrockDist < skyDist);
    }

    @Test
    @DisplayName("Negative multipliers and non-positive speeds safely return zero lead")
    void testNegativeLeadMultiplierAndEdgeSafety() {
        assertEquals(0.0, LODTrajectoryCalculator.calculateLeadOffset(-1.0, 1.0));
        assertEquals(0.0, LODTrajectoryCalculator.calculateLeadOffset(1.0, -1.0));
        assertEquals(0.0, LODTrajectoryCalculator.calculateLeadOffset(0.0, 1.0));

        var state = LODTrajectoryCalculator.createState(
                10.0, 20.0, 30.0,
                1.0, 0.0, 0.0,
                1.0,
                -0.5,
                0.20
        );
        assertFalse(state.active());
        assertEquals(0.0, state.leadDistance());
    }

    @Test
    @DisplayName("NaN and Infinite floating point inputs are defensively sanitized")
    void testNaNAndInfinityDefense() {
        assertEquals(0.0, LODTrajectoryCalculator.calculateLeadOffset(Double.NaN, 1.0));
        assertEquals(0.0, LODTrajectoryCalculator.calculateLeadOffset(1.0, Double.POSITIVE_INFINITY));

        double[] focus = LODTrajectoryCalculator.calculateLookaheadFocus(
                10.0, 20.0, 30.0,
                Double.NaN, 0.0, 0.0,
                Double.POSITIVE_INFINITY
        );
        assertEquals(10.0, focus[0]);
        assertEquals(20.0, focus[1]);
        assertEquals(30.0, focus[2]);

        double biased = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                Double.NaN, 0.0, 0.0,
                0.0, 0.0, 0.0,
                1.0, 0.0, 0.0,
                100.0
        );
        assertEquals(Double.MAX_VALUE, biased);
    }

    @Test
    @DisplayName("Chunk coordinate conversion helpers return accurate chunk indices")
    void testChunkCoordinatesHelper() {
        var state = LODTrajectoryCalculator.createState(
                32.5, 64.0, -48.2,
                1.0, 0.0, 0.0,
                1.0,
                1.0,
                0.20
        );

        // Cam chunk: 32 >> 4 = 2, -48 >> 4 = -3
        assertEquals(2, state.camChunkX());
        assertEquals(-4, state.camChunkZ()); // -48.2 floor is -49 >> 4 = -4

        // Focus chunk: (32.5 + 256.0) = 288.5 >> 4 = 18
        assertEquals(18, state.focusChunkX());
        assertEquals(-4, state.focusChunkZ());
    }
}
