# 🚀 Corres rápido, renderiza eso primero (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 Priorización anisotrópica de chunks y generación predictiva basada en vector de velocidad

Velocity Render es un mod de optimización para Fabric diseñado para eliminar la aparición repentina de chunks y muros de vacío a altas velocidades.

---

## ⚡ Corres rápido, renderiza eso primero (Velocity Render) — 핵심 기능 / 主要特性

- **Priorización anisotrópica de renderizado**: Sustituye la ordenación radial por el producto escalar direccional.
- **Generación predictiva en servidor**: Asignación de tickets PLAYER_LOADING en cono de avance hasta 16 chunks.
- **GameRules dinámicos y comandos**: Control total mediante velocityrender:* y comandos /velocityrender.
- **Cero asignación de memoria GC**: Caché volátil ultrarrápida sin sobrecarga de memoria.

---

## 🏛️ Selector de versión de Minecraft

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki&#124;26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki&#124;26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki&#124;26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 Descripción general
- [[Overview|es_es-Overview]]
- [[English Portal|Home]]
