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

    @Test
    @DisplayName("Should prioritize lower Y sub-chunk sections during steep vertical dives")
    void testSteepVerticalDivePrioritization() {
        double camX = 0.0, camY = 128.0, camZ = 0.0;
        double dirX = 0.0, dirY = -1.0, dirZ = 0.0; // Diving straight down
        double speed = 1.5;
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        // Deep section: Y=32 (-96 blocks along dive vector)
        double diveBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 32.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Upper section: Y=224 (+96 blocks behind dive vector)
        double skyBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 224.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertTrue(diveBiased < skyBiased, "Lower section in dive path must sort with higher priority");
        Assertions.assertTrue(diveBiased < 9216.0, "Dive section distance must be biased downward");
        Assertions.assertTrue(skyBiased > 9216.0, "Sky section behind dive vector must be biased upward");
    }

    @Test
    @DisplayName("Should prioritize upper Y sub-chunk sections symmetrically during steep rocket ascents")
    void testSteepRocketAscentPrioritization() {
        double camX = 0.0, camY = 64.0, camZ = 0.0;
        double dirX = 0.0, dirY = 1.0, dirZ = 0.0; // Climbing straight up
        double speed = 1.5;
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        // High section: Y=160 (+96 blocks along ascent vector)
        double ascentBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 160.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Lower section: Y=-32 (-96 blocks behind ascent vector)
        double groundBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, -32.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertTrue(ascentBiased < groundBiased, "Upper section in climb path must sort with higher priority");
    }

    @Test
    @DisplayName("Should maintain symmetrical vertical distance when flight is purely horizontal")
    void testHorizontalFlightVerticalSymmetry() {
        double camX = 0.0, camY = 64.0, camZ = 0.0;
        double dirX = 0.0, dirY = 0.0, dirZ = 1.0; // Moving South along +Z
        double speed = 1.5;
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        double upperDist = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 114.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        double lowerDist = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 14.0, 0.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertEquals(upperDist, lowerDist, 0.001, "Vertical distance must remain symmetrical during horizontal travel");
    }

    @Test
    @DisplayName("Should prioritize forward and lower sections during angled 45-degree dive")
    void testAngled45DegreeDivePrioritization() {
        double camX = 0.0, camY = 128.0, camZ = 0.0;
        double dirX = 0.0, dirY = -0.7071, dirZ = 0.7071; // 45-degree dive South
        double speed = 1.5;
        double leadMultiplier = 1.0;
        double minSpeed = 0.20;

        // Target quadrant: ahead and down (+Z, -Y)
        double frontDownBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 64.0, 64.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Opposing quadrant: behind and up (-Z, +Y)
        double rearUpBiased = VelocityVectorHelper.computeRawPitchBiasedDistanceSqr(
                0.0, 192.0, -64.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertTrue(frontDownBiased < rearUpBiased, "45-degree dive must prioritize forward-down quadrant over opposing quadrant");
    }

    @Test
    @DisplayName("Elevated lead multiplier scales lead offset beyond 256 blocks and preserves forward priority")
    void testUncappedLeadOffsetScaling() {
        double camX = 0.0, camY = 64.0, camZ = 0.0;
        double dirX = 0.0, dirY = 0.0, dirZ = 1.0;
        double speed = 2.0; // 2.0 b/t
        double leadMultiplier = 10.0; // 1000% lead multiplier -> raw lead = 2.0 * 16.0 * 10.0 = 320.0 blocks
        double minSpeed = 0.20;

        // Front chunk at 400 blocks ahead
        double frontBiased = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, 400.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        // Rear chunk at 400 blocks behind
        double rearBiased = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, -400.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                speed, leadMultiplier, minSpeed
        );

        Assertions.assertEquals(0.0, frontBiased, "Deep forward chunk should clamp cleanly to 0.0 priority floor");
        Assertions.assertEquals(416000.0, rearBiased, 0.001, "Rear chunk should receive full 320-block penalty: 400^2 + 2*400*320 = 416000");
        Assertions.assertTrue(frontBiased < rearBiased);

        // NaN and Infinite safety guards
        double nanResult = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                0.0, 64.0, 100.0,
                camX, camY, camZ,
                dirX, dirY, dirZ,
                Double.NaN, 1.0, 0.20
        );
        Assertions.assertFalse(Double.isNaN(nanResult));
    }
}
