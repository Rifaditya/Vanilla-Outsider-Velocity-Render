// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VelocityRenderClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityRenderClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[VelocityRender] Initializing Client Velocity Meshing Pipeline.");

        // Register client tick handler for velocity caching
        ClientTickEvents.END_CLIENT_TICK.register(ClientVelocityTracker::clientTick);

        // Initialize Level-of-Detail & chunk-caching compatibility hub
        net.vanillaoutsider.velocityrender.client.compat.LODCompatManager.init();

        LOGGER.info("[VelocityRender] Client initialization complete.");
    }
}
