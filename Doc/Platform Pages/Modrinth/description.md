<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.3-brightgreen?style=for-the-badge" alt="Minecraft 26.3">
</p>

# ⚡ Velocity Render

> **"Render what's ahead, not what's behind."**

**Velocity Render** is a lightweight, zero-overhead Fabric performance mod for Minecraft **26.3** that synchronizes chunk world generation and client chunk meshing with real-time player velocity, prioritizing terrain ahead of fast-traveling players to eliminate high-speed void pop-in and chunk loading stalls.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

---

## ✨ Features (Version 1.0.0)

### 🏎️ Client-Side Velocity-Biased Chunk Meshing
- **Anisotropic Prioritization**: Computes directional dot-product distance bias. Sections directly in front of your flight cone receive higher meshing priority, compiling forward terrain first while seamlessly deferring rear sections without visual disruption.
- **Zero-Allocation Hot Path**: Lock-free volatile caching ensures 0B heap memory allocation per frame on the client render thread.

### 🌐 Server-Side Predictive Chunk Generation
- **Kinematic Look-Ahead Vector Blending**: Blends player movement momentum (70%) with camera look direction (30%) for instant forward chunk streaming during banked flight turns.
- **Continuous Corridor Ray March**: Eliminates diagonal chunk gaps along the flight trajectory line.

### ⚖️ Adaptive MSPT Load Watchdog
- **Rock-Solid 20 TPS**: Continuously monitors server tick duration (`getAverageTickTimeNanos()`). If MSPT exceeds 25ms, dynamically tapers forward reach to protect server TPS.

---

## 🛠️ In-Game Commands (`/velocityrender`, `/vr`)

| Command | Description |
| :--- | :--- |
| `/vr status` | Displays real-time speed, dynamic reach, MSPT, and active tickets. |
| `/vr get <gamerule>` | Queries configuration values. |
| `/vr set <gamerule> <value>` | Updates settings in real time. |
| `/vr reset` | Restores default settings. |
| `/vr reload` | Reloads active configuration. |

---

## ⚙️ Native Dynamic GameRules

- `velocityrender:enabled`: Master toggle for the entire prioritization system. (Default: `true`)
- `velocityrender:lead_multiplier`: Scales forward lead distance (0% to 300%). (Default: `100`)
- `velocityrender:budget_conservation`: Enables adaptive lateral and rear ticket trimming. (Default: `true`)
- `velocityrender:min_speed_threshold_pct`: Minimum speed in hundredths of a block/tick (0.20 b/t) to activate bias. (Default: `20`)
- `velocityrender:debug_mode`: Enables developer diagnostic logging. (Default: `false`)

---

## ☕ Support

If you enjoy the **Vanilla Outsider** collection, consider supporting the next update!

<p align="center">
    <a href="https://ko-fi.com/dasikigaijin"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
</p>

---

## 📜 Credits

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GPLv3 |
