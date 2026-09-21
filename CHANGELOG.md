# 📜 Changelog: Velocity Render

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## [1.5.2+26.3] - Dynamic Dimension Clamping Persistence & Conventional Tags
- **Sparse Delta Dimension Config**: Introduced `config/velocity-render/dimension_clamps.json` providing clean Git/Packwiz compatibility without dumping unmodified defaults (Anti-Config Bombing).
- **Omni-Channel Configuration Hub**: Unified reach scaling resolution across live GameRules, JSON overrides, and data tags with zero heap allocation on the server tick path.
- **Data-Driven Conventional Tags**: Added support for `#c:dense_dimensions` and `#velocity-render:dense_dimensions` dimension type tags, enabling datapacks and modpacks to mark custom dimensions for lookahead clamping without writing code.
- **Fail-Safe Self-Healing Storage**: Automatic error recovery and schema versioning that safely falls back to defaults without corrupting existing configuration files.

---

## [1.5.1+26.3] - Dynamic Dimension Clamping GameRules & Localization Parity
- **Dynamic Dimension Clamp GameRules**: Registered `velocityrender:nether_reach_clamp_pct` (default: `60%`, range: `10% - 100%`) and `velocityrender:default_dense_reach_clamp_pct` (default: `80%`, range: `10% - 100%`) via DasikLibrary's dynamic registry.
- **Server Operator Tuning**: Enables server operators and singleplayer hosts to tune forward lookahead reach in the Nether and dense modded dimensions live in-game without server restarts.
- **12-Language Universal Localization**: Fully synchronized translations and descriptions across all 12 supported languages (English, German, Spanish, French, Indonesian, Italian, Japanese, Korean, Portuguese, Russian, Simplified Chinese, Traditional Chinese).
- **Safe Null-Gated Accessors**: Exposed zero-allocation static helper methods `getNetherReachClampPct(Level)` and `getDefaultDenseReachClampPct(Level)`.

---

## [1.5.0+26.3] - Dimension Reach Scaling Math Engine
- **Dimension Reach Scaler Utility**: Introduced pure mathematical calculation engine `DimensionReachScaler` to scale forward lookahead corridors across distinct dimensions.
- **Nether & Dense Dimension Protection**: Computes scaled corridor reaches for environments with high block densities and cave noise, clamping reach down to 60% in the Nether while preserving full lookahead in the Overworld.
- **Safety Corridor Floor**: Enforces an inviolable minimum 2-chunk lookahead corridor to guarantee continuous flight visibility even under aggressive clamping.
- **Zero-Allocation Hot-Path Execution**: Pure stateless implementation with 0 bytes allocated per server tick.
- **Automated Regression Suite**: Added `DimensionReachScalerTest` validating Nether, dense, and full-reach scaling along with underflow/overflow boundary clamping.

---

## [1.4.3+26.3] - In-Game Command Suite Controls & Diagnostic Milestone
- **Brigadier Command Suite Integration**: Added `f3_debug` and short alias `f3` to `/velocityrender get` and `/velocityrender set` with full tab completions.
- **Real-Time Telemetry Status**: Integrated F3 diagnostic status reporting directly into `/vr status`.
- **Reset Hygiene**: Updated `/vr reset` to restore `velocityrender:f3_debug` to `true`.
- **Engine Diagnostics Complete**: Concluded the background F3 engine telemetry feature across all targeted releases.

---

## [1.4.2+26.3] - F3 Debug Screen Overlay & F3+F6 Debug Options Integration
- **Native F3+F6 Debug Options Integration**: Registered engine diagnostic metrics into Minecraft 26.3's native Debug Options Screen (`F3 + F6`) under the Text Category (`DebugEntryCategory.SCREEN_TEXT`), defaulting to `IN_OVERLAY` status.
- **Prominent Top-Right Placement**: Injected the real-time flight telemetry readout at index 0 of the right-hand column (above Java and Memory hardware specs) for immediate visibility and zero interference.
- **0B Render Frame Allocations**: Telemetry line reads directly from the pre-formatted volatile reference on render ticks with zero heap allocations.

