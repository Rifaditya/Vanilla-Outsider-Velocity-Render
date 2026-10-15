# 🎫 [BL-VR-018] Migrate Command Suite to Universal Brigadier Command Framework

- **Ticket ID**: `[BL-VR-018]`
- **Category**: `[TECH_DEBT]`
- **Priority**: `[MEDIUM]`
- **Target Version**: `All Anchors`
- **Scope**: `In-Game Commands & Brigadier Architecture`
- **Status**: `📌 DEFERRED`
- **Master Workspace Reference**: [`BL-WS-016`](file:///E:/Minecraft%20Project/tickets/BL-WS-016.md)

---

## 🎯 1. Problem Statement & Player Impact
Currently, **Velocity Render** maintains custom Brigadier command registration, argument parsers, feedback formatters, and GameRule reflection/dispatch logic. This creates repetitive boilerplate, maintenance friction across Minecraft version updates, and visual inconsistencies with studio chat formatting standards.

Following the release of `Dasik Library` v1.10.0 (Modern Lead) and v1.2.0 (Multi-Era Anchors), Velocity Render should adopt the centralized `net.dasik.social.api.command.*` framework.

## 🔬 2. Architectural Blueprint & Target Classes
1. **Dispatcher Registration**: Wire `ModCommandDispatcher.registerSuite(MOD_ID, builder -> ...)` in the mod's initializers across all active version anchors.
2. **Universal Tree Builder**: Construct standard subcommands (`help`, `status`, `get`, `set`, `reset`, `reload`) via `UniversalCommandRegistry.builder("velocityrender")`.
3. **Reactive GameRule Bridging**: Bind all Velocity Render GameRules and config keys using `GameRuleCommandBridge` with saturated numeric bounds.
4. **Standard Feedback & Footers**: Leverage `CommandFeedbackFormatter` for unified gold prefixes (`§6[Velocity Render]§r `), colored feedback levels, and clickable community footers (`DasikSupportHelper`).
5. **Boilerplate Purge**: Purge custom command boilerplate classes and redundant formatting helpers, simplifying the codebase.

## ⚓ 3. Multi-Era Anchor Parity Specifications
- Applicable across all active version anchors of Velocity Render in lockstep.
- Backward compatibility: Preserves all existing command aliases (`/velocityrender`, `/ve`), argument names, and permission levels (`source.hasPermission(2)` for admin mutations).

## 🛡️ 4. Edge Cases & Sidedness Safety
- Null source entity safety for console / RCON / command block execution.
- Strict sidedness: Zero `net.minecraft.client.*` imports in common code; Knot ClassLoader runtime safety.
- Freedom Over Anti-Crash: Full saturated numerical bounds with non-blocking warnings.

## 🚀 5. Implementation Steps
1. Pre-bump `gradle.properties` in targeted anchor(s).
2. Wire `ModCommandDispatcher.registerSuite` in mod initializer.
3. Replace custom command classes with `UniversalCommandRegistry` builder.
4. Verify via 5-Pillar Testing Harness (`*CommandTest` / Brigadier AST test).
5. Compile and deploy via the 5-Link Release Chain.

## 🏁 6. 5-Pillar Verification Checklist
- [ ] **Pillar 1 (Chaos Fuzzing)**: Fuzz command arguments with saturated inputs.
- [ ] **Pillar 2 (Concurrency Load)**: Multi-threaded command execution / bridge read test.
- [ ] **Pillar 3 (Brigadier AST)**: Complete parse tree and suggestion provider validation.
- [ ] **Pillar 4 (GameTest / Headless)**: Command execution assertions.
- [ ] **Pillar 5 (Sidedness & Hygiene Contract)**: 0 client imports in common command code; single-line GPLv3 headers.
