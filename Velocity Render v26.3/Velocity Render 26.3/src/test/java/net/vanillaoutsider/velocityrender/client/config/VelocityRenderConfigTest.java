// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying serialization, deserialization, recovery, and defaults of VelocityRenderConfig.
 */
class VelocityRenderConfigTest {

    @TempDir
    Path tempDir;

    private Path testConfigPath;

    @BeforeEach
    void setUp() {
        testConfigPath = tempDir.resolve("config.json");
        VelocityRenderConfig.get().resetDefaults();
    }

    @AfterEach
    void tearDown() {
        VelocityRenderConfig.get().resetDefaults();
    }

    @Test
    void testDefaultValuesMatchGameRules() {
        VelocityRenderConfig config = VelocityRenderConfig.get();
        assertTrue(config.enabled, "enabled should default to true");
        assertEquals(100, config.leadMultiplier, "leadMultiplier should default to 100%");
        assertEquals(20, config.minSpeedThresholdPct, "minSpeedThresholdPct should default to 20%");
        assertTrue(config.turnWidening, "turnWidening should default to true");
        assertTrue(config.verticalLookahead, "verticalLookahead should default to true");
        assertEquals(64, config.serverTicketBudget, "serverTicketBudget should default to 64");
        assertEquals(60, config.netherReachClampPct, "netherReachClampPct should default to 60%");
        assertEquals(80, config.defaultDenseReachClampPct, "defaultDenseReachClampPct should default to 80%");
        assertTrue(config.lodTrajectoryHooks, "lodTrajectoryHooks should default to true");
        assertTrue(config.f3Debug, "f3Debug should default to true");
        assertFalse(config.debugMode, "debugMode should default to false");
    }

    @Test
    void testJsonSerializationRoundTrip() {
        VelocityRenderConfig config = VelocityRenderConfig.get();
        config.leadMultiplier = 250;
        config.serverTicketBudget = 128;
        config.netherReachClampPct = 40;
        config.turnWidening = false;
        config.debugMode = true;

        VelocityRenderConfig.saveToPath(testConfigPath);
        assertTrue(Files.exists(testConfigPath), "Config file should be created on disk");

        // Reset and load back
        config.resetDefaults();
        assertEquals(100, config.leadMultiplier);

        VelocityRenderConfig.loadFromPath(testConfigPath);
        assertEquals(250, config.leadMultiplier);
        assertEquals(128, config.serverTicketBudget);
        assertEquals(40, config.netherReachClampPct);
        assertFalse(config.turnWidening);
        assertTrue(config.debugMode);
        assertTrue(config.f3Debug);
    }

    @Test
    void testResetDefaultsRestoresSafeState() {
        VelocityRenderConfig config = VelocityRenderConfig.get();
        config.leadMultiplier = 999;
        config.serverTicketBudget = 1024;
        config.enabled = false;

        config.resetDefaults();
        assertTrue(config.enabled);
        assertEquals(100, config.leadMultiplier);
        assertEquals(64, config.serverTicketBudget);
    }

    @Test
    void testCorruptedJsonFallsBackSafely() throws IOException {
        Files.writeString(testConfigPath, "{ corrupted_json_syntax: [ invalid ] }");

        VelocityRenderConfig.loadFromPath(testConfigPath);
        VelocityRenderConfig config = VelocityRenderConfig.get();

        assertNotNull(config);
        assertEquals(100, config.leadMultiplier, "Should safely restore default on corrupt JSON");
    }
}
