# 📜 Changelog: Velocity Render

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## [1.3.1+26.3] - Dynamic Server Ticket Budget GameRule & Localization Parity (BL-VR-003 Step 2)
- **Dynamic GameRule Integration (`BL-VR-003 Step 2`)**: Registered `velocityrender:server_ticket_budget` integer rule (default `64`, range `[16, 256]`) via DasikLibrary's dynamic registry system.
- **Server Operator Control**: Enables dynamic live adjustment of total server-wide forward loading tickets allocated across all concurrent high-speed flyers.
- **11-Language Translation Parity**: Added native translations and detailed descriptions across all supported languages (English, German, Spanish, French, Indonesian, Italian, Japanese, Korean, Portuguese, Russian, Simplified Chinese, Traditional Chinese).

---

## [1.3.0+26.3] - Fair Allocation Math & Ticket Quota Engine (BL-VR-003 Step 1)
- **Fair Multi-Player Quota Engine (`BL-VR-003 Step 1`)**: Introduced pure algorithmic utility `TicketBudgetAllocator` computing speed-weighted proportional forward ticket quotas across concurrent flyers.
- **Dynamic Safety Floor**: Enforced a 4-ticket dynamic floor per active flyer to prevent corridor collapse during server budget saturation.
- **0B Hot-Path Allocations**: Pure static mathematical formulation running with zero heap allocations on 20 TPS tick paths.
- **Automated Regression Test Suite**: Added comprehensive unit test coverage in `TicketBudgetAllocatorTest` validating single flyer, symmetrical split, asymmetrical weighting, and extreme 20-flyer saturation clamping.

---

## [1.2.3+26.3] - Telemetry Diagnostics & Command Suite (BL-VR-002 Finale)
- **3D Trajectory Telemetry (`BL-VR-002 Step 4`)**: Added real-time pitch angle readout, vertical delta speed ($v_y$ b/t), and color-coded flight state indicators (`[DIVE]`, `[CLIMB]`, `[LEVEL]`) to `/velocityrender status` (`/vr status`).
- **Brigadier Command Controls**: Added `vertical_lookahead` and `vertical` command parameters to `/vr get` and `/vr set` with full tab completions.
- **Reset Hygiene**: Included `velocityrender:vertical_lookahead` in `/vr reset` defaults.
- **45-Degree Angled Dive Verification**: Expanded `VelocityVectorHelperTest` with diagonal 3D trajectory prioritization tests.
- **Milestone Complete**: Formally resolved and validated `[BL-VR-002]` across all 4 release cycles.

---

## [1.2.2+26.3] - Server Altitude Trigger & Vertical Gating
- **Vertical Altitude Recalculation Trigger (`BL-VR-002 Step 3`)**: Enabled rapid corridor recalculation when altitude changes by 8+ blocks ($|\Delta Y| \ge 8.0$), preventing chunk pop-in during steep Elytra dives from build height.
- **Dynamic GameRule Gating**: Bound vertical recalculation directly to `velocityrender:vertical_lookahead`.
- **Pitch & Vertical Delta Telemetry Accessors**: Added `getPlayerVerticalDelta` and `getPlayerPitch` query methods to `VelocityTicketManager`.

---

## [1.2.1+26.3] - Dynamic Vertical Lookahead GameRule & Localization
- **Dynamic GameRule Integration (`BL-VR-002 Step 2`)**: Registered `velocityrender:vertical_lookahead` in `VelocityRenderGameRules`, allowing server operators to toggle vertical velocity bias.
- **Complete 11-Language Parity**: Added full native translations for the new GameRule and its tooltip across English, German, Spanish, French, Indonesian, Italian, Japanese, Korean, Portuguese, Russian, and Simplified Chinese.

---

## [1.2.0+26.3] - Symmetrical 3D Pitch-Aware Lookahead
- **3D Vertical Section Bias (`BL-VR-002 Step 1`)**: Incorporated vertical pitch velocity ($v_y$) into sub-chunk compile priority sorting, ensuring steep Elytra nose-dives prioritize bedrock terrain sections and rocket ascents prioritize sky sections.
- **Symmetrical 3D Vector Math**: Balanced lookahead biasing uniformly across positive and negative pitch vectors with zero heap allocation.
- **Automated 3D Regression Suite**: Added comprehensive vertical dive, rocket ascent, and horizontal symmetry assertions to `VelocityVectorHelperTest`.

---

## [1.1.3+26.3] - Brigadier Command Suite & Diagnostics Update
- **Telemetry Expansion (`BL-VR-001 Step 4`)**: Exposed real-time banked turn widening toggle state, angular turn rate (deg/tick), and directional sign (`LEFT`, `RIGHT`, `STRAIGHT`) in `/velocityrender status` (`/vr status`).
- **Dynamic Command Configuration**: Added `turn_widening` and alias `turn` to `/vr get` and `/vr set` with full tab suggestions.
- **Reset Hygiene**: Included `velocityrender:turn_widening` in `/vr reset` defaults.
- **Regression Verification**: Added trajectory transition test verifying turn state decay and dynamic recovery.

---

## [1.1.2+26.3] - Server Predictive Arc Corridor Integration
- **Predictive Turn-Arc Corridor (`BL-VR-001 Step 3`)**: Integrated `TurnRateCalculator` into `VelocityTicketManager`, expanding high-speed travel lines into asymmetric directional curve fan-outs during banked turns.
- **Early Apex Generation**: Lookahead corridor activates lateral chunk loading from lookahead step >= 2 onwards, eliminating void stalls at turn apexes.
- **Adaptive MSPT Guard**: Integrated with MSPT watchdog to clamp turn fan-out under heavy server tick load (> 25ms).
- **Diagnostic Metrics**: Added `getPlayerTurnRate` and `getPlayerTurnSign` metrics for real-time monitoring.

---

## [1.1.1+26.3] - Dynamic Turn GameRule & Localization Parity Update

- **Dynamic GameRule (`BL-VR-001 Step 2`)**: Registered `velocityrender:turn_widening` (default: `true`) under `VelocityRenderGameRules`.
- **11-Language Localization**: Added complete translated keys (`velocityrender.gamerule.turn_widening` and description) across English, Simplified Chinese, Traditional Chinese, Russian, Spanish, German, French, Brazilian Portuguese, Japanese, Indonesian, and Korean.
- **Dynamic Access Helper**: Added `VelocityRenderGameRules.isTurnWideningEnabled(Level)` for safe dynamic lookup.

---

## [1.1.0+26.3] - The Banked Turn-Arc Engine Update

- **Turn-Arc Math Engine (`BL-VR-001`)**: Implemented standalone `TurnRateCalculator` with Exponential Moving Average (EMA, $\alpha = 0.60$) angular yaw rate tracking.
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
