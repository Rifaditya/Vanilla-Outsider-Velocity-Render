// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.sidedness;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VelocityRenderSidednessContractTest: Pillar 5 Sidedness & Mixin Contract Verification Suite (MC 1.21.11)
 *
 * Verifies:
 * - Zero unguarded net.minecraft.client.* or com.mojang.blaze3d.* references in common sources & bytecode.
 * - Complete removal of ModVersionGuard.java and absence of unremapped Class.forName in VelocityRenderMod.
 * - Structural integrity of velocity-render.mixins.json and fabric.mod.json.
 */
public class VelocityRenderSidednessContractTest {

    @Test
    @DisplayName("Pillar 5: Common Sources & Bytecode Contain Zero Unguarded Client References")
    void testCommonSidednessIsolation() throws IOException {
        Path srcRoot = Paths.get("src/main/java");
        assertTrue(Files.exists(srcRoot), "src/main/java must exist");

        try (Stream<Path> stream = Files.walk(srcRoot)) {
            List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Java source files must be found");

            for (Path file : javaFiles) {
                String normalized = file.toString().replace('\\', '/');
                String content = Files.readString(file, StandardCharsets.UTF_8);

                assertTrue(content.startsWith("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3"),
                        "Missing GPLv3 license header on line 1 in: " + normalized);

                if (normalized.contains("/client/")) {
                    continue;
                }

                assertFalse(content.contains("net.minecraft.client."),
                        "Forbidden client package reference in common source file: " + normalized);
                assertFalse(content.contains("com.mojang.blaze3d."),
                        "Forbidden Blaze3D reference in common source file: " + normalized);
            }
        }

        Path classesRoot = Paths.get("build/classes/java/main");
        if (Files.exists(classesRoot)) {
            try (Stream<Path> stream = Files.walk(classesRoot)) {
                List<Path> classFiles = stream.filter(p -> p.toString().endsWith(".class")).toList();
                for (Path classFile : classFiles) {
                    String normalized = classFile.toString().replace('\\', '/');
                    if (normalized.contains("/client/")) {
                        continue;
                    }
                    String rawBytes = new String(Files.readAllBytes(classFile), StandardCharsets.ISO_8859_1);
                    assertFalse(rawBytes.contains("net/minecraft/client/"),
                            "Forbidden net/minecraft/client/ constant pool reference in common bytecode: " + normalized);
                    assertFalse(rawBytes.contains("com/mojang/blaze3d/"),
                            "Forbidden com/mojang/blaze3d/ constant pool reference in common bytecode: " + normalized);
                }
            }
        }
    }

    @Test
    @DisplayName("Pillar 5: ModVersionGuard Purged and Zero Unremapped Class.forName in Entrypoint")
    void testModVersionGuardPurgedAndNoUnremappedReflection() throws IOException {
        Path guardPath = Paths.get("src/main/java/net/vanillaoutsider/velocityrender/util/ModVersionGuard.java");
        assertFalse(Files.exists(guardPath), "Obsolete ModVersionGuard.java must not exist");

        Path entrypointPath = Paths.get("src/main/java/net/vanillaoutsider/velocityrender/VelocityRenderMod.java");
        assertTrue(Files.exists(entrypointPath), "VelocityRenderMod.java entrypoint must exist");
        String entrypointContent = Files.readString(entrypointPath, StandardCharsets.UTF_8);
        assertFalse(entrypointContent.contains("ModVersionGuard"),
                "VelocityRenderMod.java must not reference ModVersionGuard");
        assertFalse(entrypointContent.contains("Class.forName"),
                "VelocityRenderMod.java must not invoke unremapped Class.forName");
    }

    @Test
    @DisplayName("Pillar 5: Mixin Configurations and Manifest Separate Client Mixins Cleanly")
    void testMixinAndManifestStructure() throws IOException {
        Path mixinConfigPath = Paths.get("src/main/resources/velocity-render.mixins.json");
        Path fabricModPath = Paths.get("src/main/resources/fabric.mod.json");

        assertTrue(Files.exists(mixinConfigPath), "velocity-render.mixins.json must exist");
        assertTrue(Files.exists(fabricModPath), "fabric.mod.json must exist");

        String mixinJson = Files.readString(mixinConfigPath, StandardCharsets.UTF_8);
        String fabricJson = Files.readString(fabricModPath, StandardCharsets.UTF_8);

        // Mixins JSON contract: common "mixins" array must be empty, only "client" mixins allowed
        assertTrue(mixinJson.contains("\"mixins\": []"),
                "velocity-render.mixins.json must contain empty common mixins array");
        assertTrue(mixinJson.contains("\"client\": ["),
                "velocity-render.mixins.json must define client mixins");

        // Verify client mixins
        assertTrue(mixinJson.contains("DebugScreenEntriesMixin"));
        assertTrue(mixinJson.contains("DebugScreenEntryListMixin"));
        assertTrue(mixinJson.contains("DebugScreenOverlayMixin"));
        assertTrue(mixinJson.contains("CompileTaskDynamicQueueMixin"));

        // Verify fabric.mod.json references mixin config and client entrypoint
        assertTrue(fabricJson.contains("velocity-render.mixins.json"),
                "fabric.mod.json must reference velocity-render.mixins.json");
        assertTrue(fabricJson.contains("net.vanillaoutsider.velocityrender.client.VelocityRenderClient"),
                "fabric.mod.json must define client entrypoint");
    }
}
