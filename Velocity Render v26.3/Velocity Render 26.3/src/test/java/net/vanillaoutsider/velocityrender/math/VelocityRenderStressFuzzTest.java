// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import java.util.Random;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pillar 1: Chaos Property & Mathematical Stress Fuzzing Engine.
 * Executes 10,000+ randomized fuzz iterations across mathematical, vector, and trajectory
 * components to prove 100% crash-immunity, boundary saturation, and monotonic distance ordering.
 */
class VelocityRenderStressFuzzTest {

    private static final int FUZZ_ITERATIONS = 10_000;
    private static final long RANDOM_SEED = 0xDA51C_2026L;

    @Test
    @DisplayName("Fuzz: 10,000 iterations of LODTrajectoryCalculator with extreme chaos and boundaries")
    void fuzzLODTrajectoryCalculatorStress() {
        Random rng = new Random(RANDOM_SEED);
        double[] specialNumbers = {
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                0.0, -0.0, Double.MIN_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE,
                0.0000001, -0.0000001, 1024.0, -1024.0, 10_000_000.0, -10_000_000.0
        };

        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            double speed;
            double multiplier;
            double camX, camY, camZ;
            double dirX, dirY, dirZ;

            if (i < specialNumbers.length) {
                speed = specialNumbers[i];
                multiplier = 1.0;
                camX = 0.0; camY = 64.0; camZ = 0.0;
                dirX = 1.0; dirY = 0.0; dirZ = 0.0;
            } else {
                // Randomly pick either extreme or continuous values
                speed = rng.nextBoolean() ? (rng.nextDouble() * 200.0 - 50.0) : (rng.nextDouble() * 20_000_000.0 - 10_000_000.0);
                multiplier = rng.nextDouble() * 10.0 - 2.0;
                camX = rng.nextDouble() * 60_000_000.0 - 30_000_000.0;
                camY = rng.nextDouble() * 1000.0 - 200.0;
                camZ = rng.nextDouble() * 60_000_000.0 - 30_000_000.0;
                dirX = rng.nextDouble() * 2.0 - 1.0;
                dirY = rng.nextDouble() * 2.0 - 1.0;
                dirZ = rng.nextDouble() * 2.0 - 1.0;
            }

            // Invariant 1: calculateLeadOffset must ALWAYS be finite and clamped within [0, 1024]
            double lead = LODTrajectoryCalculator.calculateLeadOffset(speed, multiplier);
            assertFalse(Double.isNaN(lead), "Lead offset must never be NaN at iteration " + i);
            assertFalse(Double.isInfinite(lead), "Lead offset must never be infinite at iteration " + i);
            assertTrue(lead >= 0.0 && lead <= 1024.0, "Lead offset out of physical bounds: " + lead);

            // Invariant 2: calculateLookaheadFocus must NEVER produce NaN when camera coords are finite
            double[] focus = LODTrajectoryCalculator.calculateLookaheadFocus(camX, camY, camZ, dirX, dirY, dirZ, lead);
            assertNotNull(focus);
            assertEquals(3, focus.length);
            if (Double.isFinite(camX) && Double.isFinite(camY) && Double.isFinite(camZ)) {
                assertFalse(Double.isNaN(focus[0]), "Focus X must not be NaN at iteration " + i);
                assertFalse(Double.isNaN(focus[1]), "Focus Y must not be NaN at iteration " + i);
                assertFalse(Double.isNaN(focus[2]), "Focus Z must not be NaN at iteration " + i);
            }

            // Invariant 3: createState must never return null
            LODTrajectoryState state = LODTrajectoryCalculator.createState(camX, camY, camZ, dirX, dirY, dirZ, speed, multiplier, LODTrajectoryCalculator.DEFAULT_MIN_SPEED_THRESHOLD);
            assertNotNull(state, "TrajectoryState must never be null at iteration " + i);

            // Invariant 4: Directional monotonicity invariant
            // Forward target chunk must receive lower or equal biased distance compared to identical rear target chunk
            if (Double.isFinite(camX) && Double.isFinite(camY) && Double.isFinite(camZ) && speed > 0.20 && multiplier > 0.0) {
                double targetDist = 200.0;
                // Normalized direction for test
                double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                if (len > 0.0001 && Double.isFinite(len)) {
                    double uX = dirX / len;
                    double uY = dirY / len;
                    double uZ = dirZ / len;

                    double frontX = camX + uX * targetDist;
                    double frontY = camY + uY * targetDist;
                    double frontZ = camZ + uZ * targetDist;

                    double backX = camX - uX * targetDist;
                    double backY = camY - uY * targetDist;
                    double backZ = camZ - uZ * targetDist;

                    double frontDistSqr = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(frontX, frontY, frontZ, camX, camY, camZ, uX, uY, uZ, lead);
                    double backDistSqr = LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(backX, backY, backZ, camX, camY, camZ, uX, uY, uZ, lead);

                    assertTrue(frontDistSqr <= backDistSqr,
                            String.format("Front target must have smaller/equal biased distance than rear target: front=%.2f, back=%.2f at iter %d", frontDistSqr, backDistSqr, i));
                    assertTrue(frontDistSqr >= 0.0, "Biased distance must be >= 0.0");
                }
            }
        }
    }

    @Test
    @DisplayName("Fuzz: 10,000 iterations of TurnRateCalculator with chaotic yaw jumps and anomalies")
    void fuzzTurnRateCalculatorStress() {
        Random rng = new Random(RANDOM_SEED);
        TurnRateCalculator calculator = new TurnRateCalculator();

        float[] specialYaws = {
                Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
                0.0f, -0.0f, 180.0f, -180.0f, 360.0f, -360.0f, 72000.0f, -72000.0f
        };

        int currentTick = 1;
        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            float yaw;
            if (i < specialYaws.length) {
                yaw = specialYaws[i];
            } else {
                yaw = (rng.nextFloat() * 200_000.0f) - 100_000.0f;
            }

            int tickAdvance = rng.nextInt(5); // 0, 1, 2, 3, 4
            currentTick += tickAdvance;

            calculator.update(yaw, currentTick);

            float rate = calculator.getAngularRate();
            int sign = calculator.getTurnSign();

            // Invariants: rate must be finite and >= 0; sign must be -1, 0, or 1
            assertFalse(Float.isNaN(rate), "Angular rate must never be NaN at iteration " + i);
            assertFalse(Float.isInfinite(rate), "Angular rate must never be infinite at iteration " + i);
            assertTrue(rate >= 0.0f, "Angular rate must be >= 0.0f");
            assertTrue(sign == -1 || sign == 0 || sign == 1, "Turn sign must be -1, 0, or 1; got: " + sign);

            int innerFan = calculator.getInnerFanOutWidth(1.0);
            int outerFan = calculator.getOuterFanOutWidth(1.0);
            assertTrue(innerFan >= 0 && innerFan <= 2, "Inner fan-out must be in [0, 2]");
            assertTrue(outerFan >= 0 && outerFan <= 1, "Outer fan-out must be in [0, 1]");
        }
    }

    @Test
    @DisplayName("Fuzz: 10,000 iterations of VelocityCalculator with teleportations, NaNs, and extreme deltas")
    void fuzzVelocityCalculatorStress() {
        Random rng = new Random(RANDOM_SEED);
        VelocityCalculator calculator = new VelocityCalculator();

        double currentX = 0.0;
        double currentY = 64.0;
        double currentZ = 0.0;

        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            double dx = rng.nextBoolean() ? (rng.nextDouble() * 20.0 - 10.0) : (rng.nextDouble() * 20_000_000.0 - 10_000_000.0);
            double dy = rng.nextDouble() * 10.0 - 5.0;
            double dz = rng.nextBoolean() ? (rng.nextDouble() * 20.0 - 10.0) : (rng.nextDouble() * 20_000_000.0 - 10_000_000.0);

            // Inject chaotic values occasionally
            if (i % 500 == 0) dx = Double.NaN;
            if (i % 500 == 1) dy = Double.POSITIVE_INFINITY;
            if (i % 500 == 2) dz = Double.NEGATIVE_INFINITY;

            currentX += (Double.isFinite(dx) ? dx : 0.0);
            currentY += (Double.isFinite(dy) ? dy : 0.0);
            currentZ += (Double.isFinite(dz) ? dz : 0.0);

            calculator.update(currentX, currentY, currentZ, dx, dy, dz);

            double speed = calculator.getSpeedBlocksPerTick();
            double normDx = calculator.getNormDx();
            double normDy = calculator.getNormDy();
            double normDz = calculator.getNormDz();

            // Invariants: speed is finite and >= 0, normalized components are finite
            assertFalse(Double.isNaN(speed), "Speed must never be NaN at iteration " + i);
            assertFalse(Double.isInfinite(speed), "Speed must never be infinite at iteration " + i);
            assertTrue(speed >= 0.0, "Speed must be non-negative");

            assertFalse(Double.isNaN(normDx), "NormDx must never be NaN at iteration " + i);
            assertFalse(Double.isNaN(normDy), "NormDy must never be NaN at iteration " + i);
            assertFalse(Double.isNaN(normDz), "NormDz must never be NaN at iteration " + i);

            if (speed > 0.0001) {
                double lenSqr = normDx * normDx + normDy * normDy + normDz * normDz;
                assertTrue(Math.abs(lenSqr - 1.0) < 0.001, "Normalized vector length must be ~1.0; was: " + lenSqr);
            }
        }
    }

    @Test
    @DisplayName("Fuzz: 10,000 iterations of DimensionReachScaler with extreme ranges")
    void fuzzDimensionReachScalerStress() {
        Random rng = new Random(RANDOM_SEED);

        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            int baseReach = rng.nextInt(2_000_000) - 500_000;
            int clampPct = rng.nextInt(2_000) - 1_000;

            int clamped = DimensionReachScaler.calculateClampedReach(baseReach, clampPct);

            if (baseReach <= 0) {
                assertEquals(0, clamped, "Reach <= 0 must always yield 0");
            } else {
                assertTrue(clamped >= DimensionReachScaler.MIN_REACH_FLOOR,
                        "Positive reach must satisfy floor of >= 2; got: " + clamped + " at iter " + i);
            }
        }
    }

    @Test
    @DisplayName("Fuzz: 10,000 iterations of VelocityVectorHelper raw biased distance")
    void fuzzVelocityVectorHelperStress() {
        Random rng = new Random(RANDOM_SEED);

        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            double originX = rng.nextDouble() * 2000.0 - 1000.0;
            double originY = rng.nextDouble() * 200.0;
            double originZ = rng.nextDouble() * 2000.0 - 1000.0;

            double camX = 0.0;
            double camY = 64.0;
            double camZ = 0.0;

            double dirX = 1.0;
            double dirY = 0.0;
            double dirZ = 0.0;

            double speed = rng.nextDouble() * 50.0;
            double multiplier = rng.nextDouble() * 5.0;
            double minSpeed = 0.20;

            double biasedDistSqr = VelocityVectorHelper.computeRawBiasedDistanceSqr(
                    originX, originY, originZ,
                    camX, camY, camZ,
                    dirX, dirY, dirZ,
                    speed, multiplier, minSpeed
            );

            assertFalse(Double.isNaN(biasedDistSqr), "Biased dist must not be NaN at iteration " + i);
            assertTrue(biasedDistSqr >= 0.0, "Biased dist sqr must be >= 0.0");
        }
    }
}
