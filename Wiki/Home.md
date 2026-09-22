# 🚀 Vanilla Outsider: Velocity Render — Official Wiki

> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 🌐 Global Language Switchboard

Choose your language below to view translated documentation:

| Language | Home Page | Technical Overview |
| :--- | :--- | :--- |
| **English** | [[English Home|Home]] | [[Overview (26.3)|26.3-Home]] |
| 🇨🇳 **简体中文 (Simplified Chinese)** | [[主页|zh_cn-Home]] | [[技术概览|zh_cn-Overview]] |
| 🇭🇰 **繁體中文 (Traditional Chinese)** | [[主頁|zh_tw-Home]] | [[技術概覽|zh_tw-Overview]] |
| 🇷🇺 **Русский (Russian)** | [[Главная страница|ru_ru-Home]] | [[Технический обзор|ru_ru-Overview]] |
| 🇪🇸 **Español (Spanish)** | [[Inicio|es_es-Home]] | [[Descripción general|es_es-Overview]] |
| 🇩🇪 **Deutsch (German)** | [[Startseite|de_de-Home]] | [[Technische Übersicht|de_de-Overview]] |
| 🇫🇷 **Français (French)** | [[Accueil|fr_fr-Home]] | [[Vue d'ensemble|fr_fr-Overview]] |
| 🇧🇷 **Português (Portuguese)** | [[Início|pt_br-Home]] | [[Visão Geral|pt_br-Overview]] |
| 🇯🇵 **日本語 (Japanese)** | [[ホーム|ja_jp-Home]] | [[技術概要|ja_jp-Overview]] |
| 🇮🇩 **Bahasa Indonesia (Indonesian)** | [[Beranda|id_id-Home]] | [[Ikhtisar Teknis|id_id-Overview]] |
| 🇰🇷 **한국어 (Korean)** | [[홈|ko_kr-Home]] | [[기술 개요|ko_kr-Overview]] |

---

## 🧭 Welcome to the Official Documentation

**Vanilla Outsider: Velocity Render** is an ultra-high-performance Fabric optimization mod designed to eliminate high-speed chunk pop-in and void walls. By synchronizing client chunk render meshing queues and server world generation tickets directly with the player's real-time velocity vector ($\vec{v}$), the terrain you are flying or galloping toward is compiled and loaded first.

```
                  =============================================
                  VELOCITY-BIASED ANISOTROPIC CHUNK PIPELINE
                  =============================================

                                  [ PLAYER ]
                                      |
                     Velocity Vector  |  s >= 0.20 b/t
                                      v
                 +-----------------------------------------+
                 |       VelocityCalculator (EMA a=0.65)   |
                 +-----------------------------------------+
                                 /         \
                                /           \
        (Client Render Meshing)/             \(Server Chunk Tickets)
                              v               v
             +---------------------+     +--------------------------+
             | AnisotropicDistance |     | ForwardTicketManager     |
             | Helper (Dot Product)|     | (TicketType.             |
             | Directional Bias)   |     |  PLAYER_LOADING)         |
             +---------------------+     +--------------------------+
                        |                             |
                        v                             v
             Prioritized Chunk Mesh       Predictive Forward Chunks
             (Zero Pop-In Ahead)          (Ahead up to 16 Chunks)
```

---

## 🏛️ Select Your Minecraft Version

Select your Minecraft version below to enter its dedicated, isolated documentation tree:

| Minecraft Version | Version Tree Link | Engine Lifecycle | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki|26.3-Home]] | **Modern Lead** | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki|26.2-Home]] | **Modern Predecessor** | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki|26.1-Home]] | **Modern Anchor (26.1.2)** | `CompileTaskDynamicQueue` | Java 25+ |

---

## ⚡ Key Architectural Subsystems

Every supported version features four dedicated subsystem engineering domains:

1. **[[Anisotropic Chunk Prioritization|26.3-Anisotropic-Prioritization]]**:
   Replaces vanilla's Euclidean radial sorting with directional dot-product distance calculation. Chunks in your trajectory vector receive dramatically reduced distance scores, forcing chunk compile worker threads to mesh forward terrain first.

2. **[[Predictive Chunk Generation Biasing|26.3-Chunk-Generation-Biasing]]**:
   Server-side trajectory raycaster that allocates temporary `PLAYER_LOADING` tickets along your forward velocity cone. Coupled with the Continuous MSPT Watchdog, it adapts lookahead reach from 4 to 16 chunks to preserve 20 TPS.

3. **[[Dynamic Configuration & GameRules|26.3-Configuration-and-GameRules]]**:
   Full real-time in-game control via namespaced `velocityrender:*` GameRules and the `/velocityrender` Brigadier command suite (`help`, `status`, `get`, `set`, `reset`, `reload`).

4. **[[Technical Architecture & Mixins|26.3-Architecture-and-Mixins]]**:
   Deep technical breakdown of package architectures, zero-allocation primitive FastUtil collections, volatile cache variables, and version-specific Mixin targets.

---

## 🛠️ General Information & Links

- **Full Version Matrix**: [[Version Compatibility Matrix|Version-Compatibility]]
- **Compiling & Building**: [[Developer Setup and Building Guide|Developer-Setup-and-Building]]
- **Source Code Repository**: [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render)
- **Official Mod Distribution**: [Modrinth Project](https://modrinth.com/mod/velocity-render)
- **Author Attribution**: Developed and maintained by **Dasik (Rifaditya)**
- **License**: [GNU General Public License v3.0 (GPLv3)](https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render/blob/main/LICENSE)
