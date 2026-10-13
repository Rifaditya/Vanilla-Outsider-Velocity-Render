// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.vanillaoutsider.velocityrender.math.VelocityVectorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public abstract class CompileTaskDynamicQueueMixin {

    @Shadow
    public abstract BlockPos getOrigin();

    @Inject(method = "getDistToPlayerSqr", at = @At("HEAD"), cancellable = true)
    private void velocityrender$biasedDistance(CallbackInfoReturnable<Double> cir) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera != null) {
            cir.setReturnValue(VelocityVectorHelper.calculateBiasedDistanceSqr(this.getOrigin(), camera.getPosition()));
        }
    }
}
