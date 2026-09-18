# 🚀 Du rennst schnell, rendere das zuerst (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 Geschwindigkeitsvektor-basierte anisotrope Chunk-Rendering- und Vorhersageoptimierung

Velocity Render ist eine Hochleistungs-Fabric-Optimierungsmodifikation, die Chunk-Pop-in bei hohen Geschwindigkeiten vollständig eliminiert.

---

## ⚡ Du rennst schnell, rendere das zuerst (Velocity Render) — 핵심 기능 / 主要特性

- **Anisotrope Chunk-Priorisierung**: Ersetzt euklidische Sortierung durch direktionales Skalarprodukt.
- **Server-Chunk-Vorhersage**: Weist vorauseilende PLAYER_LOADING-Tickets bis zu 16 Chunks im Voraus zu.
- **Dynamische GameRules & Befehle**: Umfassende Steuerung über velocityrender:* und /velocityrender.
- **Zero-Allocation GC Architektur**: Lock-free Cache mit primitiven Datentypen.

---

## 🏛️ Minecraft-Versionsauswahl

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki|26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki|26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki|26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 Technische Übersicht
- [[Overview|de_de-Overview]]
- [[English Portal|Home]]
