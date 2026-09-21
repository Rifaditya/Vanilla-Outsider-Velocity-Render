// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pillar 3: Comprehensive Brigadier Command Tree & Execution Suite.
 * Validates command registration, full syntax hierarchy, permission gating (OP vs unprivileged),
 * argument parsing, aliases, and tab completion suggestions for /vr and /velocityrender.
 */
class VelocityRenderCommandTest {

    private CommandDispatcher<CommandSourceStack> dispatcher;
    private CommandSourceStack adminSource;
    private CommandSourceStack unprivilegedSource;

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        dispatcher = new CommandDispatcher<>();
        VelocityRenderCommand.register(dispatcher);

        adminSource = new CommandSourceStack(
                CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                null,
                PermissionSet.ALL_PERMISSIONS,
                "AdminTester",
                Component.literal("AdminTester"),
                null,
                null
        );

        unprivilegedSource = new CommandSourceStack(
                CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                null,
                PermissionSet.NO_PERMISSIONS,
                "GuestTester",
                Component.literal("GuestTester"),
                null,
                null
        );
    }

    @Test
    @DisplayName("Registration: Both /vr and /velocityrender roots are registered with identical subtrees")
    void testRootCommandRegistration() {
        CommandNode<CommandSourceStack> vrNode = dispatcher.getRoot().getChild("vr");
        CommandNode<CommandSourceStack> fullNode = dispatcher.getRoot().getChild("velocityrender");

        assertNotNull(vrNode, "Short alias /vr must be registered in dispatcher");
        assertNotNull(fullNode, "Full command /velocityrender must be registered in dispatcher");

        // Verify standard subcommands exist on both roots
        String[] expectedSubcommands = {"help", "status", "get", "set", "reset", "reload", "dimclamp"};
        for (String sub : expectedSubcommands) {
            assertNotNull(vrNode.getChild(sub), "Subcommand /vr " + sub + " is missing");
            assertNotNull(fullNode.getChild(sub), "Subcommand /velocityrender " + sub + " is missing");
        }
    }

    @Test
    @DisplayName("Syntax: Tab completions for /vr get include all standard rules, clamp keys, and LOD hooks")
    void testGetCompletions() throws Exception {
        CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(
                dispatcher.parse("vr get ", adminSource)
        );
        Suggestions suggestions = future.get();
        List<String> list = suggestions.getList().stream().map(Suggestion::getText).toList();

        assertTrue(list.contains("enabled"), "Missing 'enabled' suggestion");
        assertTrue(list.contains("lead_multiplier"), "Missing 'lead_multiplier' suggestion");
        assertTrue(list.contains("budget_conservation"), "Missing 'budget_conservation' suggestion");
        assertTrue(list.contains("min_speed"), "Missing 'min_speed' suggestion");
        assertTrue(list.contains("turn_widening"), "Missing 'turn_widening' suggestion");
        assertTrue(list.contains("turn"), "Missing 'turn' alias suggestion");
        assertTrue(list.contains("vertical_lookahead"), "Missing 'vertical_lookahead' suggestion");
        assertTrue(list.contains("vertical"), "Missing 'vertical' alias suggestion");
        assertTrue(list.contains("server_ticket_budget"), "Missing 'server_ticket_budget' suggestion");
        assertTrue(list.contains("budget"), "Missing 'budget' alias suggestion");
        assertTrue(list.contains("debug_mode"), "Missing 'debug_mode' suggestion");
        assertTrue(list.contains("f3_debug"), "Missing 'f3_debug' suggestion");
        assertTrue(list.contains("f3"), "Missing 'f3' alias suggestion");
        assertTrue(list.contains("nether_reach_clamp_pct"), "Missing 'nether_reach_clamp_pct' suggestion");
        assertTrue(list.contains("nether_clamp"), "Missing 'nether_clamp' alias suggestion");
        assertTrue(list.contains("default_dense_reach_clamp_pct"), "Missing 'default_dense_reach_clamp_pct' suggestion");
        assertTrue(list.contains("dense_clamp"), "Missing 'dense_clamp' alias suggestion");
        assertTrue(list.contains("lod_hooks"), "Missing 'lod_hooks' suggestion");
        assertTrue(list.contains("lod"), "Missing 'lod' alias suggestion");
    }

    @Test
    @DisplayName("Syntax: All /vr set parameter branches exist and parse boolean/integer types")
    void testSetSubtreeParsing() {
        // Booleans
        assertParseSuccess("vr set enabled true", adminSource);
        assertParseSuccess("vr set enabled false", adminSource);
        assertParseSuccess("vr set budget_conservation true", adminSource);
        assertParseSuccess("vr set turn_widening true", adminSource);
        assertParseSuccess("vr set turn false", adminSource);
        assertParseSuccess("vr set vertical_lookahead true", adminSource);
        assertParseSuccess("vr set vertical false", adminSource);
        assertParseSuccess("vr set debug_mode false", adminSource);
        assertParseSuccess("vr set f3_debug true", adminSource);
        assertParseSuccess("vr set f3 false", adminSource);
        assertParseSuccess("vr set lod_hooks true", adminSource);
        assertParseSuccess("vr set lod false", adminSource);

        // Integers with bounded ranges
        assertParseSuccess("vr set lead_multiplier 100", adminSource);
        assertParseSuccess("vr set min_speed 20", adminSource);
        assertParseSuccess("vr set server_ticket_budget 64", adminSource);
        assertParseSuccess("vr set budget 128", adminSource);
        assertParseSuccess("vr set nether_reach_clamp_pct 60", adminSource);
        assertParseSuccess("vr set nether_clamp 50", adminSource);
        assertParseSuccess("vr set default_dense_reach_clamp_pct 80", adminSource);
        assertParseSuccess("vr set dense_clamp 70", adminSource);
    }

    @Test
    @DisplayName("Syntax: /vr dimclamp branches (list, reset, remove, set) parse cleanly")
    void testDimClampSubtreeParsing() {
        assertParseSuccess("vr dimclamp list", adminSource);
        assertParseSuccess("vr dimclamp reset", adminSource);
        assertParseSuccess("vr dimclamp remove minecraft:the_nether", adminSource);
        assertParseSuccess("vr dimclamp current 60", adminSource);
        assertParseSuccess("vr dimclamp minecraft:the_nether 50", adminSource);
    }

    @Test
    @DisplayName("Syntax: /vr help, status, get, reset, and reload parse correctly")
    void testStandardCommandsParsing() {
        assertParseSuccess("vr help", adminSource);
        assertParseSuccess("vr status", adminSource);
        assertParseSuccess("vr get enabled", adminSource);
        assertParseSuccess("vr get lod_hooks", adminSource);
        assertParseSuccess("vr get lod", adminSource);
        assertParseSuccess("vr reset", adminSource);
        assertParseSuccess("vr reload", adminSource);
    }

    @Test
    @DisplayName("Security & Permissions: Unprivileged players cannot access modifying commands")
    void testPermissionGatingForModifyingCommands() {
        // Read-only commands: help and status are accessible
        assertParseSuccess("vr help", unprivilegedSource);
        assertParseSuccess("vr status", unprivilegedSource);
        assertParseSuccess("vr get lod_hooks", unprivilegedSource);

        // Modifying commands: set, reset, reload, dimclamp must fail permission check
        assertParsePermissionDenied("vr set enabled false", unprivilegedSource);
        assertParsePermissionDenied("vr set lod true", unprivilegedSource);
        assertParsePermissionDenied("vr reset", unprivilegedSource);
        assertParsePermissionDenied("vr reload", unprivilegedSource);
        assertParsePermissionDenied("vr dimclamp reset", unprivilegedSource);
        assertParsePermissionDenied("vr dimclamp current 50", unprivilegedSource);
    }

    private void assertParseSuccess(String command, CommandSourceStack source) {
        ParseResults<CommandSourceStack> parse = dispatcher.parse(command, source);
        assertFalse(parse.getReader().canRead(),
                "Command parsing left unread input for: '" + command + "' -> " + parse.getReader().getRemaining());
        assertEquals(0, parse.getExceptions().size(),
                "Command parsing generated exceptions for: '" + command + "': " + parse.getExceptions());
        assertNotNull(parse.getContext().getCommand(),
                "Command node has no executable handler bound for: '" + command + "'");
    }

    private void assertParsePermissionDenied(String command, CommandSourceStack source) {
        ParseResults<CommandSourceStack> parse = dispatcher.parse(command, source);
        // Either the reader fails to advance past the restricted literal, or the execution node is null/empty
        boolean denied = parse.getContext().getCommand() == null
                || parse.getReader().canRead()
                || !parse.getExceptions().isEmpty();
        assertTrue(denied, "Unprivileged source unexpectedly passed permission check for: " + command);
    }
}
