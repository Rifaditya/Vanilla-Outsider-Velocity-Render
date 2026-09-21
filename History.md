# 🏛️ Technical History & Architecture Ledger: Velocity Render

## [1.7.1+26.3] - 12-Language GUI Localization Parity (BL-VR-007 Step 2)
- Added full suite of 26 translatable GUI keys (`config.velocityrender.*`) to master `en_us.json`:
  - Screen title: `config.velocityrender.title`
  - 3 Category headers: `category.flight`, `category.server`, `category.telemetry`
  - 11 configuration options matching `VelocityRenderConfig` fields with rich player-friendly description tooltips.
- Synchronized all 11 non-English language files (`de_de.json`, `es_es.json`, `fr_fr.json`, `id_id.json`, `it_it.json`, `ja_jp.json`, `ko_kr.json`, `pt_br.json`, `ru_ru.json`, `zh_cn.json`, `zh_tw.json`) with native translations.
- Verified 100% key parity: all 12 files possess exactly 58 translation keys with zero missing keys or syntax errors.

## [1.7.0+26.3] - Optional GUI Dependencies & Config Model (BL-VR-007 Step 1)
- Added Maven repository `https://maven.isxander.dev/releases` to `build.gradle`.
- Declared `compileOnly` dependencies for `dev.isxander:yet-another-config-lib:3.9.5+26.2-fabric` and `com.terraformersmc:modmenu:18.0.0-beta.1`, ensuring zero runtime bytecode bundling in the distributed JAR.
- Created `VelocityRenderConfig` in `net.vanillaoutsider.velocityrender.client.config`:
  - Enforces persistent JSON storage at `config/velocity-render/config.json`.
  - Implemented `copyFrom(VelocityRenderConfig)` to preserve singleton object identity across disk reload cycles.
  - Implemented resilient fallback handling in `loadFromPath(...)` and `saveToPath(...)` with environment-safe `getDefaultConfigPath()` decoupling headless tests from FabricLoader.
- Established automated test suite `VelocityRenderConfigTest` asserting default parameters, full round-trip GSON serialization, and corrupt file self-healing.

## [1.6.4+26.3] - Brigadier Command Controls & Telemetry (BL-VR-006 Finale)
- Integrated LOD trajectory hooks into Brigadier command structure in `VelocityRenderCommand`:
  - Added suggestions for `lod_hooks` and alias `lod` under `/vr get <rule>`.
  - Added subcommands for `lod_hooks` and `lod` accepting booleans under `/vr set <rule> <val>`, updating `VelocityRenderGameRules.LOD_TRAJECTORY_HOOKS`.
  - Added query handling in `executeGet` returning current boolean state of `velocityrender:lod_trajectory_hooks`.
  - Added reset handling in `executeReset`, reverting `LOD_TRAJECTORY_HOOKS` to `true`.
- Added multi-line LOD telemetry section to `/vr status`:
  - Displays master hook state (`§aENABLED` / `§7DISABLED`).
  - Displays detected adapters via `LODCompatManager.getIntegrationSummary()` (Distant Horizons, Bobby, or Vanilla Meshing).
  - Displays real-time lookahead lead distance broadcast and transmission state (`§a[TRANSMITTING]` / `§7[IDLE]`).
- Backlog Resolution: Officially closed out `[BL-VR-006]` (Bobby & Distant Horizons LOD Velocity Trajectory Hooks) with all acceptance criteria verified.

## [1.6.3+26.3] - Client Tick Integration & Broadcasting (BL-VR-006 Step 4)
- Integrated `LODCompatManager.init()` into `VelocityRenderClient.onInitializeClient()`, executing mod detection on client startup.
- Updated `ClientVelocityTracker` to broadcast real-time trajectory updates during `clientTick(...)`:
  - Added dual-layer gating: verifies client toggle `clientLodHooksEnabled` and `VelocityRenderGameRules.isLodTrajectoryHooksEnabled(mc.level)`.
  - Implemented edge-triggered state update: while moving (`speed >= minSpeedThreshold`), generates `LODTrajectoryState` and broadcasts to `LODCompatManager.updateTrajectory(state)`.
  - Implemented falling-edge idle reset: when transitioning from moving to idle, dispatches `LODTrajectoryState.INACTIVE` once and sleeps, eliminating redundant tick execution.
  - Added public accessors `isClientLodHooksEnabled()`, `setClientLodHooksEnabled(boolean)`, and `isLodHooksEnabled()`.

