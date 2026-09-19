// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TurnRateCalculatorTest {

    private TurnRateCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TurnRateCalculator();
    }

    @Test
    @DisplayName("Straight flight should produce zero turn rate and zero fan-out width")
    void testStraightFlight() {
        calculator.update(0.0f, 10);
        calculator.update(0.0f, 11);
        calculator.update(0.0f, 12);

        Assertions.assertEquals(0.0f, calculator.getAngularRate(), 0.001f);
        Assertions.assertEquals(0, calculator.getTurnSign());
        Assertions.assertFalse(calculator.isTurningActive(1.5));
        Assertions.assertEquals(0, calculator.getInnerFanOutWidth(1.5));
        Assertions.assertEquals(0, calculator.getOuterFanOutWidth(1.5));
    }

    @Test
    @DisplayName("Clockwise turn should detect right turn sign (+1) and activate moderate fan-out")
    void testClockwiseTurn() {
        calculator.update(0.0f, 1);
        // Turn 6 degrees per tick (moderate turn)
        for (int t = 2; t <= 10; t++) {
            calculator.update((t - 1) * 6.0f, t);
        }

        Assertions.assertTrue(calculator.getAngularRate() >= 4.0f, "Should reach moderate turn rate");
        Assertions.assertEquals(1, calculator.getTurnSign(), "Clockwise turn must have positive sign (+1)");
        Assertions.assertTrue(calculator.isTurningActive(0.80), "Should be active at 0.80 b/t");
        Assertions.assertEquals(1, calculator.getInnerFanOutWidth(0.80), "Moderate turn must have 1-chunk inner offset");
        Assertions.assertEquals(1, calculator.getOuterFanOutWidth(0.80), "Active turn must have 1-chunk outer tangent");
    }

    @Test
    @DisplayName("Counter-clockwise turn should detect left turn sign (-1)")
    void testCounterClockwiseTurn() {
        calculator.update(100.0f, 1);
        for (int t = 2; t <= 10; t++) {
            calculator.update(100.0f - (t - 1) * 6.0f, t);
        }

        Assertions.assertTrue(calculator.getAngularRate() >= 4.0f);
        Assertions.assertEquals(-1, calculator.getTurnSign(), "Counter-clockwise turn must have negative sign (-1)");
    }

    @Test
    @DisplayName("Sharp turn exceeding 8 deg/tick should scale inner fan-out to 2 chunks")
    void testSharpTurnScaling() {
        calculator.update(0.0f, 1);
        // Sharp turn 12 degrees per tick
        for (int t = 2; t <= 10; t++) {
            calculator.update((t - 1) * 12.0f, t);
        }

        Assertions.assertTrue(calculator.getAngularRate() > 8.0f, "Must exceed sharp turn threshold");
        Assertions.assertTrue(calculator.isTurningActive(1.2));
        Assertions.assertEquals(2, calculator.getInnerFanOutWidth(1.2), "Sharp turn must scale to 2 chunks inner offset");
        Assertions.assertEquals(1, calculator.getOuterFanOutWidth(1.2), "Outer safety tangent remains 1 chunk");
    }

    @Test
    @DisplayName("360-degree wrap-around boundary crossing (355° -> 5°) should compute +10° delta, not -350°")
    void testWrapAroundBoundary() {
        calculator.update(355.0f, 1);
        calculator.update(5.0f, 2); // 10 degrees clockwise across boundary

        Assertions.assertEquals(1, calculator.getTurnSign(), "Must detect clockwise crossing across boundary");
        Assertions.assertTrue(calculator.getAngularRate() > 0.0f);
        // Instantaneous rate was 10.0, smoothed with alpha=0.60 should be 6.0
        Assertions.assertEquals(6.0f, calculator.getAngularRate(), 0.01f);
    }

    @Test
    @DisplayName("Low speed below 0.50 b/t should suppress fan-out even during sharp head turns")
    void testLowSpeedSuppression() {
        calculator.update(0.0f, 1);
        for (int t = 2; t <= 10; t++) {
            calculator.update((t - 1) * 10.0f, t);
        }

        Assertions.assertTrue(calculator.getAngularRate() > 4.0f);
        // Speed = 0.20 b/t (walking)
        Assertions.assertFalse(calculator.isTurningActive(0.20), "Low speed must not activate turn corridor widening");
        Assertions.assertEquals(0, calculator.getInnerFanOutWidth(0.20));
        Assertions.assertEquals(0, calculator.getOuterFanOutWidth(0.20));
    }

    @Test
    @DisplayName("Reset should clear all internal state cleanly")
    void testReset() {
        calculator.update(0.0f, 1);
        calculator.update(30.0f, 2);
        Assertions.assertTrue(calculator.getAngularRate() > 0.0f);

        calculator.reset();
        Assertions.assertEquals(0.0f, calculator.getAngularRate());
        Assertions.assertEquals(0, calculator.getTurnSign());
        Assertions.assertFalse(calculator.isTurningActive(1.0));
    }
}
