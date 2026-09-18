// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VelocityVectorHelperTest {

    @Test
    @DisplayName("Should prioritize forward chunks over rear chunks at identical Euclidean distance")
    void testForwardChunkPrioritization() {
        double camX = 0.0, camY = 64.0, camZ = 0.0;
        double dirX = 0.0, dirY = 0.0, dirZ = 1.0; // Moving South along +Z
        double speed = 1.5; // High speed (e.g. Elytra)
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        // Front chunk: +100 blocks ahead
        double frontBiased = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, 100.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Rear chunk: -100 blocks behind
        double rearBiased = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, -100.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Forward chunk must have significantly lower biased distance value (higher meshing priority)
        Assertions.assertTrue(frontBiased < rearBiased, "Forward chunk must sort before rear chunk in priority queue");
        Assertions.assertTrue(frontBiased < 10000.0, "Forward chunk distance must be biased downward");
        Assertions.assertTrue(rearBiased > 10000.0, "Rear chunk distance must be biased upward");
    }

    @Test
    @DisplayName("Should fall back to pure Euclidean distance when speed is below threshold")
    void testStationaryFallback() {
        double camX = 0.0, camY = 64.0, camZ = 0.0;
        double dirX = 0.0, dirY = 0.0, dirZ = 1.0;
        double speed = 0.05; // Stationary or slow walk below 0.20 b/t
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        double frontDist = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, 50.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        double rearDist = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, -50.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertEquals(2500.0, frontDist, 0.001);
        Assertions.assertEquals(2500.0, rearDist, 0.001);
    }
}