## [1.6.2+26.3] - Soft-Reflection LOD Compatibility Hub & Public API (BL-VR-006 Step 3)
- Implemented `LODCompatManager` in `net.vanillaoutsider.velocityrender.client.compat`:
  - Enforces strict classloader isolation by verifying `FabricLoader.getInstance().isModLoaded(...)` before loading any adapter classes.
  - Instantiates isolated adapter instances `DistantHorizonsAdapter` and `BobbyAdapter` from `compat.adapter` only when the respective mod is present.
  - Maintains lock-free volatile snapshot `currentTrajectory` with zero heap allocation on updates.
  - Formats live diagnostic summaries for F3 and in-game commands via `getIntegrationSummary()`.
- Created `DistantHorizonsAdapter` in `net.vanillaoutsider.velocityrender.client.compat.adapter`:
  - Reflectively connects to `DhApi` and injects forward lookahead focus coordinates and trajectory cones.
- Created `BobbyAdapter` in `net.vanillaoutsider.velocityrender.client.compat.adapter`:
  - Reflectively inspects Bobby and supplies forward trajectory vectors for cache reading prioritization.
- Created `VelocityTrajectoryAPI` in `net.vanillaoutsider.velocityrender.client.compat`:
  - Exposes public static query methods `getActiveTrajectory()`, `isTrajectoryActive()`, `getLookaheadFocus()`, `calculateBiasedDistanceSqr(...)`, and `getIntegrationSummary()`.
- Added comprehensive unit test suite in `LODCompatManagerTest` (5 tests verifying default inactive states, absence handling, mock integration summaries, null safety, and distance ordering).

## [1.6.1+26.3] - Dynamic GameRule & 12-Language Localization Parity (BL-VR-006 Step 2)
- Declared and registered dynamic boolean GameRule `LOD_TRAJECTORY_HOOKS` (`velocityrender:lod_trajectory_hooks`, default: `true`) in `VelocityRenderGameRules`.
- Added public static accessor `VelocityRenderGameRules.isLodTrajectoryHooksEnabled(Level)` providing null-safe boolean evaluation.
- Added comprehensive localized names and detailed descriptions across all 12 supported languages (`en_us`, `de_de`, `es_es`, `fr_fr`, `id_id`, `it_it`, `ja_jp`, `ko_kr`, `pt_br`, `ru_ru`, `zh_cn`, `zh_tw`).

## [1.6.0+26.3] - Pure LOD Trajectory Math Engine (BL-VR-006 Step 1)
- Created `LODTrajectoryCalculator` in `net.vanillaoutsider.velocityrender.math`:
  - Implemented `calculateLeadOffset(double speed, double leadMultiplier)` scaling linearly (`speed * 256.0 * leadMultiplier`) and clamping at `MAX_LOD_LEAD_OFFSET = 1024.0` blocks.
  - Implemented `calculateLookaheadFocus(...)` extrapolating 3D focus coordinates along the normalized velocity vector.
  - Implemented `calculateBiasedLODDistanceSqr(...)` calculating anisotropic directional distance squared matching `VelocityVectorHelper` for consistent chunk priority ordering across LOD grids.
  - Created immutable data carrier `LODTrajectoryState` record with chunk coordinate accessors (`focusChunkX()`, `focusChunkZ()`, `camChunkX()`, `camChunkZ()`) and pre-allocated `INACTIVE` singleton.
- Implemented defensive zero-crash guards against NaN/infinite values, sub-threshold velocities, and negative multipliers.
- Added comprehensive unit test suite in `LODTrajectoryCalculatorTest` (8 tests covering stationary, sub-threshold, supersonic speeds, 3D dives, priority distance ordering, negative multipliers, and NaN resistance).

