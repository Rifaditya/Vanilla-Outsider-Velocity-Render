<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-purple?style=for-the-badge" alt="Requires Dasik Library"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.3-brightgreen?style=for-the-badge" alt="Minecraft 26.3">
</p>

# ⚡ Velocity Render

> **"Render what's ahead, not what's behind."**

Have you ever soared across the skies with an Elytra, raced boats across blue ice highways, or sprinted under Speed II—only to crash directly into an invisible chunk barrier or fall through a terrifying blue void hole while the terrain desperately tries to catch up? 

Vanilla Minecraft's chunk builder treats all directions equally: it wastes precious CPU rendering power meshing chunks behind your back that you cannot even see, while the landscape directly in your flight path remains unbuilt.

**Velocity Render** completely rewires this balance. By dynamically synchronizing Minecraft's chunk world generation and client meshing pipeline with your instantaneous velocity vector, the game actively prioritizes the terrain ahead of your travel trajectory—eliminating high-speed void pop-in, preventing Elytra wall stalls, and keeping your journey butter-smooth.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

---

## ✨ Features (Version 1.0.0)

### 🏎️ Client-Side Anisotropic Meshing Prioritization
Stop waiting for forward chunks to appear while your computer renders terrain behind you:
- **Vanilla Limitation**: Vanilla's chunk compile queue (`SectionTaskDynamicQueue`) evaluates chunk tasks using purely spherical Euclidean distance from the camera. A chunk 50 blocks behind your back gets the exact same compile priority as a chunk 50 blocks in front of your face.
- **Directional Velocity Bias**: Calculates your instantaneous movement vector and applies an anisotropic dot-product distance bias. Sections inside your forward flight cone compile first, while rear sections are seamlessly deferred without visual glitches.
- **Zero-Allocation Hot Path**: Precomputed volatile caching ensures 0B heap memory allocation per frame on the client render thread.

### 🌐 Server-Side Predictive Chunk Generation
Smooth exploration across uncharted lands on dedicated servers and singleplayer worlds:
- **Kinematic Look-Ahead Vector Blending**: Blends player delta-movement momentum (70%) with camera look direction (30%). When you bank into a turn, chunks in the direction you are steering begin streaming immediately *before* momentum catches up.
- **Continuous Corridor Marching**: Uses continuous step-by-step corridor ray marching (with banked turn fan-out at speeds $\ge 0.75$ b/t), preventing diagonal chunk gaps.
- **Leak-Free Lifecycle**: Tickets are immediately released when slowing down, changing dimensions, or disconnecting.

### ⚖️ Adaptive MSPT Load Watchdog
High speed without sacrificing server performance:
- **Continuous Server Monitoring**: Continuously tracks server tick duration via `getAverageTickTimeNanos()`.
- **Automatic Load Shedding**: If server MSPT exceeds 25ms, forward reach dynamically scales down to protect server tick rate, guaranteeing rock-solid 20 TPS.

### 🔌 Dedicated Compatibility & HUD Integration
- **Universal Shaders & Performance Engines**: Because this mod purely optimizes CPU task ordering without altering graphical shaders or vertex formats, it is 100% compatible with **Iris**, **Sodium**, **ImmediatelyFast**, **Lithium**, **Nvidium**, **Distant Horizons**, and **Bobby**.
- **Server & Client Sidedness**:
  - *Client-Only on Vanilla Servers*: Prioritizes meshing of received chunks in your forward velocity cone.
  - *Server-Only with Vanilla Clients*: Predictively generates and loads terrain ahead of fast players on the server, eliminating void walls for all connecting players.
  - *Both*: Maximum synergy across generation and meshing pipelines.

---

## 📊 Quick Reference & Mechanics Matrix

| Mechanic | Vanilla Behavior | Velocity Render Enhancement |
| :--- | :--- | :--- |
| **Client Meshing Priority** | Radial Euclidean distance (all directions equal) | Anisotropic forward dot-product vector bias |
| **High-Speed Chunk Loading** | Loads radial ring around player; misses fast players | Predictive forward corridor tickets up to 16 chunks ahead |
| **Banked Turns (Elytra)** | Only tracks past momentum | 70/30 kinematic blend with camera look vector |
| **Server Load Protection** | Unchecked generation can cause server tick spikes | Adaptive MSPT watchdog throttles reach if MSPT > 25ms |
| **Client Hot-Path Memory** | Standard heap allocations | Volatile precomputed cache with 0B/frame heap allocation |

