// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DimensionReachScalerTest {

    @Test
    @DisplayName("Nether standard clamp (60%) should scale 16 chunks to 10 chunks")
    void testNetherStandardClamp() {
        int scaled = DimensionReachScaler.calculateClampedReach(16, 60);
        Assertions.assertEquals(10, scaled, "16 chunks at 60% should round to 10 chunks");

        int scaledModerate = DimensionReachScaler.calculateClampedReach(10, 60);
        Assertions.assertEquals(6, scaledModerate, "10 chunks at 60% should round to 6 chunks");
    }

    @Test
    @DisplayName("Dense dimension clamp (80%) should scale 16 chunks to 13 chunks")
    void testDenseDimensionClamp() {
        int scaled = DimensionReachScaler.calculateClampedReach(16, 80);
        Assertions.assertEquals(13, scaled, "16 chunks at 80% should round to 13 chunks");
    }

    @Test
    @DisplayName("Overworld 100% clamp should preserve original base reach")
    void testOverworldFullReach() {
        int scaled = DimensionReachScaler.calculateClampedReach(16, 100);
        Assertions.assertEquals(16, scaled, "100% clamp must preserve 16 chunks");

        int scaledSmall = DimensionReachScaler.calculateClampedReach(4, 100);
        Assertions.assertEquals(4, scaledSmall, "100% clamp must preserve 4 chunks");
    }

    @Test
    @DisplayName("Zero or negative base reach should safely return 0")
    void testZeroOrNegativeBaseReach() {
        Assertions.assertEquals(0, DimensionReachScaler.calculateClampedReach(0, 60));
        Assertions.assertEquals(0, DimensionReachScaler.calculateClampedReach(-5, 60));
    }

    @Test
    @DisplayName("Safety floor should ensure at least 2 chunks corridor when base reach >= 2")
    void testSafetyFloor() {
        // Extreme low clamp of 10% on 4 chunks: round(0.4) = 0, floor guarantees 2
        int scaled = DimensionReachScaler.calculateClampedReach(4, 10);
        Assertions.assertEquals(2, scaled, "Corridor must not collapse below 2 chunks");

        // Base reach 2 at 10% clamp: round(0.2) = 0 -> 2
        int scaledMin = DimensionReachScaler.calculateClampedReach(2, 10);
        Assertions.assertEquals(2, scaledMin);
    }

    @Test
    @DisplayName("Clamp percentage below 10% should be clamped to 10%")
    void testUnderflowClampPercentage() {
        // Clamp of 0% or negative should be treated as 10%
        int scaledZero = DimensionReachScaler.calculateClampedReach(16, 0);
        Assertions.assertEquals(2, scaledZero, "0% clamp clamped to 10%: round(1.6) -> 2");

        int scaledNegative = DimensionReachScaler.calculateClampedReach(16, -50);
        Assertions.assertEquals(2, scaledNegative, "Negative clamp clamped to 10%: round(1.6) -> 2");
    }

    @Test
    @DisplayName("Clamp percentage exceeding 100% should be clamped to 100%")
    void testOverflowClampPercentage() {
        int scaled = DimensionReachScaler.calculateClampedReach(16, 150);
        Assertions.assertEquals(16, scaled, "Clamp > 100% should clamp to 100%");
    }
}