## [1.5.4+26.3] - Brigadier Command Controls & Telemetry (BL-VR-005 Finale)
- Implemented dedicated `/vr dimclamp` subcommand suite in `VelocityRenderCommand`:
  - `/vr dimclamp <dimension> <percentage>`: Saves Sparse Delta JSON overrides via `DimensionClampManager.setOverride` and `save()`.
  - `/vr dimclamp remove <dimension>`: Deletes specific override via `DimensionClampManager.removeOverride` and `save()`.
  - `/vr dimclamp reset`: Clears all overrides via `DimensionClampManager.clearOverrides` and `save()`.
  - `/vr dimclamp list`: Pretty-prints active custom overrides or shows informative message when none are configured.
- Added dynamic dimension suggestions providing `"current"` and querying all registered level keys from `server.levelKeys()`.
- Bound `nether_reach_clamp_pct` (alias `nether_clamp`) and `default_dense_reach_clamp_pct` (alias `dense_clamp`) to `/vr get`, `/vr set`, and `/vr reset`.
- Enhanced `/vr status` with dedicated Dimension Lookahead readout showing active dimension ID, resolved clamp %, maximum corridor reach, and active player dynamic lookahead reach.
- Updated `/vr reload` to hot-reload `DimensionClampManager.load()`.
- Marked `[BL-VR-005]` as `✅ RESOLVED` in `BACKLOG.md` (preserving `[BL-VR-008]` as `🚧 IN_PROGRESS`).

## [1.5.3+26.3] - Server Predictive Generation Dimension Gating (BL-VR-005 Step 4)
- Integrated `DimensionClampManager.getEffectiveClampPct(level)` and `DimensionReachScaler.calculateClampedReach` into `VelocityTicketManager.tickPlayer` on the 20 TPS server tick path.
- Applied post-budget corridor reach clamping: evaluated after `TicketBudgetAllocator.calculatePlayerQuota`, allowing flyers in dense dimensions to consume fewer tickets than their allocated quota, freeing server capacity for other dimensions.
- Added `PLAYER_DYNAMIC_REACH` tracking map (`Object2IntOpenHashMap<UUID>`) with default return value `0` and cleanup on player disconnect/dimension change.
- Added public query accessors `VelocityTicketManager.getPlayerDynamicReach(UUID)` and `VelocityTicketManager.getEffectiveDimensionClamp(Level)`.
- Expanded `VelocityTicketManagerTest` verifying default query return values and end-to-end mathematical pipeline consistency across multiple dimensions and quota constraints.

## [1.5.2+26.3] - Dynamic Sparse Delta & Conventional Tag Hub (BL-VR-005 Step 3)
- Implemented `DimensionClampManager` in `net.vanillaoutsider.velocityrender.server` implementing the Omni-Channel Configuration Hierarchy.
- Evaluates: (1) Active GameRule overrides, (2) Sparse Delta JSON in `config/velocity-render/dimension_clamps.json`, (3) Data-driven `#c:dense_dimensions` & `#velocity-render:dense_dimensions` tags, (4) Nether baseline (60%), and (5) Overworld/End default baseline (100%).
- Backed by FastUtil `Object2IntOpenHashMap` with `-1` default return value for zero heap allocations and O(1) primitive lookups on server tick paths.
- Declared conventional dimension type tags `#c:dense_dimensions` and `#velocity-render:dense_dimensions` containing `minecraft:the_nether`.
- Integrated `DimensionClampManager.load()` into `VelocityRenderMod.onInitialize()`.
- Created comprehensive unit tests in `DimensionClampManagerTest` verifying persistence, clamping, override removal, and malformed JSON recovery.

## [1.5.1+26.3] - Dynamic GameRules & 12-Language Localization (BL-VR-005 Step 2)
- Declared and registered `NETHER_REACH_CLAMP_PCT` (`velocityrender:nether_reach_clamp_pct`, default: 60, range: [10, 100]) and `DEFAULT_DENSE_REACH_CLAMP_PCT` (`velocityrender:default_dense_reach_clamp_pct`, default: 80, range: [10, 100]) in `VelocityRenderGameRules`.
- Added static accessor methods `getNetherReachClampPct(Level)` and `getDefaultDenseReachClampPct(Level)`.
- Synchronized translation dictionaries across all 12 supported languages (`en_us`, `de_de`, `es_es`, `fr_fr`, `id_id`, `it_it`, `ja_jp`, `ko_kr`, `pt_br`, `ru_ru`, `zh_cn`, `zh_tw`).

