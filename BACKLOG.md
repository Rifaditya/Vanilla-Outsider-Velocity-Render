# 📌 Velocity Render Backlog

This file tracks planned features, technical refinements, performance optimizations, and deferred enhancements for **Velocity Render**.

---

## 📊 Backlog Summary

| ID | Category | Title | Priority | Target Version | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `[BL-VR-001]` | `[FEATURE]` | Dynamic Corridor Turn-Widening on Banked Turns | `[HIGH]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-002]` | `[FEATURE]` | 3D Pitch-Aware Vertical Lookahead (Dives & Ascents) | `[HIGH]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-003]` | `[PERF]` | Server-Wide Ticket Budget & Fair Multi-Player Allocation | `[HIGH]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-004]` | `[REFINEMENT]` | Right-Side F3 Engine Diagnostic Metric Line | `[MEDIUM]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-005]` | `[PERF]` | Nether WorldGen Clamping & Dense Dimension Scaling | `[HIGH]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-006]` | `[INTEGRATION]` | Bobby & Distant Horizons LOD Velocity Trajectory Hooks | `[MEDIUM]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-007]` | `[FEATURE]` | Optional YACL Config Screen via ModMenu | `[LOW]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-009]` | `[REFINEMENT]` | Uncap Configuration Limits & Hard Ceilings (Player Freedom / Stress-Testing) | `[MEDIUM]` | `26.3+` | `✅ RESOLVED` |
| `[BL-VR-008]` | `[DOCS]` | Architecture Documentation & Visual Velocity Cone Progression | `[MEDIUM]` | `26.3+` | `🚧 IN_PROGRESS` |

---

## 🏷 Legend & Status Tags
- **Categories**: `[FEATURE]`, `[REFINEMENT]`, `[BUGFIX]`, `[PERF]`, `[INTEGRATION]`
- **Priorities**: `[HIGH]` (Important logic/engine upgrade), `[MEDIUM]` (Quality of life / scaling), `[LOW]` (Minor polish / optional UI)
- **Statuses**: `📌 DEFERRED` (Queued for roadmap execution), `🚧 IN_PROGRESS` (Active development), `✅ RESOLVED` (Implemented and verified)

---

## 📝 Detailed Backlog Entries

### [BL-VR-001] Dynamic Corridor Turn-Widening on Banked Turns
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `VelocityTicketManager.java`, `TurnRateCalculator.java`, `VelocityRenderGameRules.java`, `VelocityRenderCommand.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-19 (v1.1.0 – v1.1.3+26.3)

#### ❓ Problem / Context
Currently, high-speed travel produces a linear corridor along the blended velocity and look vector. When a player banks sharply during an Elytra dive or ice-boat drift, high angular yaw velocity ($\Delta \text{yaw}$) causes the player to turn faster than the forward ray corridor can reorient, occasionally causing the outer curve of the turn to lag behind.

#### 💡 Proposed Solution & Technical Specifications
Calculate the player's instantaneous angular yaw rate $\omega = |\Delta \text{yaw}| / \Delta t$. When $\omega > \text{threshold}$ and speed $\ge 0.50$ b/t, expand the corridor generation into a fan-out arc proportional to $\omega$, requesting chunks across the turn radius before the player enters the apex.

#### 🧪 Verification & Acceptance Criteria
- [ ] Sharp 90-degree and 180-degree Elytra turns generate the inner and outer curve chunks without void gaps.
- [ ] Straight flight preserves narrow corridor generation to conserve server resources.

---

### [BL-VR-002] 3D Pitch-Aware Vertical Lookahead (Dives & Ascents)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `ForwardTicketManager.java`, `AnisotropicDistanceHelper.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-20 (v1.2.0 – v1.2.3+26.3)

#### ❓ Problem / Context
Ticket requests and mesh prioritization currently focus heavily on horizontal XZ plane velocity. Steep vertical Elytra dives, rocket climbs, or bubble elevator ascents traverse vertical sub-chunk sections faster than vanilla's vertical builder priority.

#### 💡 Proposed Solution & Technical Specifications
Incorporate normalized vertical pitch velocity component ($v_y$) into both:
1. `AnisotropicDistanceHelper`: Scale compile task priority for vertical sub-chunks ($Y \pm 4$) in the flight pitch vector.
2. `ForwardTicketManager`: Forward tickets already cover entire chunk columns, but ticket generation trigger should fire upon high vertical delta-Y movement ($|v_y| \ge 0.50$ b/t).

