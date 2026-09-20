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
    private static final net.vanillaoutsider.velocityrender.math.TurnRateCalculator TURN_CALCULATOR = new net.vanillaoutsider.velocityrender.math.TurnRateCalculator();
    private static boolean enabled = true;
    private static double leadMultiplier = 1.0;
    private static double minSpeedThreshold = 0.20; // 0.20 b/t = 4.0 m/s
    private static boolean debugMode = false;
    private static boolean clientF3DebugEnabled = true;

    // Precomputed volatile hot-path cache for lock-free render thread polling
    public static volatile boolean activeBias = false;
    public static volatile double cachedLeadOffset = 0.0;
    public static volatile double cachedNormDx = 0.0;
    public static volatile double cachedNormDy = 0.0;
    public static volatile double cachedNormDz = 0.0;
    private static volatile String cachedF3Line = null;
    private static int f3ThrottleTicks = 0;

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

        // Update client-side turn rate and cone angle
        TURN_CALCULATOR.update(cameraEntity.getYRot(), cameraEntity.tickCount);

        // 10-tick throttled F3 diagnostic string cache update (0B render allocation)
        f3ThrottleTicks++;
        if (f3ThrottleTicks >= 10) {
            f3ThrottleTicks = 0;
            updateCachedF3Line(client);
        }

        if (debugMode && client.player != null && (client.player.tickCount % 40 == 0)) {
            LOGGER.info("[VelocityRender-Client] Speed: {:.2f} b/t ({:.1f} m/s), Bias: {}, Lead: {:.1f}",
                    getSpeedBlocksPerTick(), getSpeedMetersPerSecond(),
                    activeBias, cachedLeadOffset);
        }
    }

    private static void updateCachedF3Line(Minecraft client) {
        if (!isF3DebugEnabled(client)) {
            cachedF3Line = null;
            return;
        }

        int coneDeg = Math.min(90, Math.round(TURN_CALCULATOR.getAngularRate() * 10.0f));

        if (client.getSingleplayerServer() != null && client.player != null) {
            // Integrated singleplayer server context: full server telemetry
            java.util.UUID uuid = client.player.getUUID();
            int activeTickets = net.vanillaoutsider.velocityrender.server.VelocityTicketManager.getActiveTicketCount(uuid);
            
            // Cleanliness guard: stay hidden during idle standing/walking
            if (!activeBias && activeTickets == 0) {
                cachedF3Line = null;
                return;
            }

            float mspt = net.vanillaoutsider.velocityrender.server.VelocityTicketManager.getLastServerMspt();
            double shedPct = mspt > 25.0f ? Math.min(80.0, ((mspt - 25.0) / 25.0) * 100.0) : 0.0;
            int serverTickets = net.vanillaoutsider.velocityrender.server.VelocityTicketManager.getTotalServerTickets();
            int serverBudget = client.level != null ? net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules.getServerTicketBudget(client.level) : 64;

            cachedF3Line = DebugMetricsFormatter.formatIntegrated(activeTickets, shedPct, coneDeg, serverTickets, serverBudget);
        } else {
            // Dedicated multiplayer client context: client-side meshing metrics
            if (!activeBias) {
                cachedF3Line = null;
                return;
            }
            cachedF3Line = DebugMetricsFormatter.formatClientOnly(activeBias, cachedLeadOffset, coneDeg);
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

    public static String getCachedF3Line() {
        return cachedF3Line;
    }

    public static boolean isF3DebugEnabled() {
        Minecraft mc = Minecraft.getInstance();
        return isF3DebugEnabled(mc);
    }

    public static boolean isF3DebugEnabled(Minecraft mc) {
        if (!clientF3DebugEnabled) {
            return false;
        }
        if (mc != null && mc.level != null) {
            return net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules.isF3DebugEnabled(mc.level);
        }
        return true;
    }

    public static void setF3DebugEnabled(boolean value) {
        clientF3DebugEnabled = value;
        if (!value) {
            cachedF3Line = null;
        }
    }
}
