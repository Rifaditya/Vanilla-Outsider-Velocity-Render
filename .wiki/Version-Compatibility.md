# 🧭 Version Compatibility Matrix & Environmental Specifications





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📊 Minecraft Multi-Version Support Matrix

**Vanilla Outsider: Velocity Render** strictly adheres to the **1 Jar 1 Version Policy**. Each supported Minecraft release is maintained in its sovereign subproject directory to guarantee compile-time and runtime lockstep parity.

| Target Minecraft | Status | Toolchain | Loom Version | Client Queue Target | Mod Dependency |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **MC 26.3** | **Modern Lead** | Java 25+ | `net.fabricmc.fabric-loom 1.15+` | `SectionTaskDynamicQueue` | `dasik-library >= 1.8.0` |
| **MC 26.2** | **Modern Predecessor** | Java 25+ | `net.fabricmc.fabric-loom 1.15+` | `SectionTaskDynamicQueue` | `dasik-library >= 1.8.0` |
| **MC 26.1 (26.1.2)** | **Modern Anchor** | Java 25+ | `net.fabricmc.fabric-loom 1.15+` | `CompileTaskDynamicQueue` | `dasik-library >= 1.8.0` |

---

## ⚙️ Runtime & Environment Dependencies

| Component | Minimum Bound | Recommended Version | Technical Notes |
| :--- | :--- | :--- | :--- |
| **Fabric Loader** | `>= 0.18.4` | Latest Stable | Required for Java 25 bytecode loading and modern Mixin 0.8+ support |
| **Fabric API** | `*` (Any matching MC version) | Latest Release | Provides lifecycle events (`ServerTickEvents`, `ClientTickEvents`, `CommandRegistrationCallback`) |
| **Dasik Library** | `>= 1.8.0` | 1.8.0+ | Powers namespaced dynamic GameRule management and reflection-safe category registry unfreezing |
| **Java Virtual Machine** | `Java 25` | JDK 25 LTS | Modern foreign memory, vector API compatibility, and high-performance GC |

---

## 🎨 Graphics Pipeline & Shader Compatibility

### 1. Sodium / Embeddium / Iris Shaders
- **100% Fully Compatible**: You Run Fast operates exclusively on:
  1. Section task scheduling priority (`SectionTaskDynamicQueue` / `CompileTaskDynamicQueue`).
  2. Server ticket loading queues (`ServerChunkCache` / `TicketType.PLAYER_LOADING`).
- It does **not** alter vertex formats, geometry pipelines, or shader uniform buffers. As a result, modern rendering engines like Sodium and Iris run alongside You Run Fast without any visual artifacts.

### 2. Canvas & Nvidium
- **Compatible**: Task queue injection intercepts candidate section polling without interfering with Nvidium mesh compilation or Canvas pipeline passes.

### 3. OptiFine
- **Incompatible**: OptiFine relies on closed-source, monolithic class replacements that completely overwrite Minecraft's chunk rendering pipeline, breaking Fabric Mixin injection points. OptiFine is neither tested nor supported.

---

## 🗂️ Version Tree Navigation

Select your version for detailed mechanics:
- [[👉 Explore MC 26.3 Documentation|26.3-Home]]
- [[👉 Explore MC 26.2 Documentation|26.2-Home]]
- [[👉 Explore MC 26.1 Documentation|26.1-Home]]
