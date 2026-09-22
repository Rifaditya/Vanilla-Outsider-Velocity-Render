<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-purple?style=for-the-badge" alt="Requires Dasik Library"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.3-brightgreen?style=for-the-badge" alt="Minecraft 26.3">
</p>

# ⚡ Velocity Render

> **"Render what's ahead, not what's behind."**

Have you ever soared across the skies with an Elytra, raced boats across blue ice highways, or sprinted through dense terrain—only to slam into invisible chunk borders or fall through a terrifying blue void hole while chunk generation desperately tries to catch up?

Vanilla Minecraft's chunk builder treats all directions equally: it wastes precious CPU rendering power meshing chunks behind your back that you cannot even see, while the landscape directly in your travel path remains unbuilt.

**Velocity Render** completely rewires this balance. By dynamically synchronizing Minecraft's chunk world generation and client meshing pipeline with your instantaneous 3D velocity vector, the game actively prioritizes the terrain ahead of your travel trajectory—eliminating high-speed void pop-in, preventing Elytra wall stalls, and keeping your journey butter-smooth.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

---

## 📊 Visual Architecture & Engine Mechanics

### 1. The 4-Stage Velocity Cone Progression
![Velocity Cone Progression](https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Velocity-Render/main/Doc/Media/velocity_cone_progression.png)

As you accelerate from an idle stroll into supersonic Elytra flight, Velocity Render dynamically transitions through 4 distinct prioritization cones:
- **Stage 1: Vanilla Circle (`Speed < 0.20 b/t`)**: Standard radial meshing. Chunks in all directions share equal compile priority.
- **Stage 2: Wide V (`0.20 – 0.50 b/t`)**: Walking & galloping. Subtle forward bias activates, extending lead distance 2–4 chunks ahead.
- **Stage 3: Medium V (`0.50 – 1.20 b/t`)**: Sprinting & riding. Prioritization narrows forward and applies distance penalties to rear chunks.
- **Stage 4: Tight High-Speed V (`> 1.20 b/t`)**: Supersonic Elytras & ice highways. Full forward reach (up to 16+ chunks) meshes your flight corridor instantaneously.

---

### 2. 3D Pitch-Aware Vertical Lookahead (Dives & Ascents)
![3D Pitch-Aware Lookahead](https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Velocity-Render/main/Doc/Media/vertical_lookahead.png)

High-speed exploration is rarely confined to flat terrain:
- **Steep Elytra Dives (`vy < -0.50 b/t`)**: Plunging from build height toward bedrock prioritizes sub-chunks $Y - 4$ ahead of your dive path, rendering caves and terrain before impact.
- **Rocket Climbs (`vy > +0.50 b/t`)**: Upward flight prioritizes sub-chunks $Y + 4$ above you, meshing mountain summits and skybox structures immediately.
- **Level Flight (`|vy| < 0.20 b/t`)**: Sub-chunk compilation bias remains neutral across horizontal terrain.

---

### 3. Server-Wide Multi-Player Ticket Quota Allocation
![Multi-Player Ticket Quotas](https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Velocity-Render/main/Doc/Media/multiplayer_quota_pool.png)

On multi-player servers, unrestricted forward chunk tickets could overwhelm generation threads:
- **Global Ticket Pool**: A server-wide ticket budget (default: 64 active tickets) is dynamically distributed among active flyers.
- **Speed-Weighted Partitioning**: Fast flyers traveling at $48\text{ m/s}$ receive larger quotas (up to 32 tickets + banked fan-out) while moderate flyers receive balanced slices.
- **Anti-Starvation Floor**: Every active flyer moving $\ge 0.20\text{ b/t}$ is guaranteed a minimum floor of $\min(4, \text{budget} / N)$ tickets.
- **Standby Release**: Stationary or walking players draw `0` forward tickets, falling back to standard vanilla radius loading.

---

### 4. Dimension Reach Scaling & Conventional Tags
![Dimension Reach Scaling](https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Velocity-Render/main/Doc/Media/dimension_scaling.png)

World generation costs vary dramatically across dimensions:
- **Overworld (`minecraft:overworld`)**: Runs at **100% reach** (up to 16 chunks / 256m forward lookahead).
- **The Nether (`minecraft:the_nether`)**: Clamped to **60% reach** (max 10 chunks / 160m), saving **40% worldgen CPU overhead** across heavy 3D carver and lava oceans.
- **Dense Dimensions (`#c:dense_dimensions` / `#velocityrender:dense_dimensions`)**: Automatically clamped to **80% reach** (max 13 chunks / 208m) for cave worlds and modded subterranean dimensions.

