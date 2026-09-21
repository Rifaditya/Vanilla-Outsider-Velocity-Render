// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

/**
 * Pure mathematical utility for tracking a player's angular yaw velocity (omega)
 * and calculating directional arc fan-out widths for banked turns.
 * <p>
 * Uses an Exponential Moving Average (EMA, alpha = 0.60) to balance instantaneous
 * mouse-look responsiveness with jitter filtering.
 */
public final class TurnRateCalculator {

    public static final float EMA_ALPHA = 0.60f;
    public static final float MIN_TURN_THRESHOLD_DEG_PER_TICK = 4.0f;
    public static final float SHARP_TURN_THRESHOLD_DEG_PER_TICK = 8.0f;
    public static final double MIN_SPEED_BLOCKS_PER_TICK = 0.50;

    private float lastYaw = 0.0f;
    private int lastTick = -1;
    private float smoothedAngularRate = 0.0f;
    private int turnSign = 0; // -1 = Left (CCW), +1 = Right (CW), 0 = Straight
    private boolean initialized = false;

    public void update(float currentYaw, int tick) {
        if (!initialized) {
            lastYaw = currentYaw;
            lastTick = tick;
            smoothedAngularRate = 0.0f;
            turnSign = 0;
            initialized = true;
            return;
        }

        int deltaTicks = tick - lastTick;
        if (deltaTicks <= 0 || Float.isNaN(currentYaw) || Float.isInfinite(currentYaw)) {
            // Same tick, clock anomaly, or invalid floating-point value: preserve existing rate
            return;
        }

        // Calculate shortest angular delta across [-180.0, 180.0] wrap-around in O(1)
        float rawDeltaYaw = currentYaw - lastYaw;
        if (Float.isNaN(rawDeltaYaw) || Float.isInfinite(rawDeltaYaw)) {
            return;
        }
        rawDeltaYaw = ((rawDeltaYaw + 180.0f) % 360.0f + 360.0f) % 360.0f - 180.0f;

        float instantaneousRate = Math.abs(rawDeltaYaw) / (float) deltaTicks;

        // Detect turn direction: positive delta = CW (right), negative delta = CCW (left)
        if (instantaneousRate >= 0.5f) {
            turnSign = rawDeltaYaw > 0.0f ? 1 : -1;
        } else {
            turnSign = 0;
        }

        // Exponential Moving Average smoothing
        smoothedAngularRate = (EMA_ALPHA * instantaneousRate) + ((1.0f - EMA_ALPHA) * smoothedAngularRate);

        lastYaw = currentYaw;
        lastTick = tick;
    }

    public float getAngularRate() {
        return smoothedAngularRate;
    }

    public int getTurnSign() {
        return turnSign;
    }

    public boolean isTurningActive(double speed) {
        return speed >= MIN_SPEED_BLOCKS_PER_TICK && smoothedAngularRate >= MIN_TURN_THRESHOLD_DEG_PER_TICK;
    }

    /**
     * Computes the lateral chunk fan-out depth along the inner curve of the turn.
     *
     * @param speed current horizontal travel speed in blocks per tick
     * @return 2 for sharp turns, 1 for moderate turns, 0 if turning is not active
     */
    public int getInnerFanOutWidth(double speed) {
        if (!isTurningActive(speed)) {
            return 0;
        }
        return smoothedAngularRate > SHARP_TURN_THRESHOLD_DEG_PER_TICK ? 2 : 1;
    }

    /**
     * Computes the lateral chunk safety tangent along the outer curve of the turn.
     *
     * @param speed current horizontal travel speed in blocks per tick
     * @return 1 if turning is active, 0 otherwise
     */
    public int getOuterFanOutWidth(double speed) {
        return isTurningActive(speed) ? 1 : 0;
    }

    public void reset() {
        lastYaw = 0.0f;
        lastTick = -1;
        smoothedAngularRate = 0.0f;
        turnSign = 0;
        initialized = false;
    }
}
