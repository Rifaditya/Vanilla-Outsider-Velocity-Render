// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.vanillaoutsider.velocityrender.client.ClientVelocityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {

    @Inject(method = "renderLines", at = @At("HEAD"))
    private void velocityrender$injectRightDebugMetricLine(GuiGraphics graphics, List<String> lines, boolean alignLeft, CallbackInfo ci) {
        if (!alignLeft) {
            if (!ClientVelocityTracker.isF3DebugEnabled()) {
                return;
            }
            String cachedLine = ClientVelocityTracker.getCachedF3Line();
            if (cachedLine != null) {
                lines.add(0, cachedLine);
            }
        }
    }
}
