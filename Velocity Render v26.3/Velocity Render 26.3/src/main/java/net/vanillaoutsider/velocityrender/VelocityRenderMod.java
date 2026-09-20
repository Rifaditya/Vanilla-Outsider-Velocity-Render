// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.vanillaoutsider.velocityrender.command.VelocityRenderCommand;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import net.vanillaoutsider.velocityrender.server.VelocityTicketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VelocityRenderMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityRenderMod.class);
    public static final String MOD_ID = "velocity-render";
    public static final String MOD_NAME = "Velocity Render";

    @Override
    public void onInitialize() {
        LOGGER.info("[VelocityRender] Initializing {}", MOD_NAME);

        // Register Dynamic GameRules via DasikLibrary
        VelocityRenderGameRules.register();

        // Register Brigadier commands (/velocityrender, /vr)
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VelocityRenderCommand.register(dispatcher);
        });

        // Register Server Tick Event for predictive forward chunk ticket tracking
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            VelocityTicketManager.tickServer(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                VelocityTicketManager.tickPlayer(player);
            }
        });

        // Register Disconnect Cleanup Listener
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            VelocityTicketManager.onPlayerDisconnect(handler.getPlayer());
        });

        LOGGER.info("[VelocityRender] Initialization complete.");
    }
}