---

## [1.4.1+26.3] - 10-Tick Telemetry Caching, F3+F6 DebugScreenEntry & GameRule
- **Native F3+F6 Debug Screen Entry**: Created `VelocityRenderDebugEntry` (`velocity-render:engine_metrics`) registered under `DebugEntryCategory.SCREEN_TEXT`, integrating into Minecraft 26.3's native `F3 + F6` Debug Options Screen.
- **0B Render GC Cache**: Implemented 10-tick throttled telemetry update in `ClientVelocityTracker` refreshing `cachedF3Line` with zero heap allocations on the 60–240+ FPS render thread.
- **Dynamic Lookahead Turn Cone**: Derived turning cone angle dynamically from client angular turn rate (`coneDeg = min(90, round(angularRate * 10))`).
- **Dynamic GameRule & 11-Language Parity**: Registered `velocityrender:f3_debug` GameRule (default `true`) and synchronized translations across all 11 supported languages.
- **Clean Inactive State**: Keeps F3 completely clean during idle standing/walking by setting `cachedF3Line` to `null`.

---

## [1.4.0+26.3] - Diagnostic Formatter Math & State Engine
- **Diagnostic Formatter Utility**: Introduced pure stateless formatting engine `DebugMetricsFormatter` to assemble standardized F3 diagnostic telemetry strings.
- **Dual-Context Formats**: Supports `formatIntegrated` for singleplayer/integrated server (`[VelocityRender] Active Tickets: %d | Shed: %.1f%% | Cone: %d° | Pool: %d/%d`) and `formatClientOnly` for multiplayer dedicated servers (`[VelocityRender] Bias: %s | Lead: %.1fm | Cone: %d°`).
- **Precision & Clamping Protection**: Uses 1-decimal float precision for watchdog shedding, clamping tickets, cone angle ($[0, 180^\circ]$), budget ($\ge 1$), and lead offset ($[0.0, 512.0\text{m}]$).
- **Zero-Dependency High-Speed Assembly**: Built with lightweight `StringBuilder(64)` assembly for deterministic zero-garbage execution.
- **Automated Regression Test Suite**: Added `DebugMetricsFormatterTest` validating standard rendering, decimal shed formatting, boundary clamping, and negative input recovery.

---

## [1.3.3+26.3] - Diagnostic Command Suite & Multi-Player Telemetry
- **Multi-Player Ticket Telemetry**: Added live server ticket budget utilization (`X/Y` active tickets across `N` flyers) and per-player allocated quota metrics to `/velocityrender status` (`/vr status`).
- **Brigadier Command Controls**: Added `server_ticket_budget` and alias `budget` to `/vr get` and `/vr set <rule> <16-256>` with full tab suggestions.
- **Reset Hygiene**: Included `velocityrender:server_ticket_budget = 64` in `/vr reset` restoration defaults.
- **Milestone Complete**: Formally validated and resolved server ticket budgeting across all release cycles.

---

## [1.3.2+26.3] - Server-Wide Multi-Player Ticket Gating
- **Two-Phase Server Tick Architecture**: Integrated `VelocityTicketManager.tickServer` pre-pass to track active flyers and aggregate speeds across all connected players before individual ticks.
- **Fair Ticket Quota Enforcement**: Bound dynamic forward reach (`maxReachChunks`) to each player's fair quota calculated by `TicketBudgetAllocator`, preventing server chunk worker starvation.
- **Server Pool Telemetry Accessors**: Exposed `getTotalServerTickets()`, `getActiveFlyerCount()`, and `getPlayerQuota(UUID)` with zero heap allocation on 20 TPS hot paths.
- **Automated Regression Suite**: Expanded `VelocityTicketManagerTest` to verify default states for server pool telemetry.

---