---

## ✨ Full Feature Overview (v1.8.x)

### 🏎️ Client-Side Anisotropic Meshing Prioritization
- **Dynamic Dot-Product Distance Bias**: Redirects vanilla's `SectionTaskDynamicQueue.poll()` so forward chunk tasks evaluate with smaller biased distances.
- **Zero-Allocation Hot Path**: Thread-safe volatile caching guarantees **0 bytes of GC heap allocation per frame and per tick**.
- **Rear Section Deferral**: Chunks behind the player receive positive distance penalties, saving render CPU for what is on screen.

### 🔄 Dynamic Banked Turn Compensation
- **Instantaneous Yaw Rate Calculation**: Measures angular velocity ($\Delta\text{yaw}/\Delta t$) via Exponential Moving Average (EMA).
- **Apex Fan-Out Arc**: Expands corridor generation +1 to +2 chunks into the curve when turning sharply ($\ge 4^\circ/\text{tick}$ at $\ge 0.50\text{ b/t}$), eliminating black void apexes.

### 🏔️ 3D Pitch-Aware Lookahead
- Evaluates full 3D velocity vectors $(\hat{v}_x, \hat{v}_y, \hat{v}_z)$ for vertical section biasing ($Y \pm 4$).
- Fast-path volatile state prevents any thread contention or object boxing.

### 🌐 Server-Side Predictive Generation & Multi-Player Quotas
- Predictively generates terrain ahead of fast players on dedicated servers using `TicketType.PLAYER_LOADING`.
- Dynamic ticket quota pooling protects server MSPT when dozens of players fly simultaneously.
- Clean lifecycle release on player stop, dimension shift, or server disconnect.

### 🌌 Dimension Reach Scaling
- Automated dimension GameRules (`velocityrender:reach_clamp_<dimension>_pct`).
- Conventional tag support (`#c:dense_dimensions`).
- Sparse delta JSON persistence (`config/velocity-render/dimension_clamps.json`).

### 🔭 Bobby & Distant Horizons LOD Integration
- Soft-reflection bridge dynamically directs fake chunk / LOD generation queues along the forward flight vector.
- Zero crash risk: gracefully idles if Bobby or Distant Horizons is not installed.

### 📊 Clean In-Game F3 Telemetry
- Optional clean right-side F3 metric line:
  ```
  [VelocityRender] Active Tickets: 8 | Shed: 0% | Cone: 45°
  ```
- Cached formatting ensures zero GC garbage generation when F3 is open.

### ⚙️ In-Game Configuration GUI (ModMenu & YACL)
- Clean visual settings screen integrated into ModMenu via YetAnotherConfigLib (YACL v3).
- Smooth sliders for lead multipliers, budgets, speed thresholds, and dimension reach clamps.
- Full server safety: falls back cleanly if YACL is absent.

### 🔓 Freedom Over Anti-Crash Standard (Uncapped Stress-Testing)
- **Zero Nanny Ceilings**: Multipliers up to $10,000\%+$, ticket budgets up to $65,536+$, and stationary pre-loading ($0\text{ b/t}$).
- **Non-Blocking Transparency ("Warn Clearly, Never Stop")**: Dual-sink advisory alerts in chat and server logs if extreme stress parameters are chosen, but never aborts execution.
- **Saturated Arithmetic**: All mathematical calculations use `long` math bounded at physical type limits, preventing integer wraparound.

---

## 📊 Mechanics Comparison Matrix

| Mechanic | Vanilla Minecraft | Velocity Render Enhancement |
| :--- | :--- | :--- |
| **Client Meshing Priority** | Radial Euclidean distance (all directions equal) | Anisotropic 3D forward vector bias cone |
| **Vertical Flight (Dives/Climbs)** | Ignores vertical descent speed | 3D pitch lookahead prioritizing $Y \pm 4$ sub-chunks |
| **Banked Elytra Turns** | Linear look vector lags behind curve | Dynamic fan-out arc into apex of turn |
| **Multi-Player Generation** | Unchecked ticket generation causes lag spikes | Server-wide ticket quota pool with anti-starvation floor |
| **Nether Worldgen Load** | Full radial chunk generation in dense carvers | 60% dynamic reach clamp, saving 40% CPU time |
| **LOD Mods (DH / Bobby)** | Uniform circular LOD generation | Trajectory hooks stream forward distant terrain |
| **Memory Footprint** | Standard object allocations | Volatile primitive cache (0B/frame heap allocation) |
| **Configuration Freedom** | Often clamped with arbitrary limits | Uncapped power-user capacity with saturated math |

