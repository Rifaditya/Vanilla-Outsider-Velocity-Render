// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.vanillaoutsider.velocityrender.client.compat.adapter.BobbyAdapter;
import net.vanillaoutsider.velocityrender.client.compat.adapter.DistantHorizonsAdapter;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central compatibility coordinator for Level-of-Detail (LOD) and chunk-caching mods.
 * Manages runtime mod detection, isolated adapter lifecycle, and lookahead state distribution.
 */
public final class LODCompatManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LODCompatManager.class);

    private static boolean initialized = false;
    private static boolean dhPresent = false;
    private static boolean bobbyPresent = false;

    private static DistantHorizonsAdapter dhAdapter = null;
    private static BobbyAdapter bobbyAdapter = null;

    private static volatile LODTrajectoryState currentTrajectory = LODTrajectoryState.INACTIVE;

    private LODCompatManager() {
    }

    /**
     * Initializes mod detection and lazily loads adapter instances.
     * Safe to call in headless test environments or when FabricLoader is unavailable.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }

        try {
            FabricLoader loader = FabricLoader.getInstance();
            if (loader != null) {
                dhPresent = loader.isModLoaded("distanthorizons");
                bobbyPresent = loader.isModLoaded("bobby");
            }
        } catch (Throwable t) {
            // Headless / non-Fabric test environment safety
            dhPresent = false;
            bobbyPresent = false;
        }

        if (dhPresent) {
            try {
                dhAdapter = new DistantHorizonsAdapter();
            } catch (Throwable t) {
                LOGGER.warn("[VelocityRender-LOD] Failed to create Distant Horizons adapter: {}", t.getMessage());
            }
        }

        if (bobbyPresent) {
            try {
                bobbyAdapter = new BobbyAdapter();
            } catch (Throwable t) {
                LOGGER.warn("[VelocityRender-LOD] Failed to create Bobby adapter: {}", t.getMessage());
            }
        }

        initialized = true;
        LOGGER.info("[VelocityRender-LOD] Initialized LOD Compatibility Hub. Summary: {}", getIntegrationSummary());
    }

    /**
     * Updates current trajectory snapshot and broadcasts to active adapters.
     * Lock-free, zero heap allocation.
     */
    public static void updateTrajectory(LODTrajectoryState state) {
        currentTrajectory = (state != null) ? state : LODTrajectoryState.INACTIVE;

        if (dhAdapter != null) {
            dhAdapter.updateTrajectory(currentTrajectory);
        }
        if (bobbyAdapter != null) {
            bobbyAdapter.updateTrajectory(currentTrajectory);
        }
    }

    public static LODTrajectoryState getActiveTrajectory() {
        return currentTrajectory;
    }

    public static boolean isTrajectoryActive() {
        return currentTrajectory != null && currentTrajectory.active();
    }

    public static double[] getLookaheadFocus() {
        LODTrajectoryState state = currentTrajectory;
        if (state == null || !state.active()) {
            return new double[]{0.0, 0.0, 0.0};
        }
        return new double[]{state.focusX(), state.focusY(), state.focusZ()};
    }

    public static double calculateBiasedDistanceSqr(double targetX, double targetY, double targetZ) {
        LODTrajectoryState state = currentTrajectory;
        if (state == null || !state.active()) {
            return Double.MAX_VALUE;
        }
        return LODTrajectoryCalculator.calculateBiasedLODDistanceSqr(
                targetX, targetY, targetZ,
                state.camX(), state.camY(), state.camZ(),
                state.dirX(), state.dirY(), state.dirZ(),
                state.leadDistance()
        );
    }

    public static boolean isDistantHorizonsPresent() {
        return dhPresent;
    }

    public static boolean isBobbyPresent() {
        return bobbyPresent;
    }

    public static boolean isDistantHorizonsHooked() {
        return dhAdapter != null && dhAdapter.isHooked();
    }

    public static boolean isBobbyHooked() {
        return bobbyAdapter != null && bobbyAdapter.isHooked();
    }

    public static String getIntegrationSummary() {
        if (!dhPresent && !bobbyPresent) {
            return "None (Vanilla Meshing)";
        }
        StringBuilder sb = new StringBuilder();
        if (dhPresent) {
            sb.append("Distant Horizons [").append(isDistantHorizonsHooked() ? "ACTIVE" : "DETECTED").append("]");
        }
        if (bobbyPresent) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Bobby [").append(isBobbyHooked() ? "ACTIVE" : "DETECTED").append("]");
        }
        return sb.toString();
    }

    /**
     * Testing hook to manually simulate mod presence and reset state.
     */
    public static synchronized void resetForTests(boolean mockDH, boolean mockBobby) {
        initialized = true;
        dhPresent = mockDH;
        bobbyPresent = mockBobby;
        dhAdapter = mockDH ? new DistantHorizonsAdapter() : null;
        bobbyAdapter = mockBobby ? new BobbyAdapter() : null;
        currentTrajectory = LODTrajectoryState.INACTIVE;
    }
}
