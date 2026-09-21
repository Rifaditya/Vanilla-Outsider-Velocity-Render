// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

/**
 * Pure algorithmic utility for calculating Level-of-Detail (LOD) and chunk-caching lookahead trajectories.
 * Scaled aggressively (up to 1,024 blocks ahead) to match distant LOD view distances (32-256 chunks)
 * in Distant Horizons and Bobby without external mod dependencies.
 */
public final class LODTrajectoryCalculator {
    public static final double MAX_LOD_LEAD_OFFSET = 1024.0;
    public static final double LOD_LEAD_FACTOR = 256.0;
    public static final double DEFAULT_MIN_SPEED_THRESHOLD = 0.20; // 0.20 b/t = 4.0 m/s

    private LODTrajectoryCalculator() {
    }

    /**
     * Calculates the forward lead offset in blocks based on speed and multiplier.
     * Formula: min(1024.0, speed * 256.0 * leadMultiplier).
     */
    public static double calculateLeadOffset(double speed, double leadMultiplier) {
        if (speed <= 0.0 || leadMultiplier <= 0.0 || Double.isNaN(speed) || Double.isNaN(leadMultiplier) || Double.isInfinite(speed) || Double.isInfinite(leadMultiplier)) {
            return 0.0;
        }
        return Math.min(MAX_LOD_LEAD_OFFSET, speed * LOD_LEAD_FACTOR * leadMultiplier);
    }

    /**
     * Calculates the 3D projected focus coordinates along the travel direction vector.
     * When inactive or lead is 0.0, safely returns the camera coordinates.
     */
    public static double[] calculateLookaheadFocus(
            double camX, double camY, double camZ,
            double dirX, double dirY, double dirZ,
            double leadOffset
    ) {
        if (leadOffset <= 0.0 || Double.isNaN(leadOffset) || Double.isNaN(dirX) || Double.isNaN(dirY) || Double.isNaN(dirZ) || Double.isInfinite(leadOffset)) {
            return new double[]{camX, camY, camZ};
        }
        return new double[]{
                camX + dirX * leadOffset,
                camY + dirY * leadOffset,
                camZ + dirZ * leadOffset
        };
    }

    /**
     * Computes the anisotropic directional distance squared for distant LOD chunks/regions.
     * Matches VelocityVectorHelper's mathematical consistency:
     * biasedDistSqr = max(0.0, euclideanDistSqr - 2.0 * dotProduct * leadOffset).
     */
    public static double calculateBiasedLODDistanceSqr(
            double targetX, double targetY, double targetZ,
            double camX, double camY, double camZ,
            double dirX, double dirY, double dirZ,
            double leadOffset
    ) {
        if (Double.isNaN(targetX) || Double.isNaN(targetY) || Double.isNaN(targetZ)
                || Double.isNaN(camX) || Double.isNaN(camY) || Double.isNaN(camZ)) {
            return Double.MAX_VALUE;
        }

        double deltaX = targetX - camX;
        double deltaY = targetY - camY;
        double deltaZ = targetZ - camZ;

        double euclideanDistSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if (leadOffset <= 0.0 || Double.isNaN(leadOffset)
                || Double.isNaN(dirX) || Double.isNaN(dirY) || Double.isNaN(dirZ)
                || (dirX == 0.0 && dirY == 0.0 && dirZ == 0.0)) {
            return euclideanDistSqr;
        }

        double dotProduct = dirX * deltaX + dirY * deltaY + dirZ * deltaZ;
        double biasedDistSqr = euclideanDistSqr - 2.0 * dotProduct * leadOffset;
        return Math.max(0.0, biasedDistSqr);
    }

    /**
     * Constructs a comprehensive immutable LODTrajectoryState snapshot.
     */
    public static LODTrajectoryState createState(
            double camX, double camY, double camZ,
            double dirX, double dirY, double dirZ,
            double speedBlocksPerTick,
            double leadMultiplier,
            double minSpeedThreshold
    ) {
        if (Double.isNaN(speedBlocksPerTick) || speedBlocksPerTick < minSpeedThreshold || leadMultiplier <= 0.0) {
            return new LODTrajectoryState(
                    false,
                    camX, camY, camZ,
                    camX, camY, camZ,
                    0.0, 0.0, 0.0,
                    0.0,
                    Math.max(0.0, Double.isNaN(speedBlocksPerTick) ? 0.0 : speedBlocksPerTick),
                    Math.max(0.0, Double.isNaN(speedBlocksPerTick) ? 0.0 : speedBlocksPerTick * 20.0)
            );
        }

        double leadOffset = calculateLeadOffset(speedBlocksPerTick, leadMultiplier);
        double[] focus = calculateLookaheadFocus(camX, camY, camZ, dirX, dirY, dirZ, leadOffset);

        return new LODTrajectoryState(
                true,
                camX, camY, camZ,
                focus[0], focus[1], focus[2],
                dirX, dirY, dirZ,
                leadOffset,
                speedBlocksPerTick,
                speedBlocksPerTick * 20.0
        );
    }

    /**
     * Immutable snapshot record for LOD trajectory data.
     */
    public record LODTrajectoryState(
            boolean active,
            double camX,
            double camY,
            double camZ,
            double focusX,
            double focusY,
            double focusZ,
            double dirX,
            double dirY,
            double dirZ,
            double leadDistance,
            double speedBlocksPerTick,
            double speedMetersPerSecond
    ) {
        public static final LODTrajectoryState INACTIVE = new LODTrajectoryState(
                false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        );

        public int focusChunkX() {
            return (int) Math.floor(focusX) >> 4;
        }

        public int focusChunkZ() {
            return (int) Math.floor(focusZ) >> 4;
        }

        public int camChunkX() {
            return (int) Math.floor(camX) >> 4;
        }

        public int camChunkZ() {
            return (int) Math.floor(camZ) >> 4;
        }
    }
}
