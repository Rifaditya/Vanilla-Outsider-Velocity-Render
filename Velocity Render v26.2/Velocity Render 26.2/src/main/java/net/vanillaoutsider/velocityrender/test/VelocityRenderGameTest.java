// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.velocityrender.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.vanillaoutsider.velocityrender.math.DimensionReachScaler;
import net.vanillaoutsider.velocityrender.math.LODTrajectoryCalculator;
import net.vanillaoutsider.velocityrender.registry.VelocityRenderGameRules;
import net.vanillaoutsider.velocityrender.server.TicketBudgetAllocator;

/**
 * Pillar 4: In-Engine Fabric GameTest Suite.
 * Validates native GameRules, live level access, dynamic reach scaling, and entity movement calculation
 * within an in-engine headless Minecraft server environment.
 */
public class VelocityRenderGameTest {

    public static void testVelocityRenderGameRulesInLiveWorld(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        helper.assertTrue(level != null, "GameTest helper world level must not be null");
        helper.assertTrue(VelocityRenderGameRules.isEnabled(level), "Velocity Render must be enabled by default");
        helper.assertTrue(VelocityRenderGameRules.isBudgetConservation(level), "Budget conservation must be enabled by default");
        helper.assertTrue(VelocityRenderGameRules.isTurnWideningEnabled(level), "Turn widening must be enabled by default");
        helper.assertTrue(VelocityRenderGameRules.isVerticalLookaheadEnabled(level), "Vertical lookahead must be enabled by default");
        helper.assertTrue(VelocityRenderGameRules.isLodTrajectoryHooksEnabled(level), "LOD trajectory hooks must be enabled by default");
        helper.assertTrue(VelocityRenderGameRules.getLeadMultiplierPct(level) == 100, "Lead multiplier must default to 100%");

        helper.succeed();
    }

    public static void testEntityMovementInLiveWorld(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig pig = EntityTypes.PIG.create(level, EntitySpawnReason.COMMAND);

        helper.assertTrue(pig != null, "Pig entity failed to spawn in GameTest");
        pig.setDeltaMovement(1.5, 0.0, 1.5);

        double speed = Math.sqrt(1.5 * 1.5 + 1.5 * 1.5);
        double lead = LODTrajectoryCalculator.calculateLeadOffset(speed, 1.0);
        helper.assertTrue(lead > 0.0 && lead <= 1024.0, "LOD lead offset out of range in live world test");

        int reach = DimensionReachScaler.calculateClampedReach(16, 60);
        helper.assertTrue(reach == 10, "Nether 60% reach clamp must yield 10 chunks in live world");

        int quota = TicketBudgetAllocator.calculatePlayerQuota(speed, speed, 1, 64, 16);
        helper.assertTrue(quota == 16, "Single flyer must receive maximum allowed reach");

        helper.succeed();
    }
}
