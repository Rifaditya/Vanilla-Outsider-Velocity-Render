// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;

public final class VelocityVectorHelper {
    private static final double MAX_LEAD_OFFSET = 256.0;
    public static final double MAX_VERTICAL_LEAD_OFFSET = 128.0;

    private VelocityVectorHelper() {
    }

    /**
     * Calculates the anisotropic velocity-biased squared distance between camera and chunk origin.
     * Pure primitive calculation with zero heap allocation.
     */
    public static double calculateBiasedDistanceSqr(BlockPos origin, Position cameraPos) {
        if (origin == null || cameraPos == null) {
            return Double.MAX_VALUE;
        }

        double deltaX = ((double) origin.getX() + 0.5) - cameraPos.x();
        double deltaY = ((double) origin.getY() + 0.5) - cameraPos.y();
        double deltaZ = ((double) origin.getZ() + 0.5) - cameraPos.z();

        double euclideanDistSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        // Zero-branch fast path when player is stationary or mod bias is inactive
        if (!ClientVelocityTracker.activeBias) {
            return euclideanDistSqr;
        }

        double dirX = ClientVelocityTracker.cachedNormDx;
        double dirY = ClientVelocityTracker.cachedNormDy;
        double dirZ = ClientVelocityTracker.cachedNormDz;
        double leadOffset = ClientVelocityTracker.cachedLeadOffset;

        double dotProduct = dirX * deltaX + dirY * deltaY + dirZ * deltaZ;
        double biasedDistSqr = euclideanDistSqr - 2.0 * dotProduct * leadOffset;
        return Math.max(0.0, biasedDistSqr);
    }

    /**
     * Standalone calculation helper for tests and headless verification.
     */
    public static double computeRawBiasedDistanceSqr(
            double originX, double originY, double originZ,
            double camX, double camY, double camZ,
            double dirX, double dirY, double dirZ,
            double speed, double leadMultiplier, double minSpeed
    ) {
        double deltaX = originX - camX;
        double deltaY = originY - camY;
        double deltaZ = originZ - camZ;

        double euclideanDistSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        if (speed < minSpeed) {
            return euclideanDistSqr;
        }

        double dotProduct = dirX * deltaX + dirY * deltaY + dirZ * deltaZ;
        double leadOffset = Math.min(MAX_LEAD_OFFSET, speed * 16.0 * leadMultiplier);
        double biasedDistSqr = euclideanDistSqr - 2.0 * dotProduct * leadOffset;
        return Math.max(0.0, biasedDistSqr);
    }

    /**
     * Pitch-biased distance calculation helper for 3D verification and testing.
     */
    public static double computeRawPitchBiasedDistanceSqr(
            double originX, double originY, double originZ,
            double camX, double camY, double camZ,
            double dirX, double dirY, double dirZ,
            double speed, double leadMultiplier, double minSpeed
    ) {
        return computeRawBiasedDistanceSqr(originX, originY, originZ, camX, camY, camZ, dirX, dirY, dirZ, speed, leadMultiplier, minSpeed);
    }
}
