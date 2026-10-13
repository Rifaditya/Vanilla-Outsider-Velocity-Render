// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-isolated runtime state synchronizer.
 * Dispatches active configuration changes to {@link ClientVelocityTracker}
 * and integrated singleplayer server GameRules.
 *
 * <p>Annotated with {@link Environment}({@link EnvType#CLIENT}) to prevent
 * classloading and linkage crashes on dedicated server environments.
 */
@Environment(EnvType.CLIENT)
public final class ClientConfigSyncer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigSyncer.class);

    private ClientConfigSyncer() {
    }

    public static void sync(VelocityRenderConfig config) {
        if (config == null) {
            return;
        }

        // 1. Synchronize Client-side tracker parameters
        ClientVelocityTracker.setEnabled(config.enabled);
        ClientVelocityTracker.setLeadMultiplier(config.leadMultiplier / 100.0);
        ClientVelocityTracker.setMinSpeedThreshold(config.minSpeedThresholdPct / 100.0);
        ClientVelocityTracker.setF3DebugEnabled(config.f3Debug);
        ClientVelocityTracker.setClientLodHooksEnabled(config.lodTrajectoryHooks);
        ClientVelocityTracker.setDebugMode(config.debugMode);

        // 2. Synchronize Integrated Singleplayer Server GameRules (if running)
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) {
                MinecraftServer server = mc.getSingleplayerServer();
                if (server != null) {
                    server.execute(() -> {
                        for (ServerLevel level : server.getAllLevels()) {
                            if (VelocityRenderGameRules.ENABLED != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.ENABLED).set(config.enabled, server);
                            }
                            if (VelocityRenderGameRules.LEAD_MULTIPLIER != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.LEAD_MULTIPLIER).set(config.leadMultiplier, server);
                            }
                            if (VelocityRenderGameRules.MIN_SPEED_THRESHOLD_PCT != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.MIN_SPEED_THRESHOLD_PCT).set(config.minSpeedThresholdPct, server);
                            }
                            if (VelocityRenderGameRules.TURN_WIDENING != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.TURN_WIDENING).set(config.turnWidening, server);
                            }
                            if (VelocityRenderGameRules.VERTICAL_LOOKAHEAD != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.VERTICAL_LOOKAHEAD).set(config.verticalLookahead, server);
                            }
                            if (VelocityRenderGameRules.SERVER_TICKET_BUDGET != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.SERVER_TICKET_BUDGET).set(config.serverTicketBudget, server);
                            }
                            if (VelocityRenderGameRules.DEBUG_MODE != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.DEBUG_MODE).set(config.debugMode, server);
                            }
                            if (VelocityRenderGameRules.F3_DEBUG != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.F3_DEBUG).set(config.f3Debug, server);
                            }
                            if (VelocityRenderGameRules.NETHER_REACH_CLAMP_PCT != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.NETHER_REACH_CLAMP_PCT).set(config.netherReachClampPct, server);
                            }
                            if (VelocityRenderGameRules.DEFAULT_DENSE_REACH_CLAMP_PCT != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.DEFAULT_DENSE_REACH_CLAMP_PCT).set(config.defaultDenseReachClampPct, server);
                            }
                            if (VelocityRenderGameRules.LOD_TRAJECTORY_HOOKS != null) {
                                level.getGameRules().getRule(VelocityRenderGameRules.LOD_TRAJECTORY_HOOKS).set(config.lodTrajectoryHooks, server);
                            }
                        }
                    });
                }
            }
        } catch (Throwable t) {
            LOGGER.debug("[VelocityRender] Singleplayer server GameRule sync skipped: {}", t.getMessage());
        }
    }
}
