# 🏛️ Technical History & Architecture Ledger: Velocity Render

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