## [1.3.1+26.3] - Dynamic Server Ticket Budget GameRule & Localization Parity
- **Dynamic GameRule Integration**: Registered `velocityrender:server_ticket_budget` integer rule (default `64`, range `[16, 256]`) via DasikLibrary's dynamic registry system.
- **Server Operator Control**: Enables dynamic live adjustment of total server-wide forward loading tickets allocated across all concurrent high-speed flyers.
- **11-Language Translation Parity**: Added native translations and detailed descriptions across all supported languages (English, German, Spanish, French, Indonesian, Italian, Japanese, Korean, Portuguese, Russian, Simplified Chinese, Traditional Chinese).

---

## [1.3.0+26.3] - Fair Allocation Math & Ticket Quota Engine
- **Fair Multi-Player Quota Engine**: Introduced pure algorithmic utility `TicketBudgetAllocator` computing speed-weighted proportional forward ticket quotas across concurrent flyers.
- **Dynamic Safety Floor**: Enforced a 4-ticket dynamic floor per active flyer to prevent corridor collapse during server budget saturation.
- **0B Hot-Path Allocations**: Pure static mathematical formulation running with zero heap allocations on 20 TPS tick paths.
- **Automated Regression Test Suite**: Added comprehensive unit test coverage in `TicketBudgetAllocatorTest` validating single flyer, symmetrical split, asymmetrical weighting, and extreme 20-flyer saturation clamping.

---

## [1.2.3+26.3] - Telemetry Diagnostics & Command Suite
- **3D Trajectory Telemetry**: Added real-time pitch angle readout, vertical delta speed ($v_y$ b/t), and color-coded flight state indicators (`[DIVE]`, `[CLIMB]`, `[LEVEL]`) to `/velocityrender status` (`/vr status`).
- **Brigadier Command Controls**: Added `vertical_lookahead` and `vertical` command parameters to `/vr get` and `/vr set` with full tab completions.
- **Reset Hygiene**: Included `velocityrender:vertical_lookahead` in `/vr reset` defaults.
- **45-Degree Angled Dive Verification**: Expanded `VelocityVectorHelperTest` with diagonal 3D trajectory prioritization tests.
- **Milestone Complete**: Formally resolved and validated 3D lookahead across all release cycles.

---

## [1.2.2+26.3] - Server Altitude Trigger & Vertical Gating
- **Vertical Altitude Recalculation Trigger**: Enabled rapid corridor recalculation when altitude changes by 8+ blocks ($|\Delta Y| \ge 8.0$), preventing chunk pop-in during steep Elytra dives from build height.
- **Dynamic GameRule Gating**: Bound vertical recalculation directly to `velocityrender:vertical_lookahead`.
- **Pitch & Vertical Delta Telemetry Accessors**: Added `getPlayerVerticalDelta` and `getPlayerPitch` query methods to `VelocityTicketManager`.

---

## [1.2.1+26.3] - Dynamic Vertical Lookahead GameRule & Localization
- **Dynamic GameRule Integration**: Registered `velocityrender:vertical_lookahead` in `VelocityRenderGameRules`, allowing server operators to toggle vertical velocity bias.
- **Complete 11-Language Parity**: Added full native translations for the new GameRule and its tooltip across English, German, Spanish, French, Indonesian, Italian, Japanese, Korean, Portuguese, Russian, and Simplified Chinese.

---

## [1.2.0+26.3] - Symmetrical 3D Pitch-Aware Lookahead
- **3D Vertical Section Bias**: Incorporated vertical pitch velocity ($v_y$) into sub-chunk compile priority sorting, ensuring steep Elytra nose-dives prioritize bedrock terrain sections and rocket ascents prioritize sky sections.
- **Symmetrical 3D Vector Math**: Balanced lookahead biasing uniformly across positive and negative pitch vectors with zero heap allocation.
- **Automated 3D Regression Suite**: Added comprehensive vertical dive, rocket ascent, and horizontal symmetry assertions to `VelocityVectorHelperTest`.

