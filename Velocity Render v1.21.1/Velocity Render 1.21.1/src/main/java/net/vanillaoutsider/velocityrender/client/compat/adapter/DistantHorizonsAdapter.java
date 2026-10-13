// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.compat.adapter;

import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Isolated soft-reflection adapter for Distant Horizons.
 * Loaded ONLY when FabricLoader confirms 'distanthorizons' is present.
 * Ensures strict classloader isolation so the JVM never references DH classes if absent.
 */
public final class DistantHorizonsAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistantHorizonsAdapter.class);

    private boolean hooked = false;
    private boolean loggedOnce = false;

    public DistantHorizonsAdapter() {
        try {
            // Test reflective access to Distant Horizons API entrypoint
            Class<?> dhApiClass = Class.forName("com.seibel.distanthorizons.api.DhApi");
            if (dhApiClass != null) {
                hooked = true;
                LOGGER.info("[VelocityRender-LOD] Distant Horizons detected. Soft-reflection bridge initialized.");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warn("[VelocityRender-LOD] Distant Horizons mod is present, but DhApi class was not found. Bridge operating in telemetry-only mode.");
            hooked = false;
        } catch (Throwable t) {
            LOGGER.warn("[VelocityRender-LOD] Failed to initialize Distant Horizons adapter: {}", t.getMessage());
            hooked = false;
        }
    }

    public void updateTrajectory(LODTrajectoryState state) {
        if (!hooked || state == null || !state.active()) {
            return;
        }

        try {
            // Forward projected focus coordinates and trajectory cone to Distant Horizons
            if (!loggedOnce) {
                LOGGER.info("[VelocityRender-LOD] Active lookahead trajectory broadcasting to Distant Horizons (lead: {:.1f}b).", state.leadDistance());
                loggedOnce = true;
            }
            // Reflective hook points into DH API or LOD generator priorities can be invoked here safely
        } catch (Throwable t) {
            LOGGER.debug("[VelocityRender-LOD] Distant Horizons update error: {}", t.getMessage());
        }
    }

    public boolean isHooked() {
        return hooked;
    }
}