---

## 🛠️ In-Game Commands (`/velocityrender`, `/vr`)

Full in-game Brigadier command suite accessible under `/velocityrender` or the short alias `/vr`:

| Command | Permission | Description |
| :--- | :---: | :--- |
| `/vr status` | All Players | Displays real-time speed ($m/s$ & $b/t$), forward lead distance, active tickets, and server MSPT. |
| `/vr get <rule>` | All Players | Queries the current value of a configuration GameRule. |
| `/vr set <rule> <value>` | Gamemasters (Level 2) | Updates settings in real time. |
| `/vr reset` | Gamemasters (Level 2) | Restores all settings to vanilla defaults. |
| `/vr reload` | Gamemasters (Level 2) | Reloads active configuration. |

---

## ⚙️ Native GameRules & Configuration

> [!IMPORTANT]
> **💡 Config vs. In-Game GameRules:**
> The global configuration file only defines default values for newly created worlds. In existing worlds, change settings in-game via the **Edit Game Rules** UI screen or the `/gamerule` / `/vr` command.

Customize behavior per-world with standard dynamic GameRules:

- `velocityrender:enabled`: Master toggle for the entire prioritization system. (Default: `true`)
- `velocityrender:lead_multiplier`: Scales forward lead distance (0% to 300%). (Default: `100`)
- `velocityrender:budget_conservation`: Enables adaptive lateral and rear ticket trimming. (Default: `true`)
- `velocityrender:min_speed_threshold_pct`: Minimum speed in hundredths of a block/tick (0.20 b/t = 4.0 m/s) to activate forward bias. (Default: `20`)
- `velocityrender:debug_mode`: Enables developer diagnostic logging to server console. (Default: `false`)

---

## 📖 In-Depth How-To & Operational Playbook

### 1. Installation & Dependency Verification
- Ensure you have installed **Fabric Loader** (`>=0.18.4`), **Fabric API**, and **Dasik Library** (`>=1.8.0`).
- Drop `velocity-render-1.0.0+26.3.jar` into your `mods` folder.

### 2. Live In-Game Telemetry Verification
- Hop into your world, equip an Elytra or jump on an ice boat.
- Type `/vr status` while in motion to view your real-time speed in $b/t$ and $m/s$, active trajectory tickets, and current dynamic reach.

### 3. Tuning Forward Reach for High-Speed Transport
- If your server has powerful hardware and you want even farther Elytra lookahead, increase the multiplier:
  `/vr set lead_multiplier 150`
- If you run a heavily populated server and want conservative chunk generation, keep `budget_conservation` enabled:
  `/vr set budget_conservation true`

---

## ☕ Support

If you enjoy the **Vanilla Outsider** collection and want to support ongoing development, consider supporting me!

<p align="center">
    <a href="https://ko-fi.com/dasikigaijin"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
    <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
    <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

> [!NOTE]
> **🇮🇩 Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Credits & Modpack Permissions

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GPLv3 |
| **Source Code** | [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render) |

> [!IMPORTANT]
> **📦 Modpack Permissions & Distribution:**<br>
> You are fully welcome to include this mod in any modpack on any platform! However, the mod file must be downloaded directly through official distribution channels (**Modrinth** or **CurseForge**). Re-uploading, mirroring, or redistributing the original mod JAR to third-party mirror sites, scraper portals, or unauthorized launchers is strictly prohibited.
> <br><br>
> **⚖️ License & Fork Guidelines (No Zero-Change Re-uploads):**<br>
> This project is open-source under the **GNU GPLv3**. You are fully encouraged to inspect the code, learn from it, and fork the repository to create genuine modifications, substantial feature expansions, or community ports—provided your project remains open-source under GPLv3 with proper attribution.<br>
> **However, straight 1:1 re-uploads, clone forks with no meaningful functional changes, or re-publishing identical builds under different project names (e.g. to farm downloads or rewards) are strictly forbidden.**

---

<p align="center">
  <strong>Made with ❤️ for the Minecraft community</strong><br>
  <em>Part of the Vanilla Outsider Collection</em>
</p>