## [1.5.0+26.3] - Pure Scaling Math & Reach Scaler (BL-VR-005 Step 1)
- Created `DimensionReachScaler` pure stateless algorithmic utility in `net.vanillaoutsider.velocityrender.math`.
- Formula: `calculateClampedReach(int baseReach, int clampPct)` clamping `clampPct` to `[10, 100]` and returning `Math.max(2, Math.round(baseReach * (clampPct / 100.0)))`.
- Enforced minimum 2-chunk corridor floor when `baseReach >= 2` and safe `0` return on non-positive reach.
- Added comprehensive unit test suite in `DimensionReachScalerTest` (7 tests covering standard, moderate, dense, full reach, safety floor, and bounds).
- Updated `fabric.mod.json` license identifier to `GPL-3.0-or-later`.

## [1.4.3+26.3] - Command Suite Controls & Backlog Resolution (BL-VR-004 Finale)
- Added `f3_debug` and alias `f3` to `/vr get` and `/vr set` in `VelocityRenderCommand`.
- Integrated `F3_DEBUG` into `/vr reset` restoration defaults.
- Added F3 telemetry status line to `/vr status`.
- Marked `[BL-VR-004]` as `✅ RESOLVED` in `BACKLOG.md`.

## [1.4.2+26.3] - DebugScreenOverlay & DebugScreenEntries Mixin Integration (BL-VR-004 Step 3)
- Registered `VelocityRenderDebugEntry` into `DebugScreenEntries` via `DebugScreenEntriesMixin`.
- Ensured default `IN_OVERLAY` status in `DebugScreenEntryListMixin.rebuildCurrentList`.
- Injected telemetry line at index 0 of `rightLines` in `DebugScreenOverlayMixin.extractLines`.
- Made `VelocityRenderDebugEntry.display(...)` a no-op to prevent double-line rendering.

## [1.4.1+26.3] - 10-Tick Telemetry Caching, F3+F6 DebugScreenEntry & GameRule (BL-VR-004 Step 2)
- Created `VelocityRenderDebugEntry` (`velocity-render:engine_metrics`) under `DebugEntryCategory.SCREEN_TEXT`.
- Integrated 10-tick throttled telemetry computation in `ClientVelocityTracker`.
- Derived turning cone angle from client `TurnRateCalculator`.
- Registered `velocityrender:f3_debug` GameRule with 11-language translations.

## [1.4.0+26.3] - Diagnostic Formatter Math & State Engine (BL-VR-004 Step 1)
- Implemented `DebugMetricsFormatter` with `formatIntegrated` and `formatClientOnly`.
- Enforced 1-decimal float precision for watchdog load shedding (`0.0%`).
- Clamped active tickets ($\ge 0$), shed ($[0.0, 100.0]$), cone ($[0, 180]$), budget ($\ge 1$), and lead offset ($[0.0, 512.0]$).
- Added comprehensive unit tests in `DebugMetricsFormatterTest`.

## [1.3.3+26.3] - Diagnostic Command Suite & Multi-Player Telemetry (BL-VR-003 Step 4)
- Exposed `server_ticket_budget` and alias `budget` in `/vr get` and `/vr set`.
- Added server pool budget utilization and player quota readouts to `/vr status`.
- Integrated `SERVER_TICKET_BUDGET = 64` into `/vr reset`.
- Marked `[BL-VR-003]` as `✅ RESOLVED` in `BACKLOG.md`.

## [1.3.2+26.3] - Server-Wide Multi-Player Ticket Gating (BL-VR-003 Step 3)
- Implemented `tickServer` pre-pass to aggregate speeds and active flyer count.
- Clamped per-player reach with `TicketBudgetAllocator` quota.
- Added `getTotalServerTickets`, `getActiveFlyerCount`, and `getPlayerQuota` accessors.