#### 🧪 Verification & Acceptance Criteria
- [x] Steep vertical nose-dives from build height ($Y=320$) to bedrock mesh terrain continuously with 0B/frame heap allocations.
- [x] Rocket ascents prioritize chunk sections directly above.

---

### [BL-VR-003] Server-Wide Ticket Budget & Fair Multi-Player Allocation
- **Category**: `[PERF]`
- **Priority**: `[HIGH]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `VelocityTicketManager.java`, `TicketBudgetAllocator.java`, `VelocityRenderGameRules.java`, `VelocityRenderCommand.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-20 (v1.3.0 – v1.3.3+26.3)

#### ❓ Problem / Context
On multiplayer servers with multiple players flying simultaneously with Elytras, unrestricted forward ticket creation across all players could overwhelm the chunk generation thread pool, even with individual MSPT watchdog load shedding.

#### 💡 Proposed Solution & Technical Specifications
Implement a global server ticket budget (default: 64 active forward tickets server-wide).
- Maintain an active ticket counter across all players.
- If total requested tickets exceed the budget, distribute tickets fairly among active high-speed players using weighted round-robin or proportionate scaling based on relative speeds.

#### 🧪 Verification & Acceptance Criteria
- [x] Total active `PLAYER_LOADING` tickets issued by Velocity Render never exceed the configured server budget.
- [x] Server MSPT remains stable even when 4+ players fly concurrently.

---

### [BL-VR-004] Right-Side F3 Engine Diagnostic Metric Line
- **Category**: `[REFINEMENT]`
- **Priority**: `[MEDIUM]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `DebugScreenOverlayMixin.java`, `VelocityRenderClient.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-20 (v1.4.0 – v1.4.3+26.3)

#### ❓ Problem / Context
Minecraft 26.3 and VO Speedometer already provide player movement speed. Users want clean visibility into Velocity Render's actual background engine state (active tickets, watchdog shed percentage, and anisotropic cone angle) without cluttering player speed readouts.

#### 💡 Proposed Solution & Technical Specifications
Inject into the right-side list of `DebugScreenOverlay`:
```
[VelocityRender] Active Tickets: 8 | Shed: 0% | Cone: 45°
```
Gated behind client toggle or GameRule. Zero string concatenation garbage by formatting into a reusable or cached string buffer.

#### 🧪 Verification & Acceptance Criteria
- [x] F3 right-side list displays the clean diagnostic line when F3 is open.
- [x] Displays actual live ticket count, watchdog load shed state, and turn cone.
- [x] Zero impact on FPS or garbage collection when F3 is open.

---

### [BL-VR-005] Nether & Dense Dimension WorldGen Clamping
- **Category**: `[PERF]`
- **Priority**: `[HIGH]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `DimensionReachScaler.java`, `DimensionClampManager.java`, `VelocityRenderGameRules.java`, `VelocityTicketManager.java`, `VelocityRenderCommand.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-21 (v1.5.0 – v1.5.4+26.3)

#### ❓ Problem / Context
The Nether and custom cave/modded dimensions feature solid bedrock ceilings, complex 3D noise carvers, and high block densities. Generating full 16-chunk forward lookahead corridors in these environments incurs dramatically higher CPU worldgen costs per chunk than surface Overworld terrain, causing server MSPT spikes during Elytra flight or high-speed ice-boat highways.

#### 💡 Proposed Solution & Technical Specifications
Implement universal dynamic dimension reach scaling through a unified 3-tier configuration system:
1. **Dynamic GameRules**:
   - `velocityrender:reach_clamp_the_nether_pct` (default: 60)
   - `velocityrender:reach_clamp_default_dense_pct` (default: 80, applied to non-Overworld dimensions without an explicit rule)
   - Auto-registers dimension-specific GameRules dynamically upon server start for all loaded dimensions.
2. **Dynamic Sparse Delta Persistence (`config/velocity-render/dimension_clamps.json`)**:
   - Persists per-dimension overrides set via commands without dumping full boilerplate configs.
3. **Data-Driven Conventional Tag `#c:dense_dimensions` / `#velocityrender:dense_dimensions`**:
   - Datapacks and modpacks can tag dimensions as dense for automatic scaling.
4. **Command Suite Integration**:
   - `/vr dimclamp <dimension> <percentage>` and `/vr dimclamp reset` with full tab completions.
   - `/vr status` displays active dimension reach scaling: `• Dimension Scaling: the_nether (Clamped: 60% -> max 10 chunks)`.

