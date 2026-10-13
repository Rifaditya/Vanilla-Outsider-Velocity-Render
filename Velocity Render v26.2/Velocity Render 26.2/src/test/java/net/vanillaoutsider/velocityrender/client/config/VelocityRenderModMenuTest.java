// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying ModMenu integration, reflection safety,
 * and runtime client state synchronization without headless JVM crashes.
 */
class VelocityRenderModMenuTest {

    @BeforeEach
    void setUp() {
        VelocityRenderConfig.get().resetDefaults();
    }

    @AfterEach
    void tearDown() {
        VelocityRenderConfig.get().resetDefaults();
    }

    @Test
    @DisplayName("ModMenu integration returns safe fallback factory when YACL is absent at runtime")
    void testModMenuFactoryReturnsNullWhenYaclAbsent() {
        ModMenuIntegration integration = new ModMenuIntegration();
        ConfigScreenFactory<?> factory = integration.getModConfigScreenFactory();
        assertNotNull(factory, "ModMenu factory should return non-null fallback factory");
        assertNull(factory.create(null), "Fallback factory should produce null screen when YACL is not loaded");
    }

    @Test
    @DisplayName("YaclScreenHelper factory methods return non-null ConfigScreenFactory instances")
    void testDualFactoryParity() {
        ConfigScreenFactory<?> factory = YaclScreenHelper.createFactory();
        ConfigScreenFactory<?> screenFactory = YaclScreenHelper.createScreen();

        assertNotNull(factory, "createFactory must return a non-null ConfigScreenFactory");
        assertNotNull(screenFactory, "createScreen must return a non-null ConfigScreenFactory");
    }

    @Test
    @DisplayName("ClientConfigSyncer synchronizes configuration parameters to ClientVelocityTracker")
    void testClientConfigSyncerUpdatesTracker() {
        VelocityRenderConfig config = VelocityRenderConfig.get();
        config.enabled = false;
        config.leadMultiplier = 175;
        config.minSpeedThresholdPct = 40;
        config.f3Debug = false;
        config.lodTrajectoryHooks = false;
        config.debugMode = true;

        ClientConfigSyncer.sync(config);

        assertFalse(ClientVelocityTracker.isEnabled(), "Tracker enabled state should match config");
        assertEquals(1.75, ClientVelocityTracker.getLeadMultiplier(), 1e-5, "Tracker leadMultiplier should be 1.75");
        assertEquals(0.40, ClientVelocityTracker.getMinSpeedThreshold(), 1e-5, "Tracker minSpeedThreshold should be 0.40");
        assertFalse(ClientVelocityTracker.isClientLodHooksEnabled(), "Tracker LOD hooks state should match config");
        assertTrue(ClientVelocityTracker.isDebugMode(), "Tracker debugMode should match config");
    }

    @Test
    @DisplayName("ClientConfigSyncer handles null config gracefully without throwing")
    void testClientConfigSyncerHandlesNull() {
        assertDoesNotThrow(() -> ClientConfigSyncer.sync(null));
    }
}
