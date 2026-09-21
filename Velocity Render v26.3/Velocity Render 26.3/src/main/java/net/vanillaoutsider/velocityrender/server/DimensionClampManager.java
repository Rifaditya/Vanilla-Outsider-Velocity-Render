// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.vanillaoutsider.velocityrender.math.DimensionReachScaler;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hub manager for multi-dimensional reach scaling configuration.
 * Adheres to the Omni-Channel Configuration Standard (Many Ways to Change 1 Thing)
 * and Anti-Config Bombing Sparse Delta Persistence.
 *
 * <p>Precedence Hierarchy:
 * <ol>
 *   <li>Explicit In-Game GameRule override (Active World Save for Nether)</li>
 *   <li>Sparse Delta JSON override (config/velocity-render/dimension_clamps.json)</li>
 *   <li>Data-Driven Conventional Tag match (#c:dense_dimensions & #velocityrender:dense_dimensions)</li>
 *   <li>Vanilla Dimension Archetype default (Nether = 60%, End = 100%, Overworld = 100%)</li>
 *   <li>Global Fallback baseline (100%)</li>
 * </ol>
 */
public final class DimensionClampManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionClampManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CURRENT_SCHEMA_VERSION = 1;

    public static final TagKey<DimensionType> CONVENTIONAL_DENSE_TAG = TagKey.create(
            Registries.DIMENSION_TYPE,
            Identifier.fromNamespaceAndPath("c", "dense_dimensions")
    );

    public static final TagKey<DimensionType> MOD_DENSE_TAG = TagKey.create(
            Registries.DIMENSION_TYPE,
            Identifier.fromNamespaceAndPath("velocity-render", "dense_dimensions")
    );

    // In-memory sparse delta overrides: only customized dimensions are stored
    private static final Object2IntOpenHashMap<String> JSON_OVERRIDES = new Object2IntOpenHashMap<>();

    static {
        JSON_OVERRIDES.defaultReturnValue(-1);
    }

    private DimensionClampManager() {
    }

    /**
     * Resolves the effective reach scaling clamp percentage for the given level
     * according to the Omni-Channel Configuration Hierarchy with zero heap allocations.
     *
     * @param level the active ServerLevel
     * @return the clamped integer percentage (range 10 to 100)
     */
    public static int getEffectiveClampPct(Level level) {
        if (level == null) {
            return 100;
        }

        ResourceKey<Level> dimKey = level.dimension();
        String dimId = dimKey.identifier().toString();

        // 1. Check explicit Sparse Delta JSON override first
        int jsonOverride = JSON_OVERRIDES.getInt(dimId);
        if (jsonOverride >= 0) {
            return jsonOverride;
        }

        // 2. Check Nether GameRule when in the Nether
        if (dimKey.equals(Level.NETHER)) {
            return VelocityRenderGameRules.getNetherReachClampPct(level);
        }

        // 3. Check Data-Driven Conventional Tags (#c:dense_dimensions or #velocityrender:dense_dimensions)
        try {
            if (level.dimensionTypeRegistration().is(CONVENTIONAL_DENSE_TAG) || level.dimensionTypeRegistration().is(MOD_DENSE_TAG)) {
                return VelocityRenderGameRules.getDefaultDenseReachClampPct(level);
            }
        } catch (Throwable ignored) {
            // Safe fallback during un-initialized registry state or headless mock levels
        }

        // 4. Overworld and End default to 100% full lookahead reach
        if (dimKey.equals(Level.OVERWORLD) || dimKey.equals(Level.END)) {
            return 100;
        }

        // 5. Unknown/modded dimension fallback: use default dense clamp GameRule
        return VelocityRenderGameRules.getDefaultDenseReachClampPct(level);
    }

    /**
     * Sets or updates an in-memory sparse override and persists it to disk.
     */
    public static void setOverride(String dimId, int pct) {
        if (dimId == null || dimId.isBlank()) {
            return;
        }
        int clamped = Math.max(0, pct);
        JSON_OVERRIDES.put(dimId, clamped);
        save();
    }

    /**
     * Removes an in-memory sparse override and persists changes to disk.
     */
    public static boolean removeOverride(String dimId) {
        if (dimId == null) {
            return false;
        }
        int previous = JSON_OVERRIDES.removeInt(dimId);
        if (previous != -1) {
            save();
            return true;
        }
        return false;
    }

    public static int getOverride(String dimId) {
        return JSON_OVERRIDES.getInt(dimId);
    }

    public static void clearOverrides() {
        JSON_OVERRIDES.clear();
        save();
    }

    public static Map<String, Integer> getAllOverrides() {
        return new java.util.HashMap<>(JSON_OVERRIDES);
    }

    /**
     * Loads the sparse delta configuration from the default path.
     */
    public static void load() {
        loadFromPath(getDefaultConfigPath());
    }

    /**
     * Saves the current sparse delta configuration to the default path.
     */
    public static void save() {
        saveToPath(getDefaultConfigPath());
    }

    /**
     * Path-parameterized loader supporting headless tests.
     */
    public static void loadFromPath(Path path) {
        JSON_OVERRIDES.clear();
        if (path == null || !Files.exists(path)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            if (rootElement != null && rootElement.isJsonObject()) {
                JsonObject rootObj = rootElement.getAsJsonObject();
                if (rootObj.has("clamps") && rootObj.get("clamps").isJsonObject()) {
                    JsonObject clampsObj = rootObj.getAsJsonObject("clamps");
                    for (Map.Entry<String, JsonElement> entry : clampsObj.entrySet()) {
                        if (entry.getValue().isJsonPrimitive()) {
                            int pct = entry.getValue().getAsInt();
                            int clamped = Math.max(0, pct);
                            JSON_OVERRIDES.put(entry.getKey(), clamped);
                        }
                    }
                }
            }
            LOGGER.info("[VelocityRender] Loaded {} dimension reach overrides from {}", JSON_OVERRIDES.size(), path.getFileName());
        } catch (Exception e) {
            LOGGER.warn("[VelocityRender] Failed to parse {}: {}. Restoring clean in-memory state.", path.getFileName(), e.getMessage());
            JSON_OVERRIDES.clear();
        }
    }

    /**
     * Path-parameterized serializer supporting headless tests.
     * Strictly writes only modified overrides (Sparse Delta).
     */
    public static void saveToPath(Path path) {
        if (path == null) {
            return;
        }

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            JsonObject rootObj = new JsonObject();
            rootObj.addProperty("schemaVersion", CURRENT_SCHEMA_VERSION);

            JsonObject clampsObj = new JsonObject();
            for (Object2IntOpenHashMap.Entry<String> entry : JSON_OVERRIDES.object2IntEntrySet()) {
                clampsObj.addProperty(entry.getKey(), entry.getIntValue());
            }
            rootObj.add("clamps", clampsObj);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                GSON.toJson(rootObj, writer);
            }
        } catch (IOException e) {
            LOGGER.error("[VelocityRender] Failed to save {}: {}", path.getFileName(), e.getMessage());
        }
    }

    public static Path getDefaultConfigPath() {
        try {
            return FabricLoader.getInstance().getConfigDir().resolve("velocity-render/dimension_clamps.json");
        } catch (Throwable t) {
            return Path.of("config", "velocity-render", "dimension_clamps.json");
        }
    }
}
