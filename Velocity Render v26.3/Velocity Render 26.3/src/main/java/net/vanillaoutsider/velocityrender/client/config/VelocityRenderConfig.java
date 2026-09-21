// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistent configuration data model for Velocity Render.
 * Supports YetAnotherConfigLib (YACL v3) GUI bindings, offline manual editing,
 * and bi-directional synchronization with in-game GameRules and Brigadier commands.
 */
public final class VelocityRenderConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityRenderConfig.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static VelocityRenderConfig INSTANCE = new VelocityRenderConfig();

    // Category 1: Flight Biasing & Meshing
    public boolean enabled = true;
    public int leadMultiplier = 100;
    public int minSpeedThresholdPct = 20;
    public boolean turnWidening = true;
    public boolean verticalLookahead = true;

    // Category 2: Server Quotas & WorldGen
    public int serverTicketBudget = 64;
    public int netherReachClampPct = 60;
    public int defaultDenseReachClampPct = 80;

    // Category 3: Telemetry & Compatibility
    public boolean lodTrajectoryHooks = true;
    public boolean f3Debug = true;
    public boolean debugMode = false;

    public VelocityRenderConfig() {
    }

    public static VelocityRenderConfig get() {
        return INSTANCE;
    }

    public void resetDefaults() {
        this.enabled = true;
        this.leadMultiplier = 100;
        this.minSpeedThresholdPct = 20;
        this.turnWidening = true;
        this.verticalLookahead = true;
        this.serverTicketBudget = 64;
        this.netherReachClampPct = 60;
        this.defaultDenseReachClampPct = 80;
        this.lodTrajectoryHooks = true;
        this.f3Debug = true;
        this.debugMode = false;
    }

    public void copyFrom(VelocityRenderConfig other) {
        if (other == null) {
            return;
        }
        this.enabled = other.enabled;
        this.leadMultiplier = other.leadMultiplier;
        this.minSpeedThresholdPct = other.minSpeedThresholdPct;
        this.turnWidening = other.turnWidening;
        this.verticalLookahead = other.verticalLookahead;
        this.serverTicketBudget = other.serverTicketBudget;
        this.netherReachClampPct = other.netherReachClampPct;
        this.defaultDenseReachClampPct = other.defaultDenseReachClampPct;
        this.lodTrajectoryHooks = other.lodTrajectoryHooks;
        this.f3Debug = other.f3Debug;
        this.debugMode = other.debugMode;
    }

    public static void load() {
        loadFromPath(getDefaultConfigPath());
    }

    public static void save() {
        saveToPath(getDefaultConfigPath());
    }

    public static void loadFromPath(Path path) {
        if (path == null || !Files.exists(path)) {
            LOGGER.info("[VelocityRender] No config found at {}. Using defaults.", path != null ? path.getFileName() : "null");
            INSTANCE.resetDefaults();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            VelocityRenderConfig loaded = GSON.fromJson(reader, VelocityRenderConfig.class);
            if (loaded != null) {
                INSTANCE.copyFrom(loaded);
                LOGGER.info("[VelocityRender] Loaded configuration from {}", path.getFileName());
            }
        } catch (Exception e) {
            LOGGER.warn("[VelocityRender] Failed to parse {}: {}. Restoring defaults.", path.getFileName(), e.getMessage());
            INSTANCE.resetDefaults();
        }
    }

    public static void saveToPath(Path path) {
        if (path == null) {
            return;
        }

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                GSON.toJson(INSTANCE, writer);
            }
            LOGGER.info("[VelocityRender] Saved configuration to {}", path.getFileName());
        } catch (IOException e) {
            LOGGER.error("[VelocityRender] Failed to save {}: {}", path.getFileName(), e.getMessage());
        }
    }

    public static Path getDefaultConfigPath() {
        try {
            return FabricLoader.getInstance().getConfigDir().resolve("velocity-render/config.json");
        } catch (Throwable t) {
            return Path.of("config", "velocity-render", "config.json");
        }
    }
}
