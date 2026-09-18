# 📌 Velocity Render Backlog

This file tracks planned features, technical refinements, performance optimizations, and deferred enhancements for **Velocity Render**.

---

## 📊 Backlog Summary

| ID | Category | Title | Priority | Target Version | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `[BL-VR-001]` | `[FEATURE]` | Dynamic Corridor Turn-Widening on Banked Turns | `[HIGH]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-002]` | `[FEATURE]` | 3D Pitch-Aware Vertical Lookahead (Dives & Ascents) | `[HIGH]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-003]` | `[PERF]` | Server-Wide Ticket Budget & Fair Multi-Player Allocation | `[HIGH]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-004]` | `[REFINEMENT]` | Right-Side F3 Engine Diagnostic Metric Line | `[MEDIUM]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-005]` | `[PERF]` | Nether WorldGen Clamping & Dense Dimension Scaling | `[MEDIUM]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-006]` | `[INTEGRATION]` | Bobby & Distant Horizons LOD Velocity Trajectory Hooks | `[MEDIUM]` | `26.3+` | `📌 DEFERRED` |
| `[BL-VR-007]` | `[FEATURE]` | Optional YACL Config Screen via ModMenu | `[LOW]` | `26.3+` | `📌 DEFERRED` |

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
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `ForwardTicketManager.java`, `VelocityRenderMod.java`
- **Date Added**: 2026-09-18

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
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `ForwardTicketManager.java`, `AnisotropicDistanceHelper.java`
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
Ticket requests and mesh prioritization currently focus heavily on horizontal XZ plane velocity. Steep vertical Elytra dives, rocket climbs, or bubble elevator ascents traverse vertical sub-chunk sections faster than vanilla's vertical builder priority.

#### 💡 Proposed Solution & Technical Specifications
Incorporate normalized vertical pitch velocity component ($v_y$) into both:
1. `AnisotropicDistanceHelper`: Scale compile task priority for vertical sub-chunks ($Y \pm 4$) in the flight pitch vector.
2. `ForwardTicketManager`: Forward tickets already cover entire chunk columns, but ticket generation trigger should fire upon high vertical delta-Y movement ($|v_y| \ge 0.50$ b/t).

#### 🧪 Verification & Acceptance Criteria
- [ ] Steep vertical nose-dives from build height ($Y=320$) to bedrock mesh terrain continuously with 0B/frame heap allocations.
- [ ] Rocket ascents prioritize chunk sections directly above.

---

### [BL-VR-003] Server-Wide Ticket Budget & Fair Multi-Player Allocation
- **Category**: `[PERF]`
- **Priority**: `[HIGH]`
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `ForwardTicketManager.java`, `VelocityRenderMod.java`
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
On multiplayer servers with multiple players flying simultaneously with Elytras, unrestricted forward ticket creation across all players could overwhelm the chunk generation thread pool, even with individual MSPT watchdog load shedding.

#### 💡 Proposed Solution & Technical Specifications
Implement a global server ticket budget (default: 64 active forward tickets server-wide).
- Maintain an active ticket counter across all players.
- If total requested tickets exceed the budget, distribute tickets fairly among active high-speed players using weighted round-robin or proportionate scaling based on relative speeds.

#### 🧪 Verification & Acceptance Criteria
- [ ] Total active `PLAYER_LOADING` tickets issued by Velocity Render never exceed the configured server budget.
- [ ] Server MSPT remains stable even when 4+ players fly concurrently.

---

### [BL-VR-004] Right-Side F3 Engine Diagnostic Metric Line
- **Category**: `[REFINEMENT]`
- **Priority**: `[MEDIUM]`
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `DebugScreenOverlayMixin.java`, `VelocityRenderClient.java`
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
Minecraft 26.3 and VO Speedometer already provide player movement speed. Users want clean visibility into Velocity Render's actual background engine state (active tickets, watchdog shed percentage, and anisotropic cone angle) without cluttering player speed readouts.

#### 💡 Proposed Solution & Technical Specifications
Inject into the right-side list of `DebugScreenOverlay`:
```
[VelocityRender] Active Tickets: 8 | Shed: 0% | Cone: 45°
```
Gated behind client toggle or GameRule. Zero string concatenation garbage by formatting into a reusable or cached string buffer.

#### 🧪 Verification & Acceptance Criteria
- [ ] F3 right-side list displays the clean diagnostic line when F3 is open.
- [ ] Displays actual live ticket count, watchdog load shed state, and turn cone.
- [ ] Zero impact on FPS or garbage collection when F3 is open.

---

### [BL-VR-005] Nether WorldGen Clamping & Dense Dimension Scaling
- **Category**: `[PERF]`
- **Priority**: `[MEDIUM]`
- **Status**: `📌 DEFERRED`
- **Target Component(s)**: `ForwardTicketManager.java`
- **Date Added**: 2026-09-18

#### ❓ Problem / Context
The Nether features a solid ceiling, complex cave carvers, and heavy block density. Full 16-chunk lookahead in the Nether creates substantially higher CPU worldgen load per chunk than Overworld surface terrain.

#### 💡 Proposed Solution & Technical Specifications
Check `world.dimension() == Level.NETHER`. In the Nether, clamp maximum forward lookahead reach to 60% of the normal limit (e.g. max 8–10 chunks instead of 16), while maintaining full anisotropic client meshing priority.

#### 🧪 Verification & Acceptance Criteria
- [ ] Nether Elytra flights or blue-ice highways clamp forward tickets to prevent server tick spikes.
- [ ] Overworld and End dimensions retain full lookahead reach.

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