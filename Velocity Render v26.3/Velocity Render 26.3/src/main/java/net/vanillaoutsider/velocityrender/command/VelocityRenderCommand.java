// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;
import net.vanillaoutsider.velocityrender.client.compat.LODCompatManager;
import net.vanillaoutsider.velocityrender.math.DimensionReachScaler;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator.LODTrajectoryState;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import net.vanillaoutsider.velocityrender.server.DimensionClampManager;
import net.vanillaoutsider.velocityrender.server.VelocityTicketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VelocityRenderCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityRenderCommand.class);

    private VelocityRenderCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register /velocityrender
        dispatcher.register(buildNode("velocityrender"));
        // Register short alias /vr
        dispatcher.register(buildNode("vr"));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildNode(String literalName) {
        return Commands.literal(literalName)
                .then(Commands.literal("help").executes(VelocityRenderCommand::executeHelp))
                .then(Commands.literal("status").executes(VelocityRenderCommand::executeStatus))
                .then(Commands.literal("get")
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("enabled");
                                    builder.suggest("lead_multiplier");
                                    builder.suggest("budget_conservation");
                                    builder.suggest("min_speed");
                                    builder.suggest("turn_widening");
                                    builder.suggest("turn");
                                    builder.suggest("vertical_lookahead");
                                    builder.suggest("vertical");
                                    builder.suggest("server_ticket_budget");
                                    builder.suggest("budget");
                                    builder.suggest("debug_mode");
                                    builder.suggest("f3_debug");
                                    builder.suggest("f3");
                                    builder.suggest("nether_reach_clamp_pct");
                                    builder.suggest("nether_clamp");
                                    builder.suggest("default_dense_reach_clamp_pct");
                                    builder.suggest("dense_clamp");
                                    builder.suggest("lod_hooks");
                                    builder.suggest("lod");
                                    return builder.buildFuture();
                                })
                                .executes(VelocityRenderCommand::executeGet)))
                .then(Commands.literal("set")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("enabled")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "enabled", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("lead_multiplier")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 300))
                                        .executes(ctx -> executeSetInt(ctx, "lead_multiplier", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("budget_conservation")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "budget_conservation", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("min_speed")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, 200))
                                        .executes(ctx -> executeSetInt(ctx, "min_speed", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("turn_widening")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "turn_widening", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("turn")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "turn_widening", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("vertical_lookahead")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "vertical_lookahead", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("vertical")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "vertical_lookahead", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("server_ticket_budget")
                                .then(Commands.argument("value", IntegerArgumentType.integer(16, 256))
                                        .executes(ctx -> executeSetInt(ctx, "server_ticket_budget", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("budget")
                                .then(Commands.argument("value", IntegerArgumentType.integer(16, 256))
                                        .executes(ctx -> executeSetInt(ctx, "server_ticket_budget", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("debug_mode")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "debug_mode", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("f3_debug")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "f3_debug", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("f3")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "f3_debug", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("nether_reach_clamp_pct")
                                .then(Commands.argument("value", IntegerArgumentType.integer(10, 100))
                                        .executes(ctx -> executeSetInt(ctx, "nether_reach_clamp_pct", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("nether_clamp")
                                .then(Commands.argument("value", IntegerArgumentType.integer(10, 100))
                                        .executes(ctx -> executeSetInt(ctx, "nether_reach_clamp_pct", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("default_dense_reach_clamp_pct")
                                .then(Commands.argument("value", IntegerArgumentType.integer(10, 100))
                                        .executes(ctx -> executeSetInt(ctx, "default_dense_reach_clamp_pct", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("dense_clamp")
                                .then(Commands.argument("value", IntegerArgumentType.integer(10, 100))
                                        .executes(ctx -> executeSetInt(ctx, "default_dense_reach_clamp_pct", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("lod_hooks")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "lod_hooks", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("lod")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> executeSetBool(ctx, "lod_hooks", BoolArgumentType.getBool(ctx, "value"))))))
                .then(Commands.literal("dimclamp")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("list")
                                .executes(VelocityRenderCommand::executeDimClampList))
                        .then(Commands.literal("reset")
                                .executes(VelocityRenderCommand::executeDimClampReset))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("dimension", StringArgumentType.string())
                                        .suggests(VelocityRenderCommand::suggestDimensions)
                                        .executes(VelocityRenderCommand::executeDimClampRemove)))
                        .then(Commands.argument("dimension", StringArgumentType.string())
                                .suggests(VelocityRenderCommand::suggestDimensions)
                                .then(Commands.argument("percentage", IntegerArgumentType.integer(10, 100))
                                        .executes(VelocityRenderCommand::executeDimClampSet))))
                .then(Commands.literal("reset")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(VelocityRenderCommand::executeReset))
                .then(Commands.literal("reload")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(VelocityRenderCommand::executeReload))
                .executes(VelocityRenderCommand::executeStatus);
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal(
                "§6=== Velocity Render Commands (/velocityrender, /vr) ===§r\n" +
                "§e/vr status§r - View real-time velocity, forward reach, MSPT, and active tickets.\n" +
                "§e/vr get <rule>§r - Query current configuration value.\n" +
                "§e/vr set <rule> <value>§r - Update settings in real time.\n" +
                "§e/vr dimclamp <dim> <pct>§r - Set custom lookahead reach clamp for a dimension.\n" +
                "§e/vr dimclamp list§r - List custom dimension clamp overrides.\n" +
                "§e/vr dimclamp remove <dim>§r - Remove custom dimension clamp override.\n" +
                "§e/vr dimclamp reset§r - Reset all custom dimension clamp overrides.\n" +
                "§e/vr reset§r - Restore default settings.\n" +
                "§e/vr reload§r - Reload configuration and dimension clamps."
        ), false);
        return 1;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        boolean enabled = VelocityRenderGameRules.isEnabled(level);
        int leadMultiplier = VelocityRenderGameRules.getLeadMultiplierPct(level);
        double minSpeed = VelocityRenderGameRules.getMinSpeedThreshold(level);
        boolean budget = VelocityRenderGameRules.isBudgetConservation(level);
        boolean turnWidening = VelocityRenderGameRules.isTurnWideningEnabled(level);
        boolean verticalLookahead = VelocityRenderGameRules.isVerticalLookaheadEnabled(level);
        boolean debug = VelocityRenderGameRules.isDebugMode(level);
        boolean f3Debug = VelocityRenderGameRules.isF3DebugEnabled(level);

        int activeTickets = 0;
        double currentSpeed = 0.0;
        float turnRate = 0.0f;
        String direction = "N/A";
        float currentPitch = 0.0f;
        double currentVDelta = 0.0;
        int playerQuota = 0;
        if (source.getEntity() instanceof ServerPlayer player) {
            activeTickets = VelocityTicketManager.getActiveTicketCount(player.getUUID());
            currentSpeed = VelocityTicketManager.getPlayerSpeed(player.getUUID());
            turnRate = VelocityTicketManager.getPlayerTurnRate(player.getUUID());
            int turnSign = VelocityTicketManager.getPlayerTurnSign(player.getUUID());
            direction = turnSign > 0 ? "RIGHT" : (turnSign < 0 ? "LEFT" : "STRAIGHT");
            currentPitch = VelocityTicketManager.getPlayerPitch(player.getUUID());
            currentVDelta = VelocityTicketManager.getPlayerVerticalDelta(player.getUUID());
            playerQuota = VelocityTicketManager.getPlayerQuota(player.getUUID());
        }

        final float pitch = currentPitch;
        final double verticalDelta = currentVDelta;
        final String pitchState = pitch < -10.0f ? "§c[DIVE]§r" : (pitch > 10.0f ? "§b[CLIMB]§r" : "§7[LEVEL]§r");
        final int quota = playerQuota;
        final int serverBudget = VelocityRenderGameRules.getServerTicketBudget(level);
        final int totalServerTickets = VelocityTicketManager.getTotalServerTickets();
        final int activeFlyers = VelocityTicketManager.getActiveFlyerCount();

        float serverMspt = (float) source.getServer().getAverageTickTimeNanos() / (float) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        float approxTps = Math.min(20.0f, 1000.0f / Math.max(1.0f, serverMspt));
        int dynamicReach = VelocityTicketManager.getLastDynamicReach();
        boolean clientBias = ClientVelocityTracker.activeBias;

        ResourceKey<Level> dimKey = level.dimension();
        String dimName = dimKey.identifier().toString();
        int clampPct = DimensionClampManager.getEffectiveClampPct(level);
        int maxReachInDim = DimensionReachScaler.calculateClampedReach(16, clampPct);

        final int tickets = activeTickets;
        final double speed = currentSpeed;
        final float mspt = serverMspt;
        final float tps = approxTps;
        final int reach = dynamicReach;
        final boolean bias = clientBias;
        final boolean widening = turnWidening;
        final float rate = turnRate;
        final String dir = direction;
        final String currentDimName = dimName;
        final int currentClampPct = clampPct;
        final int currentMaxReach = maxReachInDim;

        boolean lodHooks = VelocityRenderGameRules.isLodTrajectoryHooksEnabled(level);
        String lodSummary = LODCompatManager.getIntegrationSummary();
        LODTrajectoryState traj = LODCompatManager.getActiveTrajectory();
        double lodLead = (traj != null && traj.active()) ? traj.leadDistance() : 0.0;
        String lodStateStr = (traj != null && traj.active()) ? "§a[TRANSMITTING]" : "§7[IDLE]";

        final boolean lodEnabled = lodHooks;
        final String currentLodSummary = lodSummary;
        final double currentLodLead = lodLead;
        final String currentLodState = lodStateStr;

        source.sendSuccess(() -> Component.literal(
                "§6[Velocity Render — Engine Telemetry]§r\n" +
                " §7• §fStatus: " + (enabled ? "§aACTIVE" : "§cDISABLED") + "§r\n" +
                " §7• §fDimension Lookahead: §e" + currentDimName + " §7(Clamp: " + currentClampPct + "% -> max " + currentMaxReach + " chunks)§r\n" +
                " §7• §fYour Velocity: §a" + String.format("%.2f", speed) + " b/t (" + String.format("%.1f", speed * 20.0) + " m/s)§r\n" +
                " §7• §fDynamic Forward Reach: §e" + reach + " Chunks§r\n" +
                (source.getEntity() instanceof ServerPlayer player ? " §7• §fYour Active Lookahead: §e" + VelocityTicketManager.getPlayerDynamicReach(player.getUUID()) + " chunks§r\n" : "") +
                " §7• §fActive Trajectory Tickets: §b" + tickets + " (Zero-Alloc Reusable Scratch)§r\n" +
                (source.getEntity() instanceof ServerPlayer ? " §7• §fYour Allocated Quota: §e" + quota + " tickets§r\n" : "") +
                " §7• §fServer Ticket Budget: §e" + totalServerTickets + "/" + serverBudget + " §7(§b" + activeFlyers + "§7 active flyers)§r\n" +
                " §7• §fServer MSPT Load: §f" + String.format("%.1f", mspt) + " ms §7(§a" + String.format("%.1f", tps) + " TPS§7)§r\n" +
                " §7• §fClient Mesh Bias: " + (bias ? "§aCOMPILING FORWARD" : "§7IDLE") + "§r\n" +
                " §7• §fForward Lead Multiplier: §e" + leadMultiplier + "%§r\n" +
                " §7• §fMin Speed Threshold: §b" + String.format("%.2f", minSpeed) + " b/t (" + String.format("%.1f", minSpeed * 20.0) + " m/s)§r\n" +
                " §7• §fBudget Conservation: " + (budget ? "§aON" : "§7OFF") + "§r\n" +
                " §7• §fBanked Turn Widening: " + (widening ? "§aON" : "§7OFF") + "§r\n" +
                " §7• §fYour Angular Turn Rate: §b" + String.format("%.2f", rate) + " deg/tick §7(" + dir + ")§r\n" +
                " §7• §fVertical Lookahead: " + (verticalLookahead ? "§aON" : "§7OFF") + "§r\n" +
                " §7• §fPitch & Trajectory: §b" + String.format("%.1f", pitch) + "° §7" + pitchState + " (" + String.format("%+.2f", verticalDelta) + " b/t)§r\n" +
                " §7• §fF3 Diagnostic Telemetry: " + (f3Debug ? "§aON" : "§7OFF") + "§r\n" +
                " §7• §fLOD Trajectory Hooks: " + (lodEnabled ? "§aENABLED" : "§7DISABLED") + "§r\n" +
                "   §7↳ §fDetected Adapters: §b" + currentLodSummary + "§r\n" +
                "   §7↳ §fLead Broadcast: §e" + String.format("%.1f", currentLodLead) + " blocks " + currentLodState + "§r\n" +
                " §7• §fDebug Diagnostics: " + (debug ? "§aON" : "§7OFF")
        ), false);
        return 1;
    }

    private static int executeGet(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        String rule = StringArgumentType.getString(context, "rule").toLowerCase();

        switch (rule) {
            case "enabled" -> source.sendSuccess(() -> Component.literal("§6velocityrender:enabled = §e" + VelocityRenderGameRules.isEnabled(level)), false);
            case "lead_multiplier" -> source.sendSuccess(() -> Component.literal("§6velocityrender:lead_multiplier = §e" + VelocityRenderGameRules.getLeadMultiplierPct(level) + "%"), false);
            case "budget_conservation" -> source.sendSuccess(() -> Component.literal("§6velocityrender:budget_conservation = §e" + VelocityRenderGameRules.isBudgetConservation(level)), false);
            case "min_speed" -> source.sendSuccess(() -> Component.literal("§6velocityrender:min_speed = §e" + VelocityRenderGameRules.getMinSpeedThreshold(level) + " b/t"), false);
            case "turn_widening", "turn" -> source.sendSuccess(() -> Component.literal("§6velocityrender:turn_widening = §e" + VelocityRenderGameRules.isTurnWideningEnabled(level)), false);
            case "vertical_lookahead", "vertical" -> source.sendSuccess(() -> Component.literal("§6velocityrender:vertical_lookahead = §e" + VelocityRenderGameRules.isVerticalLookaheadEnabled(level)), false);
            case "server_ticket_budget", "budget" -> source.sendSuccess(() -> Component.literal("§6velocityrender:server_ticket_budget = §e" + VelocityRenderGameRules.getServerTicketBudget(level)), false);
            case "debug_mode" -> source.sendSuccess(() -> Component.literal("§6velocityrender:debug_mode = §e" + VelocityRenderGameRules.isDebugMode(level)), false);
            case "f3_debug", "f3" -> source.sendSuccess(() -> Component.literal("§6velocityrender:f3_debug = §e" + VelocityRenderGameRules.isF3DebugEnabled(level)), false);
            case "nether_reach_clamp_pct", "nether_clamp" -> source.sendSuccess(() -> Component.literal("§6velocityrender:nether_reach_clamp_pct = §e" + VelocityRenderGameRules.getNetherReachClampPct(level) + "%"), false);
            case "default_dense_reach_clamp_pct", "dense_clamp" -> source.sendSuccess(() -> Component.literal("§6velocityrender:default_dense_reach_clamp_pct = §e" + VelocityRenderGameRules.getDefaultDenseReachClampPct(level) + "%"), false);
            case "lod_hooks", "lod" -> source.sendSuccess(() -> Component.literal("§6velocityrender:lod_trajectory_hooks = §e" + VelocityRenderGameRules.isLodTrajectoryHooksEnabled(level)), false);
            default -> source.sendFailure(Component.literal("§cUnknown setting: " + rule));
        }
        return 1;
    }

    private static int executeSetBool(CommandContext<CommandSourceStack> context, String rule, boolean value) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        switch (rule) {
            case "enabled" -> level.getGameRules().set(VelocityRenderGameRules.ENABLED, value, source.getServer());
            case "budget_conservation" -> level.getGameRules().set(VelocityRenderGameRules.BUDGET_CONSERVATION, value, source.getServer());
            case "turn_widening" -> level.getGameRules().set(VelocityRenderGameRules.TURN_WIDENING, value, source.getServer());
            case "vertical_lookahead" -> level.getGameRules().set(VelocityRenderGameRules.VERTICAL_LOOKAHEAD, value, source.getServer());
            case "debug_mode" -> level.getGameRules().set(VelocityRenderGameRules.DEBUG_MODE, value, source.getServer());
            case "f3_debug" -> level.getGameRules().set(VelocityRenderGameRules.F3_DEBUG, value, source.getServer());
            case "lod_hooks", "lod" -> level.getGameRules().set(VelocityRenderGameRules.LOD_TRAJECTORY_HOOKS, value, source.getServer());
        }

        source.sendSuccess(() -> Component.literal("§aUpdated velocityrender:" + rule + " to " + value), true);
        return 1;
    }

    private static int executeSetInt(CommandContext<CommandSourceStack> context, String rule, int value) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        switch (rule) {
            case "lead_multiplier" -> level.getGameRules().set(VelocityRenderGameRules.LEAD_MULTIPLIER, value, source.getServer());
            case "min_speed" -> level.getGameRules().set(VelocityRenderGameRules.MIN_SPEED_THRESHOLD_PCT, value, source.getServer());
            case "server_ticket_budget", "budget" -> level.getGameRules().set(VelocityRenderGameRules.SERVER_TICKET_BUDGET, value, source.getServer());
            case "nether_reach_clamp_pct" -> level.getGameRules().set(VelocityRenderGameRules.NETHER_REACH_CLAMP_PCT, value, source.getServer());
            case "default_dense_reach_clamp_pct" -> level.getGameRules().set(VelocityRenderGameRules.DEFAULT_DENSE_REACH_CLAMP_PCT, value, source.getServer());
        }

        source.sendSuccess(() -> Component.literal("§aUpdated velocityrender:" + rule + " to " + value), true);
        return 1;
    }

    private static int executeReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        level.getGameRules().set(VelocityRenderGameRules.ENABLED, true, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.LEAD_MULTIPLIER, 100, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.BUDGET_CONSERVATION, true, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.MIN_SPEED_THRESHOLD_PCT, 20, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.TURN_WIDENING, true, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.VERTICAL_LOOKAHEAD, true, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.SERVER_TICKET_BUDGET, 64, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.DEBUG_MODE, false, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.F3_DEBUG, true, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.NETHER_REACH_CLAMP_PCT, 60, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.DEFAULT_DENSE_REACH_CLAMP_PCT, 80, source.getServer());
        level.getGameRules().set(VelocityRenderGameRules.LOD_TRAJECTORY_HOOKS, true, source.getServer());

        source.sendSuccess(() -> Component.literal("§aReset all Velocity Render settings to defaults."), true);
        return 1;
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        DimensionClampManager.load();
        source.sendSuccess(() -> Component.literal("§aReloaded Velocity Render configuration and dimension clamps successfully."), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestDimensions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        builder.suggest("current");
        builder.suggest("minecraft:overworld");
        builder.suggest("minecraft:the_nether");
        builder.suggest("minecraft:the_end");
        if (context.getSource().getServer() != null) {
            for (ResourceKey<Level> key : context.getSource().getServer().levelKeys()) {
                builder.suggest(key.identifier().toString());
            }
        }
        return builder.buildFuture();
    }

    private static int executeDimClampSet(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String dimStr = StringArgumentType.getString(context, "dimension");
        int pct = IntegerArgumentType.getInteger(context, "percentage");
        if ("current".equalsIgnoreCase(dimStr)) {
            dimStr = source.getLevel().dimension().identifier().toString();
        }
        DimensionClampManager.setOverride(dimStr, pct);
        DimensionClampManager.save();
        String finalDimStr = dimStr;
        source.sendSuccess(() -> Component.literal("§aSet dimension reach clamp for §e" + finalDimStr + " §ato §e" + pct + "%§r"), true);
        return 1;
    }

    private static int executeDimClampRemove(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String dimStr = StringArgumentType.getString(context, "dimension");
        if ("current".equalsIgnoreCase(dimStr)) {
            dimStr = source.getLevel().dimension().identifier().toString();
        }
        DimensionClampManager.removeOverride(dimStr);
        DimensionClampManager.save();
        String finalDimStr = dimStr;
        source.sendSuccess(() -> Component.literal("§aRemoved custom reach clamp override for §e" + finalDimStr + "§r"), true);
        return 1;
    }

    private static int executeDimClampReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        DimensionClampManager.clearOverrides();
        DimensionClampManager.save();
        source.sendSuccess(() -> Component.literal("§aCleared all custom dimension clamp overrides. Reverted to GameRules and data tags."), true);
        return 1;
    }

    private static int executeDimClampList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Map<String, Integer> overrides = DimensionClampManager.getAllOverrides();
        if (overrides.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§6[Velocity Render]§r §7No custom dimension clamp overrides configured. All dimensions evaluate standard GameRules and data tags."), false);
            return 1;
        }
        StringBuilder sb = new StringBuilder("§6=== Velocity Render: Custom Dimension Clamps ===§r\n");
        for (Map.Entry<String, Integer> entry : overrides.entrySet()) {
            sb.append(" §7• §f").append(entry.getKey()).append(": §e").append(entry.getValue()).append("%§r\n");
        }
        String output = sb.toString().trim();
        source.sendSuccess(() -> Component.literal(output), false);
        return 1;
    }
}
