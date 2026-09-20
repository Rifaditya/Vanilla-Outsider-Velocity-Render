// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;
import net.vanillaoutsider.velocityrender.client.VelocityRenderDebugEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractLines", at = @At("HEAD"))
    private void velocityrender$injectRightDebugMetricLine(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, int scaledScreenWidth, CallbackInfo ci) {
        if (!alignLeft) {
            if (!ClientVelocityTracker.isF3DebugEnabled()) {
                return;
            }
            if (this.minecraft.debugEntries.isCurrentlyEnabled(VelocityRenderDebugEntry.ID)) {
                String cachedLine = ClientVelocityTracker.getCachedF3Line();
                if (cachedLine != null) {
                    lines.add(0, cachedLine);
                }
            }
        }
    }
}