#### 🧪 Verification & Acceptance Criteria
- [x] Nether forward reach clamps to 60% (max 10 chunks at full speed) while preserving 100% in the Overworld.
- [x] Modded/End dimensions default to configured clamp (80%) or custom per-dimension override.
- [x] Zero heap allocations on 20 TPS server tick hot paths.
- [x] Fully controllable via Dynamic GameRules, Sparse Delta JSON config, Data-driven Tags, and `/vr dimclamp`.

---

### [BL-VR-006] Bobby & Distant Horizons LOD Velocity Trajectory Hooks
- **Category**: `[INTEGRATION]`
- **Priority**: `[MEDIUM]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `LODTrajectoryCalculator.java`, `VelocityRenderGameRules.java`, `VelocityTrajectoryAPI.java`, `LODCompatManager.java`, `DistantHorizonsAdapter.java`, `BobbyAdapter.java`, `ClientVelocityTracker.java`, `VelocityRenderClient.java`, `VelocityRenderCommand.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-21 (v1.6.0 – v1.6.4+26.3)

#### ❓ Problem / Context
Players who use Distant Horizons or Bobby load fake/LOD chunks far into the distance. By default, LOD worker queues process LOD chunks uniformly in all directions.

#### 💡 Proposed Solution & Technical Specifications
Provide soft-reflection bridges:
- If Distant Horizons or Bobby is present on the client, broadcast the normalized lookahead trajectory to their priority queues or adjust LOD generation weighting along the forward velocity cone.
- Strict classloader safety: do not crash if neither mod is installed.

#### 🧪 Verification & Acceptance Criteria
- [x] Game starts smoothly without errors when Bobby/DH are absent.
- [x] When Bobby/DH are present, forward distant terrain generates ahead of rear distant terrain.

---

