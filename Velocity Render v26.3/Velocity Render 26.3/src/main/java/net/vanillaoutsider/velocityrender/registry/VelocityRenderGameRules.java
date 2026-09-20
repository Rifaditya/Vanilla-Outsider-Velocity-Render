// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.registry;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VelocityRenderGameRules {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityRenderGameRules.class);

    public static final GameRuleCategory CATEGORY = DynamicGameRuleManager.registerCategory(
            Identifier.fromNamespaceAndPath("velocity-render", "main")
    );

    public static GameRule<Boolean> ENABLED;
    public static GameRule<Integer> LEAD_MULTIPLIER;
    public static GameRule<Boolean> BUDGET_CONSERVATION;
    public static GameRule<Integer> MIN_SPEED_THRESHOLD_PCT;
    public static GameRule<Boolean> TURN_WIDENING;
    public static GameRule<Boolean> VERTICAL_LOOKAHEAD;
    public static GameRule<Boolean> DEBUG_MODE;

    private VelocityRenderGameRules() {
    }

    public static void register() {
        LOGGER.info("[VelocityRender] Registering dynamic namespaced GameRules via DasikLibrary");

        ENABLED = DynamicGameRuleManager.booleanRule("velocityrender:enabled", CATEGORY, true)
                .name("Enabled")
                .description("Toggle anisotropic forward chunk loading and meshing priority")
                .register();

        LEAD_MULTIPLIER = DynamicGameRuleManager.integerRule("velocityrender:lead_multiplier", CATEGORY, 100)
                .range(0, 300)
                .name("Forward Lead Multiplier")
                .description("Scaling percentage of forward lookahead reach (0% - 300%)")
                .register();

        BUDGET_CONSERVATION = DynamicGameRuleManager.booleanRule("velocityrender:budget_conservation", CATEGORY, true)
                .name("Budget Conservation")
                .description("Trim rear and lateral non-simulation tickets at high speed")
                .register();

        MIN_SPEED_THRESHOLD_PCT = DynamicGameRuleManager.integerRule("velocityrender:min_speed_threshold_pct", CATEGORY, 20)
                .range(1, 200)
                .name("Min Speed Threshold")
                .description("Minimum speed percentage required to activate forward prioritization (20 = 0.20 b/t)")
                .register();

        TURN_WIDENING = DynamicGameRuleManager.booleanRule("velocityrender:turn_widening", CATEGORY, true)
                .name("Banked Turn Widening")
                .description("Dynamically widens chunk generation corridor during sharp turns to prevent outer-curve void pop-in")
                .register();

        VERTICAL_LOOKAHEAD = DynamicGameRuleManager.booleanRule("velocityrender:vertical_lookahead", CATEGORY, true)
                .name("Vertical Lookahead")
                .description("Incorporate vertical pitch velocity into chunk meshing and altitude corridor updates during dives and climbs")
                .register();

        DEBUG_MODE = DynamicGameRuleManager.booleanRule("velocityrender:debug_mode", CATEGORY, false)
                .name("Debug Diagnostics")
                .description("Print real-time speed and trajectory diagnostics to logs")
                .register();
    }

    public static boolean isEnabled(Level level) {
        return DynamicGameRuleManager.getBoolean(level, ENABLED);
    }

    public static int getLeadMultiplierPct(Level level) {
        return DynamicGameRuleManager.getInt(level, LEAD_MULTIPLIER);
    }

    public static boolean isBudgetConservation(Level level) {
        return DynamicGameRuleManager.getBoolean(level, BUDGET_CONSERVATION);
    }

    public static double getMinSpeedThreshold(Level level) {
        return DynamicGameRuleManager.getPct(level, MIN_SPEED_THRESHOLD_PCT);
    }

    public static boolean isTurnWideningEnabled(Level level) {
        return DynamicGameRuleManager.getBoolean(level, TURN_WIDENING);
    }

    public static boolean isVerticalLookaheadEnabled(Level level) {
        return DynamicGameRuleManager.getBoolean(level, VERTICAL_LOOKAHEAD);
    }

    public static boolean isDebugMode(Level level) {
        return DynamicGameRuleManager.getBoolean(level, DEBUG_MODE);
    }
}
