# 🎛️ Master Release Queue: Vanilla Outsider — Velocity Render

> **Mod Project Master Ground-Truth Document**  
> *Last Synchronized: 2026-10-13*  
> **Modrinth ID**: `bfvgClpI` (`velocity-render`) | **CurseForge ID**: `1742373` (`vo-velocity-render`) | **Lead SemVer**: `1.8.5`

---

## 📊 Multi-Version Release Matrix & Queue Status

| Target MC | Generational Era | Live on Platforms | Next Queued Version | Status & Cadence Action | Feature Highlights / Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **MC 26.3** | Modern Lead | Modrinth: *(Under Review)*<br>CurseForge: *(Under Review)* | `1.8.5+26.3` | ⏸️ **Dual-Platform Moderation Lock** | Queued builds through 1.8.5+26.3 (5-Pillar testing harness & sidedness contract suite). Auto-publisher locked until initial platform moderation approvals are granted. |
| **MC 26.2** | Modern Sovereign | None | `1.8.5+26.2` | ⏸️ **Pending Release** | Modern sovereign anchor port; full feature parity, 5-pillar test harness. |
| **MC 26.1.2** | Modern Sovereign | None | `1.8.5+26.1.2` | ⏸️ **Pending Release** | Modern sovereign anchor port; CompileTaskDynamicQueue hook adaptation, 5-pillar test harness. |

---

## 🏛️ Project Operating Rules & Architectural Invariants

1. **1 Jar 1 Version Policy**:
   - Each targeted Minecraft version anchor maintains its own isolated subproject directory (`Velocity Render v26.3/`).
2. **Universal 1 Step = 1 SemVer Bump Law**:
   - Every applicable roadmap step increments SemVer.
