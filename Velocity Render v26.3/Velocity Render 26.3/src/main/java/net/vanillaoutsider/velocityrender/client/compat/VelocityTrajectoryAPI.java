// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.compat;

import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;

/**
 * Public static API providing zero-reflection access to Velocity Render's
 * current lookahead trajectory for third-party Level-of-Detail (LOD) renderers,
 * chunk-caching mods, shaders, and external development tools.
 */
public final class VelocityTrajectoryAPI {

    private VelocityTrajectoryAPI() {
    }

    /**
     * Retrieves the current immutable snapshot of the player's LOD velocity trajectory.
     * Returns {@link LODTrajectoryState#INACTIVE} if the player is stationary or the mod is disabled.
     */
    public static LODTrajectoryState getActiveTrajectory() {
        return LODCompatManager.getActiveTrajectory();
    }

    /**
     * Checks whether directional lookahead bias is actively engaged.
     */
    public static boolean isTrajectoryActive() {
        return LODCompatManager.isTrajectoryActive();
    }

    /**
     * Returns the 3D projected lookahead focus coordinate [x, y, z].
     * If inactive, returns the camera position.
     */
    public static double[] getLookaheadFocus() {
        return LODCompatManager.getLookaheadFocus();
    }

    /**
     * Computes the anisotropic biased distance squared from the camera to a target coordinate,
     * applying current trajectory weighting. Forward chunks receive lower values (higher priority).
     */
    public static double calculateBiasedDistanceSqr(double targetX, double targetY, double targetZ) {
        return LODCompatManager.calculateBiasedDistanceSqr(targetX, targetY, targetZ);
    }

    /**
     * Returns a human-readable summary of active LOD mod integrations (e.g. Distant Horizons, Bobby).
     */
    public static String getIntegrationSummary() {
        return LODCompatManager.getIntegrationSummary();
    }
}