## [1.3.1+26.3] - Dynamic Server Ticket Budget GameRule & Localization Parity (BL-VR-003 Step 2)
- Added `SERVER_TICKET_BUDGET` dynamic GameRule (default 64, range 16-256).
- Synchronized all 11 language translation dictionaries.
- Added `getServerTicketBudget(Level)` accessor.

## [1.3.0+26.3] - Fair Allocation Math & Ticket Quota Engine (BL-VR-003 Step 1)
- Implemented `TicketBudgetAllocator` with speed-weighted proportional allocation.
- Enforced dynamic safety floor protecting active flyers from total ticket starvation.
- Added comprehensive unit test suite in `TicketBudgetAllocatorTest`.

## [1.2.3+26.3] - Telemetry Diagnostics & Command Suite (BL-VR-002 Step 4)
- Added `vertical_lookahead` and `vertical` commands in `VelocityRenderCommand`.
- Integrated colored trajectory indicators into `/vr status`.
- Added 45-degree angled dive unit tests.

## [1.2.2+26.3] - Server Altitude Trigger & Vertical Gating (BL-VR-002 Step 3)
- Added `LAST_PLAYER_Y` cache and altitude delta recalculation trigger.
- Implemented `getPlayerVerticalDelta` and `getPlayerPitch` metrics.

## [1.2.1+26.3] - Dynamic Vertical Lookahead GameRule & Localization (BL-VR-002 Step 2)
- Added `VERTICAL_LOOKAHEAD` GameRule definition.
- Synchronized 11-language localization dictionary.

## [1.2.0+26.3] - Symmetrical 3D Pitch-Aware Lookahead (BL-VR-002 Step 1)
- Expanded `VelocityVectorHelper` with pitch bias verification methods.
- Verified symmetrical section prioritization for vertical dives and rocket climbs.

## [1.1.3+26.3] - Brigadier Command Suite & Diagnostics (BL-VR-001 Step 4)
- Exposed live `getPlayerTurnRate` and `getPlayerTurnSign` metrics in `/vr status`.
- Added `turn_widening` and `turn` arguments to Brigadier `/vr get` and `/vr set`.
- Validated trajectory transition and decay curves in `TurnRateCalculatorTest`.

## [1.1.2+26.3] - Server Predictive Arc Corridor Integration (BL-VR-001 Step 3)
- Bound `TurnRateCalculator` to `VelocityTicketManager` via `PLAYER_TURN_RATES` hash map.
- Implemented dynamic perpendicular lateral offset calculation along the turn radius.
- Linked with `velocityrender:turn_widening` GameRule and MSPT watchdog throttling.

## [1.1.1+26.3] - Dynamic Turn GameRule & Localization Parity (BL-VR-001 Step 2)
- Added `TURN_WIDENING` (`velocityrender:turn_widening`) boolean GameRule via `DynamicGameRuleManager`.
- Configured default to `true` with translatable display title and description.
- Populated full 11-language translation matrix in `assets/velocity-render/lang/`.
- Exposed `VelocityRenderGameRules.isTurnWideningEnabled(Level)` query method.

## [1.1.0+26.3] - Turn-Arc Math Engine (BL-VR-001 Step 1)
- Introduced `TurnRateCalculator` in `net.vanillaoutsider.velocityrender.math`.
- Computes shortest angular delta across wrap-around boundaries (`[-180.0, 180.0]`).
- Exponential Moving Average ($\alpha = 0.60$) smooths angular velocity $\omega$.
- 2-tier lateral fan-out step scaling: 1 chunk (moderate turn) or 2 chunks (sharp turn).
- Fully covered by unit tests in `TurnRateCalculatorTest`.

## [1.0.0+26.3] - Genesis Architecture
- Rebranded and rebuilt from ground-up as `velocity-render`.
- Package namespace established under `net.vanillaoutsider.velocityrender`.
- Client Mixin targets `net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue` at `poll()`.
- Distance bias algorithm uses: `d^2 - 2 * (v · r) * lambda`.
- Server ticket management uses FastUtil `LongOpenHashSet` and `ChunkPos.pack()` for primitive performance.
- Load throttling evaluates tick time against `TimeUtil.NANOSECONDS_PER_MILLISECOND`.
