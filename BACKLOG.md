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
| `[BL-VR-005]` | `[PERF]` | Nether WorldGen Clamping & Dense Dimension Scaling | `[HIGH]` | `26.3+` | `🚧 IN_PROGRESS` |
| `[BL-VR-006]` | `[INTEGRATION]` | Bobby & Distant Horizons LOD Velocity Trajectory Hooks | `[MEDIUM]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-007]` | `[FEATURE]` | Optional YACL Config Screen via ModMenu | `[LOW]` | `26.3+` | `📌 DEFERRED` |
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
- **Status**: `🔄 IN PROGRESS`
- **Target Component(s)**: `DimensionReachScaler.java`, `DimensionClampManager.java`, `VelocityRenderGameRules.java`, `VelocityTicketManager.java`, `VelocityRenderCommand.java`
- **Date Added**: 2026-09-18

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
- [ ] Nether forward reach clamps to 60% (max 10 chunks at full speed) while preserving 100% in the Overworld.
- [ ] Modded/End dimensions default to configured clamp (80%) or custom per-dimension override.
- [ ] Zero heap allocations on 20 TPS server tick hot paths.
- [ ] Fully controllable via Dynamic GameRules, Sparse Delta JSON config, Data-driven Tags, and `/vr dimclamp`.

---

### [BL-VR-006] Bobby & Distant Horizons LOD Velocity Trajectory Hooks
- **Category**: `[INTEGRATION]`
- **Priority**: `[MEDIUM]`
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `LODCompatManager.java` (Optional/Soft reflection)
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
Players who use Distant Horizons or Bobby load fake/LOD chunks far into the distance. By default, LOD worker queues process LOD chunks uniformly in all directions.

#### 💡 Proposed Solution & Technical Specifications
Provide soft-reflection bridges:
- If Distant Horizons or Bobby is present on the client, broadcast the normalized lookahead trajectory to their priority queues or adjust LOD generation weighting along the forward velocity cone.
- Strict classloader safety: do not crash if neither mod is installed.

#### 🧪 Verification & Acceptance Criteria
- [ ] Game starts smoothly without errors when Bobby/DH are absent.
- [ ] When Bobby/DH are present, forward distant terrain generates ahead of rear distant terrain.

---

### [BL-VR-007] Optional YACL Config Screen via ModMenu
- **Category**: `[FEATURE]`
- **Priority**: `[LOW]`
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `ModMenuIntegration.java`, `VelocityRenderConfig.java`
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
Currently, settings are adjusted via GameRules and `/velocityrender` commands. A clean in-game visual GUI makes tuning accessible to casual players.

#### 💡 Proposed Solution & Technical Specifications
Implement YetAnotherConfigLib (YACL v3) screen integrated with ModMenu:
- Sliders for lookahead sensitivity, corridor width, and watchdog threshold.
- Toggles for F3 diagnostics and Nether clamping.
- Strict optional gating: if YACL is absent, fallback gracefully to `null` screen without classloader crashes.

#### 🧪 Verification & Acceptance Criteria
- [ ] ModMenu shows "Configure" button when YACL is loaded.
- [ ] Dedicated server and client without YACL run without any class not found crashes.

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