# ⚡ Velocity Render

> **"Render what's ahead, not what's behind."**

**Velocity Render** is a lightweight, zero-overhead Fabric mod for modern Minecraft that synchronizes chunk world generation and client chunk meshing with real-time player velocity, prioritizing terrain ahead of fast-traveling players to eliminate high-speed void stalls and chunk pop-in.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

## Features
- **Client Anisotropic Meshing**: Biases chunk compile task sorting along your movement vector.
- **Server Predictive Generation**: Issues forward `PLAYER_LOADING` tickets for fast travelers (Elytra, ice boats, galloping horses).
- **MSPT Watchdog**: Automatically throttles lead distance if server tick duration spikes, keeping tick rate at 20 TPS.
- **Commands & GameRules**: In-game control via `/velocityrender` or `/vr`, backed by native GameRules.

## License
GNU General Public License v3.0 (GPLv3).
