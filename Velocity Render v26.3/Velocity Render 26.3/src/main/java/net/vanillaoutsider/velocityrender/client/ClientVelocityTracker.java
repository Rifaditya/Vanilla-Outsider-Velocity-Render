// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.velocityrender.math.VelocityCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientVelocityTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientVelocityTracker.class);

    private static final VelocityCalculator CALCULATOR = new VelocityCalculator();
    private static boolean enabled = true;
    private static double leadMultiplier = 1.0;
    private static double minSpeedThreshold = 0.20; // 0.20 b/t = 4.0 m/s
    private static boolean debugMode = false;

    // Precomputed volatile hot-path cache for lock-free render thread polling
    public static volatile boolean activeBias = false;
    public static volatile double cachedLeadOffset = 0.0;
    public static volatile double cachedNormDx = 0.0;
    public static volatile double cachedNormDy = 0.0;
    public static volatile double cachedNormDz = 0.0;

    private ClientVelocityTracker() {
    }

    public static void clientTick(Minecraft client) {
        if (client == null) {
            return;
        }

        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || cameraEntity.level() == null) {
            CALCULATOR.reset();
            activeBias = false;
            cachedLeadOffset = 0.0;
            return;
        }

        CALCULATOR.update(
                cameraEntity.getX(),
                cameraEntity.getY(),
                cameraEntity.getZ(),
                cameraEntity.getDeltaMovement().x,
                cameraEntity.getDeltaMovement().y,
                cameraEntity.getDeltaMovement().z
        );

        double speed = CALCULATOR.getSpeedBlocksPerTick();
        if (enabled && speed >= minSpeedThreshold) {
            activeBias = true;
            cachedNormDx = CALCULATOR.getNormDx();
            cachedNormDy = CALCULATOR.getNormDy();
            cachedNormDz = CALCULATOR.getNormDz();
            cachedLeadOffset = Math.min(256.0, speed * 16.0 * leadMultiplier);
        } else {
            activeBias = false;
            cachedLeadOffset = 0.0;
        }

        if (debugMode && client.player != null && (client.player.tickCount % 40 == 0)) {
            LOGGER.info("[VelocityRender-Client] Speed: {:.2f} b/t ({:.1f} m/s), Bias: {}, Lead: {:.1f}",
                    getSpeedBlocksPerTick(), getSpeedMetersPerSecond(),
                    activeBias, cachedLeadOffset);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static double getLeadMultiplier() {
        return leadMultiplier;
    }

    public static void setLeadMultiplier(double value) {
        leadMultiplier = Math.max(0.0, Math.min(3.0, value));
    }

    public static double getMinSpeedThreshold() {
        return minSpeedThreshold;
    }

    public static void setMinSpeedThreshold(double value) {
        minSpeedThreshold = Math.max(0.01, Math.min(2.0, value));
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean value) {
        debugMode = value;
    }

    public static double getSpeedBlocksPerTick() {
        return CALCULATOR.getSpeedBlocksPerTick();
    }

    public static double getSpeedMetersPerSecond() {
        return CALCULATOR.getSpeedMetersPerSecond();
    }

    public static double getNormDx() {
        return CALCULATOR.getNormDx();
    }

    public static double getNormDy() {
        return CALCULATOR.getNormDy();
    }

    public static double getNormDz() {
        return CALCULATOR.getNormDz();
    }
}
