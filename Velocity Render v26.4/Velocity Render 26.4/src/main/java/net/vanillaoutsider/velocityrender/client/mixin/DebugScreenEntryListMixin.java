// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import java.util.Map;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.ResourceKey;
import net.vanillaoutsider.velocityrender.client.VelocityRenderDebugEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntryList.class)
public abstract class DebugScreenEntryListMixin {

    @Shadow
    @Final
    private Map<ResourceKey<DebugScreenEntry>, DebugScreenEntryStatus> allStatuses;

    @Inject(method = "rebuildCurrentList", at = @At("HEAD"))
    private void velocityrender$ensureDefaultStatus(CallbackInfo ci) {
        this.allStatuses.putIfAbsent(VelocityRenderDebugEntry.KEY, DebugScreenEntryStatus.IN_OVERLAY);
    }
}
