# 🚀 Tu cours vite, génère ça d'abord (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 Priorisation anisotrope du rendu et génération prédictive de chunks selon la vélocité

Velocity Render est un mod d'optimisation pour Fabric conçu pour éliminer l'apparition soudaine de chunks à grande vitesse.

---

## ⚡ Tu cours vite, génère ça d'abord (Velocity Render) — 핵심 기능 / 主要特性

- **Priorisation anisotrope du maillage**: Remplace le tri radial par un produit scalaire directionnel.
- **Génération prédictive côté serveur**: Allocation de tickets PLAYER_LOADING jusqu'à 16 chunks vers l'avant.
- **GameRules dynamiques et commandes**: Contrôle via velocityrender:* et la suite /velocityrender.
- **Zéro allocation d'objets (Zero GC)**: Structure ultra-optimisée sur types primitifs.

---

## 🏛️ Portail des versions Minecraft

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki&#124;26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki&#124;26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki&#124;26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 Vue d'ensemble
- [[Overview|fr_fr-Overview]]
- [[English Portal|Home]]
