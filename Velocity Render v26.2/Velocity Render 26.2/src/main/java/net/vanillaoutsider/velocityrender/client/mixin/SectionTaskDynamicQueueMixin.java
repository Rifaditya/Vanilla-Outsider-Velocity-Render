// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.vanillaoutsider.velocityrender.math.VelocityVectorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionTaskDynamicQueue.class)
public abstract class SectionTaskDynamicQueueMixin {

    @Redirect(
            method = "poll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;distToCenterSqr(Lnet/minecraft/core/Position;)D"
            )
    )
    private double velocityRender$biasedDistance(BlockPos origin, Position cameraPos) {
        return VelocityVectorHelper.calculateBiasedDistanceSqr(origin, cameraPos);
    }
}
