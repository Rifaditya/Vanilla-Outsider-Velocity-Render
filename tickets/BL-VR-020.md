# 📋 [BL-VR-020] Adapt F3 Debug Screen Overlay to Minecraft 26.4 Modular Debug API

- **Ticket ID**: `[BL-VR-020]`
- **Category**: `[TECH_DEBT]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 26.4 Anchor`
- **Scope**: `Client Rendering & F3 Debug Overlay`
- **Status**: `📋 DEFERRED`
- **Governance Mandate**: Studio Constitution Section 1 (*Vanilla Engine Primacy & Mixin Stability Law*)

---

## 🔍 1. Problem Statement & Root Cause
In Minecraft 26.4 (verified against decompiled `26.4 snapshot 5`), Mojang performed a complete architectural overhaul of the F3 Debug Screen:
1. In MC 26.3 and prior, debug screen lines were extracted as plain strings through `DebugScreenOverlay.extractLines(GuiGraphicsExtractor, List<String>, boolean, int, CallbackInfo)`.
2. In MC 26.4, **`extractLines` was deleted entirely**. Mojang replaced it with a structured component architecture:
   ```java
   net.minecraft.client.gui.components.debug.DebugScreenEntries
   net.minecraft.client.gui.components.debug.DebugGroup
   net.minecraft.client.gui.components.debug.DebugFact
   net.minecraft.client.gui.components.debug.DebugColumn
   ```
3. **Current Failure Mode**: Velocity Render's [`DebugScreenOverlayMixin.java`](file:///e:/Minecraft%20Project/Vanilla%20Outsider%20Collections/Velocity%20Render/Velocity%20Render%20v26.3/Velocity%20Render%2026.3/src/main/java/net/vanillaoutsider/velocityrender/client/mixin/DebugScreenOverlayMixin.java) injects directly into `extractLines`. When executed on Minecraft 26.4, Fabric Loader throws an `InvalidMixinException: Target method extractLines was not found in DebugScreenOverlay`, resulting in an instant crash on game launch.

---

## 🛠️ 2. Architectural Blueprint & Target Refactoring
When porting or releasing Velocity Render for MC 26.4:
1. **Retire Legacy String Extraction**: Remove the `@Inject(method = "extractLines")` hook from `DebugScreenOverlayMixin.java` for the 26.4 version branch.
2. **Hook Mojang's New Component Registry**:
   - Integrate Velocity Render's metric line into Mojang's new `DebugScreenEntries` / `DebugGroup` system.
   - Alternatively, inject into `DebugScreenDisplayer` or register a custom `DebugFact` / `DebugEntry` adhering to the new 26.4 F3 rendering lifecycle.
3. **Graceful Fallback**: Ensure that when F3 debug display is toggled off (`ClientVelocityTracker.isF3DebugEnabled() == false`), zero overhead or allocations occur on the rendering hot path.

---

## ⚓ 3. Multi-Era Parity Specifications
- **Legacy & 26.1–26.3 Anchors**: Retain the existing `extractLines` Mixin for MC 1.20.1 through MC 26.3 (where `extractLines` remains valid).
- **26.4+ Anchor**: Branch the Mixin or condition the hook specifically for MC 26.4+.

---

## 🛡️ 4. Edge Cases & Side-Safety
- **Client-Side Guarding**: Ensure all `DebugScreenEntries` and `DebugFact` classes remain strictly under `@Environment(EnvType.CLIENT)` or client subpackages to prevent dedicated server classloading issues.
- **Null Safety**: Validate `minecraft.debugEntries` before querying status to prevent NPE during early HUD initialization.

---

## 📋 5. Implementation Steps
1. Create/update the 26.4 subproject for Velocity Render once Loom mappings are finalized.
2. Implement the new F3 `DebugFact` / `DebugEntry` registration in client initialization.
3. Verify via headless unit tests and live client launch on 26.4.
4. Update `CHANGELOG.md` and release queue entries.

---

## ✅ 6. Verification Checklist
- [ ] No references to deleted `extractLines` method on MC 26.4.
- [ ] Velocity metric properly renders in F3 overlay under 26.4.
- [ ] Client boots cleanly on MC 26.4 with zero Mixin transform errors.
- [ ] Dedicated server boots cleanly without client HUD classloading crashes.
