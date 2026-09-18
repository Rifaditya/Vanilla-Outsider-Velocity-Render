// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.velocityrender.math.VelocityCalculator;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VelocityTicketManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityTicketManager.class);

    // Reusable per-player data holders (Zero-allocation in hot loops)
    private static final Map<UUID, VelocityCalculator> PLAYER_VELOCITY = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, LongOpenHashSet> ACTIVE_TICKETS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, LongOpenHashSet> SCRATCH_TICKETS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, ResourceKey<Level>> PLAYER_DIMENSION = new Object2ObjectOpenHashMap<>();

    // Throttling state caches
    private static final Map<UUID, Long> LAST_PLAYER_CHUNK = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, Float> LAST_PLAYER_YAW = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, Integer> LAST_PLAYER_TICK = new Object2ObjectOpenHashMap<>();

    // Telemetry metrics
    private static volatile int lastDynamicReach = 16;
    private static volatile float lastServerMspt = 20.0f;

    private VelocityTicketManager() {
    }

    public static void tickPlayer(ServerPlayer player) {
        if (player == null || player.isRemoved()) {
            return;
        }

        ServerLevel level = player.level() instanceof ServerLevel sl ? sl : null;
        if (level == null || !VelocityRenderGameRules.isEnabled(level)) {
            clearPlayer(player.getUUID(), level);
            return;
        }

        UUID uuid = player.getUUID();
        ResourceKey<Level> currentDim = level.dimension();
        ResourceKey<Level> lastDim = PLAYER_DIMENSION.put(uuid, currentDim);
        if (lastDim != null && !lastDim.equals(currentDim)) {
            clearPlayerTickets(uuid, level, ACTIVE_TICKETS.computeIfAbsent(uuid, k -> new LongOpenHashSet()));
        }

        VelocityCalculator calculator = PLAYER_VELOCITY.computeIfAbsent(uuid, k -> new VelocityCalculator());
        calculator.update(
                player.getX(), player.getY(), player.getZ(),
                player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z
        );

        double speed = calculator.getSpeedBlocksPerTick();
        double minSpeed = VelocityRenderGameRules.getMinSpeedThreshold(level);

        LongOpenHashSet activeTickets = ACTIVE_TICKETS.computeIfAbsent(uuid, k -> new LongOpenHashSet());

        if (speed < minSpeed) {
            if (!activeTickets.isEmpty()) {
                clearPlayerTickets(uuid, level, activeTickets);
            }
            return;
        }

        // Spatial and angular update throttling
        long currentChunkPacked = ChunkPos.pack(player.chunkPosition().x(), player.chunkPosition().z());
        Long lastChunkObj = LAST_PLAYER_CHUNK.get(uuid);
        Float lastYawObj = LAST_PLAYER_YAW.get(uuid);
        Integer lastTickObj = LAST_PLAYER_TICK.get(uuid);

        float currentYaw = player.getYRot();
        int currentTick = player.tickCount;

        boolean shouldRecalculate = (lastChunkObj == null)
                || (lastChunkObj != currentChunkPacked)
                || (lastYawObj != null && Math.abs(currentYaw - lastYawObj) > 6.0f)
                || (lastTickObj == null || (currentTick - lastTickObj) >= 8);

        if (!shouldRecalculate) {
            return;
        }

        LAST_PLAYER_CHUNK.put(uuid, currentChunkPacked);
        LAST_PLAYER_YAW.put(uuid, currentYaw);
        LAST_PLAYER_TICK.put(uuid, currentTick);

        // Continuous MSPT Watchdog: Dynamically taper reach under server load to guarantee 20 TPS
        float mspt = (float) level.getServer().getAverageTickTimeNanos() / (float) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        lastServerMspt = mspt;
        double msptFactor = 1.0;
        if (mspt > 25.0f) {
            msptFactor = Math.max(0.20, 1.0 - (double) (mspt - 25.0f) / 25.0);
        }

        int playerChunkX = player.chunkPosition().x();
        int playerChunkZ = player.chunkPosition().z();

        // Kinematic Look-Ahead Vector Blending: 70% movement momentum + 30% look direction for banked turns
        double moveDx = calculator.getNormDx();
        double moveDz = calculator.getNormDz();

        float yawRad = (float) Math.toRadians(currentYaw);
        double lookDx = -Math.sin(yawRad);
        double lookDz = Math.cos(yawRad);

        double blendedDx = moveDx * 0.70 + lookDx * 0.30;
        double blendedDz = moveDz * 0.70 + lookDz * 0.30;

        double horizontalMag = Math.sqrt(blendedDx * blendedDx + blendedDz * blendedDz);
        if (horizontalMag < 0.05) {
            return;
        }
        double normHzX = blendedDx / horizontalMag;
        double normHzZ = blendedDz / horizontalMag;

        int multiplierPct = VelocityRenderGameRules.getLeadMultiplierPct(level);
        double leadScale = (double) multiplierPct / 100.0;
        int maxAllowedReach = Math.max(4, (int) Math.round(16.0 * leadScale * msptFactor));
        int maxReachChunks = Math.min(maxAllowedReach, (int) Math.round(speed * 8.0 * leadScale * msptFactor));
        lastDynamicReach = maxReachChunks;

        // Zero-allocation reusable scratch set
        LongOpenHashSet targetChunks = SCRATCH_TICKETS.computeIfAbsent(uuid, k -> new LongOpenHashSet());
        targetChunks.clear();

        boolean enableFanOut = speed >= 0.75; // Banked flight fan-out

        // Continuous corridor ray march (every chunk along the trajectory line)
        for (int step = 1; step <= maxReachChunks; step++) {
            int targetX = playerChunkX + (int) Math.round(normHzX * step);
            int targetZ = playerChunkZ + (int) Math.round(normHzZ * step);
            targetChunks.add(ChunkPos.pack(targetX, targetZ));

            // Lateral corridor fan-out for banked turning at high speeds (from step 4 onward)
            if (enableFanOut && step >= 4) {
                int leftX = targetX + (int) Math.round(-normHzZ);
                int leftZ = targetZ + (int) Math.round(normHzX);
                int rightX = targetX + (int) Math.round(normHzZ);
                int rightZ = targetZ + (int) Math.round(-normHzX);

                targetChunks.add(ChunkPos.pack(leftX, leftZ));
                targetChunks.add(ChunkPos.pack(rightX, rightZ));
            }
        }

        // Remove tickets that are no longer in the forward path
        LongIterator activeIt = activeTickets.iterator();
        while (activeIt.hasNext()) {
            long packedPos = activeIt.nextLong();
            if (!targetChunks.contains(packedPos)) {
                level.getChunkSource().removeTicketWithRadius(TicketType.PLAYER_LOADING, ChunkPos.unpack(packedPos), 1);
                activeIt.remove();
            }
        }

        // Add new forward tickets
        LongIterator targetIt = targetChunks.iterator();
        while (targetIt.hasNext()) {
            long packedPos = targetIt.nextLong();
            if (activeTickets.add(packedPos)) {
                level.getChunkSource().addTicketWithRadius(TicketType.PLAYER_LOADING, ChunkPos.unpack(packedPos), 1);
            }
        }

        if (VelocityRenderGameRules.isDebugMode(level) && (player.tickCount % 40 == 0)) {
            LOGGER.info("[VelocityRender-Server] Player {} speed: {:.2f} b/t, MSPT: {:.1f}ms, reach: {} chunks, tickets: {}",
                    player.getScoreboardName(), speed, mspt, maxReachChunks, activeTickets.size());
        }
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        if (player != null) {
            ServerLevel level = player.level() instanceof ServerLevel sl ? sl : null;
            clearPlayer(player.getUUID(), level);
        }
    }

    public static void onPlayerChangeDimension(ServerPlayer player) {
        if (player != null) {
            ServerLevel level = player.level() instanceof ServerLevel sl ? sl : null;
            clearPlayer(player.getUUID(), level);
        }
    }

    private static void clearPlayer(UUID uuid, ServerLevel level) {
        PLAYER_VELOCITY.remove(uuid);
        PLAYER_DIMENSION.remove(uuid);
        LAST_PLAYER_CHUNK.remove(uuid);
        LAST_PLAYER_YAW.remove(uuid);
        LAST_PLAYER_TICK.remove(uuid);
        SCRATCH_TICKETS.remove(uuid);
        LongOpenHashSet tickets = ACTIVE_TICKETS.remove(uuid);
        if (tickets != null && level != null) {
            LongIterator it = tickets.iterator();
            while (it.hasNext()) {
                level.getChunkSource().removeTicketWithRadius(TicketType.PLAYER_LOADING, ChunkPos.unpack(it.nextLong()), 1);
            }
        }
    }

    private static void clearPlayerTickets(UUID uuid, ServerLevel level, LongOpenHashSet tickets) {
        if (level != null) {
            LongIterator it = tickets.iterator();
            while (it.hasNext()) {
                level.getChunkSource().removeTicketWithRadius(TicketType.PLAYER_LOADING, ChunkPos.unpack(it.nextLong()), 1);
            }
        }
        tickets.clear();
    }

    public static int getActiveTicketCount(UUID uuid) {
        LongOpenHashSet set = ACTIVE_TICKETS.get(uuid);
        return set != null ? set.size() : 0;
    }

    public static double getPlayerSpeed(UUID uuid) {
        VelocityCalculator calc = PLAYER_VELOCITY.get(uuid);
        return calc != null ? calc.getSpeedBlocksPerTick() : 0.0;
    }

    public static int getLastDynamicReach() {
        return lastDynamicReach;
    }

    public static float getLastServerMspt() {
        return lastServerMspt;
    }
}
