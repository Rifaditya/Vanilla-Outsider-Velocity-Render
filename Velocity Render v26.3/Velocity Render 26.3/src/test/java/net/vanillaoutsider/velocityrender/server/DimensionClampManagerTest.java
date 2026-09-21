// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DimensionClampManagerTest {

    @TempDir
    Path tempDir;

    private Path testConfigPath;

    @BeforeEach
    void setUp() {
        testConfigPath = tempDir.resolve("velocity-render/dimension_clamps.json");
        DimensionClampManager.clearOverrides();
    }

    @Test
    @DisplayName("Empty or non-existent file should load clean with zero overrides")
    void testLoadEmptyOrNonExistent() {
        DimensionClampManager.loadFromPath(testConfigPath);
        Assertions.assertEquals(-1, DimensionClampManager.getOverride("minecraft:the_nether"));
        Assertions.assertTrue(DimensionClampManager.getAllOverrides().isEmpty());
    }

    @Test
    @DisplayName("Saving and loading overrides should persist sparse deltas cleanly")
    void testSaveAndLoadOverrides() {
        DimensionClampManager.setOverride("minecraft:the_nether", 50);
        DimensionClampManager.setOverride("custom_mod:deep_dark", 40);

        DimensionClampManager.saveToPath(testConfigPath);
        Assertions.assertTrue(Files.exists(testConfigPath));

        // Clear in-memory state and reload from disk
        DimensionClampManager.clearOverrides();
        Assertions.assertEquals(-1, DimensionClampManager.getOverride("minecraft:the_nether"));

        DimensionClampManager.loadFromPath(testConfigPath);
        Assertions.assertEquals(50, DimensionClampManager.getOverride("minecraft:the_nether"));
        Assertions.assertEquals(40, DimensionClampManager.getOverride("custom_mod:deep_dark"));
        Assertions.assertEquals(2, DimensionClampManager.getAllOverrides().size());
    }

    @Test
    @DisplayName("Clamping bounds [10, 100] should be enforced on overrides")
    void testOverrideClampingBounds() {
        DimensionClampManager.setOverride("test:too_low", 5);
        Assertions.assertEquals(10, DimensionClampManager.getOverride("test:too_low"), "Values < 10 must clamp to 10");

        DimensionClampManager.setOverride("test:too_high", 150);
        Assertions.assertEquals(100, DimensionClampManager.getOverride("test:too_high"), "Values > 100 must clamp to 100");
    }

    @Test
    @DisplayName("Removing overrides should remove entry and update persistent file")
    void testRemoveOverride() {
        DimensionClampManager.setOverride("minecraft:the_nether", 60);
        DimensionClampManager.saveToPath(testConfigPath);
        Assertions.assertEquals(60, DimensionClampManager.getOverride("minecraft:the_nether"));

        boolean removed = DimensionClampManager.removeOverride("minecraft:the_nether");
        Assertions.assertTrue(removed);
        Assertions.assertEquals(-1, DimensionClampManager.getOverride("minecraft:the_nether"));

        // Second removal attempt should return false
        Assertions.assertFalse(DimensionClampManager.removeOverride("minecraft:the_nether"));
    }

    @Test
    @DisplayName("Corrupted JSON syntax should not crash and should safely restore empty state")
    void testCorruptedJsonResilience() throws IOException {
        Files.createDirectories(testConfigPath.getParent());
        Files.writeString(testConfigPath, "{ this is not valid JSON }");

        Assertions.assertDoesNotThrow(() -> DimensionClampManager.loadFromPath(testConfigPath));
        Assertions.assertTrue(DimensionClampManager.getAllOverrides().isEmpty());
    }
}