---

## 🛠️ In-Game Commands (`/velocityrender`, `/vr`)

Full in-game Brigadier command suite with rich tab completions:

| Command | Permission | Description |
| :--- | :---: | :--- |
| `/vr status` | All Players | Real-time diagnostics: speed ($m/s$ & $b/t$), forward lead distance, active tickets, and server MSPT. |
| `/vr get <rule>` | All Players | Queries the current value of any configuration setting. |
| `/vr set <rule> <value>` | Gamemasters (Level 2) | Updates settings dynamically in real time. |
| `/vr dimclamp <dim> <pct>` | Gamemasters (Level 2) | Sets reach scaling percentage for a specific dimension. |
| `/vr dimclamp reset` | Gamemasters (Level 2) | Resets dimension clamp overrides to defaults. |
| `/vr reset` | Gamemasters (Level 2) | Restores all configuration settings to default values. |
| `/vr reload` | Gamemasters (Level 2) | Reloads configuration files and dimension clamp overrides from disk. |

---

## ⚙️ Native Dynamic GameRules

Customize behavior per-world directly through the in-game **Edit Game Rules** UI or commands:

- `velocityrender:enabled`: Master toggle for the entire prioritization system. (Default: `true`)
- `velocityrender:lead_multiplier`: Forward lead distance scaling factor (0% to uncapped). (Default: `100`)
- `velocityrender:server_ticket_budget`: Server-wide active ticket budget (1 to uncapped). (Default: `64`)
- `velocityrender:budget_conservation`: Enables dynamic ticket trimming and watchdog load shedding. (Default: `true`)
- `velocityrender:min_speed_threshold_pct`: Speed threshold in hundredths of b/t (20 = 0.20 b/t = 4.0 m/s). Set to 0 for stationary preloading. (Default: `20`)
- `velocityrender:reach_clamp_the_nether_pct`: Nether reach percentage clamp. (Default: `60`)
- `velocityrender:reach_clamp_default_dense_pct`: Dense / cave dimension reach clamp. (Default: `80`)
- `velocityrender:debug_mode`: Enables diagnostic logging. (Default: `false`)

---

## 🔌 Sidedness & Compatibility

- **Universal Graphic Compatibility**: Velocity Render does not modify shaders, render pipelines, or vertex formats. It is 100% compatible with **Iris**, **Sodium**, **ImmediatelyFast**, **Lithium**, **Nvidium**, **Distant Horizons**, and **Bobby**.
- **Flexible Environment Sidedness**:
  - *Client-Only on Vanilla Servers*: Prioritizes meshing of received chunks in your forward velocity cone.
  - *Server-Only with Vanilla Clients*: Predictively generates and loads terrain ahead of fast players on the server, eliminating void walls for all connecting players.
  - *Both*: Maximum synergy across generation and meshing pipelines.

---

## ☕ Support the Project

If you enjoy Velocity Render and want to support ongoing development across the **Vanilla Outsider** collection:

<p align="center">
    <a href="https://ko-fi.com/dasikigaijin"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
    <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
    <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

---

## 📜 Credits & Modpack Permissions

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GNU General Public License v3.0 (GPL-3.0-or-later) |
| **Source Code** | [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render) |

> [!IMPORTANT]
> **📦 Modpack Permissions & Distribution:**<br>
> You are fully welcome to include this mod in any modpack on Modrinth or CurseForge! Please ensure downloads are routed through official platform links. Re-uploading raw mod JARs to unauthorized mirror sites or third-party scraping launchers is prohibited.<br><br>
> **⚖️ License & Fork Guidelines:**<br>
> Open source under **GPLv3**. Forking for genuine feature development, community ports, or experimentation is encouraged with proper attribution. Straight 1:1 re-uploads and zero-change clone forks are strictly prohibited.

---

<p align="center">
  <strong>Made with ❤️ for the Minecraft community</strong><br>
  <em>Part of the Vanilla Outsider Collection</em>
</p>
