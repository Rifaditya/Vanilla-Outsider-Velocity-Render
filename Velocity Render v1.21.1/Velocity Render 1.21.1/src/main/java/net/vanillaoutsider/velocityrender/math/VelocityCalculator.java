// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.math;

public final class VelocityCalculator {
    private double lastX;
    private double lastY;
    private double lastZ;
    private boolean initialized = false;

    private double speedBlocksPerTick = 0.0;
    private double normDx = 0.0;
    private double normDy = 0.0;
    private double normDz = 0.0;

    public void update(double currentX, double currentY, double currentZ, double deltaX, double deltaY, double deltaZ) {
        double effectiveDx = deltaX;
        double effectiveDy = deltaY;
        double effectiveDz = deltaZ;

        if (initialized) {
            double diffX = currentX - lastX;
            double diffY = currentY - lastY;
            double diffZ = currentZ - lastZ;
            double diffMagSqr = diffX * diffX + diffY * diffY + diffZ * diffZ;
            double deltaMagSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

            if (diffMagSqr > deltaMagSqr && diffMagSqr > 0.0001) {
                effectiveDx = diffX;
                effectiveDy = diffY;
                effectiveDz = diffZ;
            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        initialized = true;

        double magSqr = effectiveDx * effectiveDx + effectiveDy * effectiveDy + effectiveDz * effectiveDz;
        if (magSqr < 0.000001 || Double.isNaN(magSqr) || Double.isInfinite(magSqr)) {
            speedBlocksPerTick = 0.0;
            normDx = 0.0;
            normDy = 0.0;
            normDz = 0.0;
            return;
        }

        double magnitude = Math.sqrt(magSqr);
        speedBlocksPerTick = magnitude;
        normDx = effectiveDx / magnitude;
        normDy = effectiveDy / magnitude;
        normDz = effectiveDz / magnitude;
    }

    public void reset() {
        initialized = false;
        speedBlocksPerTick = 0.0;
        normDx = 0.0;
        normDy = 0.0;
        normDz = 0.0;
    }

    public double getSpeedBlocksPerTick() {
        return speedBlocksPerTick;
    }

    public double getSpeedMetersPerSecond() {
        return speedBlocksPerTick * 20.0;
    }

    public double getNormDx() {
        return normDx;
    }

    public double getNormDy() {
        return normDy;
    }

    public double getNormDz() {
        return normDz;
    }
}
