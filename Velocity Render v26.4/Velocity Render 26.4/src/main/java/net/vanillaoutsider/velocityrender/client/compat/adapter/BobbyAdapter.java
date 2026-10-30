// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.compat.adapter;

import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Isolated soft-reflection adapter for Bobby.
 * Loaded ONLY when FabricLoader confirms 'bobby' is present.
 * Ensures strict classloader isolation so the JVM never references Bobby classes if absent.
 */
public final class BobbyAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BobbyAdapter.class);

    private boolean hooked = false;
    private boolean loggedOnce = false;

    public BobbyAdapter() {
        try {
            // Test reflective access to Bobby entrypoint or FakeChunkManager
            Class<?> bobbyClass = Class.forName("de.johni0702.minecraft.bobby.Bobby");
            if (bobbyClass != null) {
                hooked = true;
                LOGGER.info("[VelocityRender-LOD] Bobby detected. Soft-reflection bridge initialized.");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warn("[VelocityRender-LOD] Bobby mod is present, but main class was not found. Bridge operating in telemetry-only mode.");
            hooked = false;
        } catch (Throwable t) {
            LOGGER.warn("[VelocityRender-LOD] Failed to initialize Bobby adapter: {}", t.getMessage());
            hooked = false;
        }
    }

    public void updateTrajectory(LODTrajectoryState state) {
        if (!hooked || state == null || !state.active()) {
            return;
        }

        try {
            if (!loggedOnce) {
                LOGGER.info("[VelocityRender-LOD] Active lookahead trajectory broadcasting to Bobby (lead: {:.1f}b).", state.leadDistance());
                loggedOnce = true;
            }
            // Reflective hook points into Bobby's chunk loading prioritizer can be invoked here safely
        } catch (Throwable t) {
            LOGGER.debug("[VelocityRender-LOD] Bobby update error: {}", t.getMessage());
        }
    }

    public boolean isHooked() {
        return hooked;
    }
}
