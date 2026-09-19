# 📜 Changelog: Velocity Render

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

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
