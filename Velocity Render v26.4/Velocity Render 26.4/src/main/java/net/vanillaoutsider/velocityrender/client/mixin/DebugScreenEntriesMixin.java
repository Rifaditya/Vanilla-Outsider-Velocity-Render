// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.Registry;
import net.vanillaoutsider.velocityrender.client.VelocityRenderDebugEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenEntries.class)
public abstract class DebugScreenEntriesMixin {

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void velocityrender$registerDebugEntry(Registry<DebugScreenEntry> registry, CallbackInfoReturnable<Object> cir) {
        Registry.register(registry, VelocityRenderDebugEntry.KEY, new VelocityRenderDebugEntry());
    }
}