### [BL-VR-007] Optional YACL Config Screen via ModMenu
- **Category**: `[FEATURE]`
- **Priority**: `[LOW]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `ModMenuIntegration.java`, `VelocityRenderConfig.java`, `YaclScreenHelper.java`, `ClientConfigSyncer.java`
- **Date Added**: 2026-09-18
- **Date Resolved**: 2026-09-22 (v1.7.0 – v1.7.4+26.3)

#### ❓ Problem / Context
Currently, settings are adjusted via GameRules and `/velocityrender` commands. A clean in-game visual GUI makes tuning accessible to casual players.

#### 💡 Proposed Solution & Technical Specifications
Implement YetAnotherConfigLib (YACL v3) screen integrated with ModMenu:
- Sliders for lookahead sensitivity, corridor width, and watchdog threshold.
- Toggles for F3 diagnostics and Nether clamping.
- Strict optional gating: if YACL is absent, fallback gracefully to `null` screen without classloader crashes.

#### 🧪 Verification & Acceptance Criteria
- [x] ModMenu shows "Configure" button when YACL is loaded.
- [x] Dedicated server and client without YACL run without any class not found crashes.
- [x] Bi-directional synchronization between GUI, commands (`/vr reset`, `/vr reload`, `/vr set`), and runtime tracker.

---

### [BL-VR-009] Uncap Configuration Limits & Hard Ceilings (Player Freedom / Stress-Testing)
- **Category**: `[REFINEMENT]`
- **Priority**: `[MEDIUM]`
- **Status**: `✅ RESOLVED`
- **Target Component(s)**: `VelocityRenderGameRules.java`, `VelocityRenderCommand.java`, `VelocityTicketManager.java`, `ClientVelocityTracker.java`, `VelocityVectorHelper.java`, `TicketBudgetAllocator.java`
- **Date Added**: 2026-09-21
- **Date Resolved**: 2026-09-22 (v1.8.0 – v1.8.4+26.3)

#### ❓ Problem / Context
Velocity Render currently enforces restrictive artificial hard limits and upper clamping bounds across its configuration:
- `velocityrender:lead_multiplier`: Capped at `300%` in GameRule and `/vr set lead_multiplier` (`0..300`).
- `velocityrender:server_ticket_budget`: Capped at `256` tickets in GameRule and `/vr set budget` (`16..256`).
- `VelocityVectorHelper.MAX_LEAD_OFFSET`: Hardcoded client compile bias cap at `256.0` blocks (16 chunks).
- `ClientVelocityTracker`: Clamps lead multiplier strictly to `[0.0, 3.0]`.
- `VelocityTicketManager`: Base forward reach clamped around `16.0 * leadScale * msptFactor`.

Power users, high-performance dedicated servers (e.g. modern multi-core / 64GB+ setups), modpack developers, and players testing extreme supersonic travel (custom Elytras, high-speed rail, explosive cannon launchers) are artificially prevented from pushing the engine to its limits. If a player or server operator wants to crank values to extreme tiers (e.g. 1000%+ lead multiplier, 2048+ tickets) to stress-test their hardware or achieve unlimited forward lookahead—even at the risk of lagging their server or "breaking their own game"—the mod should provide complete freedom rather than imposing arbitrary ceilings.

**Key Invariant**: Safe defaults must remain strictly identical (`lead_multiplier: 100`, `server_ticket_budget: 64`, `min_speed_threshold_pct: 20`, `nether_reach_clamp_pct: 60`), guaranteeing out-of-the-box stability for standard gameplay.

#### 💡 Proposed Solution & Technical Specifications
1. **Uncap Dynamic GameRules & Command Ranges**:
   - `lead_multiplier`: Expand range from `(0, 300)` to `(0, 10000)` (or open/Integer.MAX_VALUE).
   - `server_ticket_budget`: Expand range from `(16, 256)` to `(1, 65536)` (or Integer.MAX_VALUE).
   - `min_speed_threshold_pct`: Expand range from `(1, 200)` to `(0, 10000)`.
   - Relax corresponding Brigadier argument clamps in `VelocityRenderCommand.java`.
2. **Elevate / Parameterize Client Biasing & Offset Caps**:
   - Relax `ClientVelocityTracker.setLeadMultiplier` upper clamp from `3.0` to support arbitrary user multipliers.
   - Scale `VelocityVectorHelper.MAX_LEAD_OFFSET` dynamically or raise the ceiling so client chunk compile prioritizing can bias beyond 256 blocks when extreme lead multipliers are configured.
3. **Dynamic Forward Reach Scaling**:
   - Ensure `VelocityTicketManager` does not impose an arbitrary 16-chunk base cap if a server operator configures high lead multipliers or large budgets.
4. **Preserve Defaults & Watchdog Safeguards**:
   - Baseline defaults remain unchanged (`lead_multiplier = 100`, `server_ticket_budget = 64`, `min_speed = 20`).
   - The MSPT Watchdog remains active as an automatic performance shock absorber unless explicitly disabled or overridden.

#### 🧪 Verification & Acceptance Criteria
- [x] Setting `velocityrender:lead_multiplier` to values $> 300$ (e.g. `1000`) succeeds in commands and GameRules without syntax errors or clamp truncation.
- [x] Setting `velocityrender:server_ticket_budget` to high values (e.g. `2048`) allows server to allocate forward tickets beyond 256.
- [x] Client chunk meshing bias scales beyond 256 blocks with elevated lead multipliers.
- [x] Default values remain 100% identical to previous releases (`lead_multiplier: 100`, `server_ticket_budget: 64`, `min_speed_threshold_pct: 20`).
- [x] `/vr reset` restores default safe values without regression.

---

### [BL-VR-008] Architecture Documentation & Visual Velocity Cone Progression
- **Category**: `[DOCS]`
- **Priority**: `[MEDIUM]`
- **Status**: `🔄 IN PROGRESS`
- **Target Component(s)**: `HOW_IT_WORKS.md`, `README.md`, `Doc/Media/velocity_cone_progression.png`, `Doc/Platform Pages/`
- **Date Added**: 2026-09-21

#### ❓ Problem / Context
Players, modpack developers, and contributors require comprehensive visual and mathematical explanations of Velocity Render's engine systems (anisotropic meshing queue, server lookahead corridors, multi-player ticket quotas, and dimension scaling).

#### 💡 Proposed Solution & Technical Specifications
1. **Initial Velocity Cone Progression Guide**:
   - Created `HOW_IT_WORKS.md` detailing the 4-stage velocity cone progression.
   - Embedded canonical draw.io architecture diagram in `Doc/Media/velocity_cone_progression.png`.
   - Documented core mathematical foundations: anisotropic distance formulas, rear distance penalties, banked turn fan-out, and MSPT load shedding.
   - Linked guide in root `README.md`.
2. **Upcoming Documentation Expansion**:
   - Visual diagrams for 3D pitch-aware dives & ascents (`BL-VR-002`).
   - Server-wide ticket quota distribution diagrams (`BL-VR-003`).
   - Dimension scaling diagrams and Conventional Tag guides (`BL-VR-005`).
   - Modrinth and CurseForge platform page visual asset refresh.

#### 🧪 Verification & Acceptance Criteria
- [x] Canonical velocity cone progression diagram saved in `Doc/Media/velocity_cone_progression.png`.
- [x] Initial 4-stage guide documented in `HOW_IT_WORKS.md`.
- [x] Navigational link integrated in `README.md`.
- [ ] 3D vertical flight and multi-player pool diagrams added.
- [ ] Platform descriptions synchronized with visual explanations.