---

## [1.1.3+26.3] - Brigadier Command Suite & Diagnostics Update
- **Telemetry Expansion**: Exposed real-time banked turn widening toggle state, angular turn rate (deg/tick), and directional sign (`LEFT`, `RIGHT`, `STRAIGHT`) in `/velocityrender status` (`/vr status`).
- **Dynamic Command Configuration**: Added `turn_widening` and alias `turn` to `/vr get` and `/vr set` with full tab suggestions.
- **Reset Hygiene**: Included `velocityrender:turn_widening` in `/vr reset` defaults.
- **Regression Verification**: Added trajectory transition test verifying turn state decay and dynamic recovery.

---

## [1.1.2+26.3] - Server Predictive Arc Corridor Integration
- **Predictive Turn-Arc Corridor**: Integrated `TurnRateCalculator` into `VelocityTicketManager`, expanding high-speed travel lines into asymmetric directional curve fan-outs during banked turns.
- **Early Apex Generation**: Lookahead corridor activates lateral chunk loading from lookahead step >= 2 onwards, eliminating void stalls at turn apexes.
- **Adaptive MSPT Guard**: Integrated with MSPT watchdog to clamp turn fan-out under heavy server tick load (> 25ms).
- **Diagnostic Metrics**: Added `getPlayerTurnRate` and `getPlayerTurnSign` metrics for real-time monitoring.

---

## [1.1.1+26.3] - Dynamic Turn GameRule & Localization Parity Update

- **Dynamic GameRule**: Registered `velocityrender:turn_widening` (default: `true`) under `VelocityRenderGameRules`.
- **11-Language Localization**: Added complete translated keys (`velocityrender.gamerule.turn_widening` and description) across English, Simplified Chinese, Traditional Chinese, Russian, Spanish, German, French, Brazilian Portuguese, Japanese, Indonesian, and Korean.
- **Dynamic Access Helper**: Added `VelocityRenderGameRules.isTurnWideningEnabled(Level)` for safe dynamic lookup.

---

## [1.1.0+26.3] - The Banked Turn-Arc Engine Update

- **Turn-Arc Math Engine**: Implemented standalone `TurnRateCalculator` with Exponential Moving Average (EMA, $\alpha = 0.60$) angular yaw rate tracking.
- **Directional Arc Fan-Out**: Added 2-tier lateral step scaling algorithms for moderate and sharp turn detection.
- **Headless Unit Verification**: Added comprehensive test suite covering wrap-around boundary crossings, direction signs, and threshold gates.

---

## [1.0.0+26.3] - The Velocity Render Genesis Update

Initial production release of **Velocity Render** for Minecraft **26.3**.

### ✨ Features
- **Client-Side Anisotropic Meshing**: Directs chunk compile thread priority along the player's forward travel vector using a lightweight dot-product bias. Forward chunks compile first; rear chunks defer smoothly.
- **Server-Side Predictive Generation**: Dynamically issues lightweight `PLAYER_LOADING` chunk tickets up to 16 chunks ahead during high-speed travel (Elytra, ice boats, horses, speed potions), eliminating void stalls.
- **Adaptive MSPT Load Watchdog**: Continuously monitors server tick duration (`getAverageTickTimeNanos()`) and automatically throttles forward reach if MSPT exceeds 25ms to ensure rock-solid 20 TPS.
- **Modern Brigadier Command Suite**: Accessible via `/velocityrender` or short alias `/vr` (`/vr status`, `/vr get`, `/vr set`, `/vr reset`, `/vr reload`).
- **Dynamic In-Game GameRules**: Custom per-world configuration via `velocityrender:enabled`, `velocityrender:lead_multiplier`, and `velocityrender:budget_conservation`.
- **Zero-Allocation Hot Path**: Lock-free volatile caching ensures 0B heap memory allocation per frame on the client render thread.
