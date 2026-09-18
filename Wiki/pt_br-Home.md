# 🚀 Você corre rápido, renderize isso primeiro (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 Priorização anisotrópica de malha de chunks e geração preditiva baseada em velocidade

Velocity Render é um mod de alta performance para Fabric que elimina o pop-in de chunks e paredes de vazio durante viagens rápidas.

---

## ⚡ Você corre rápido, renderize isso primeiro (Velocity Render) — 핵심 기능 / 主要特性

- **Priorização anisotrópica de renderização**: Substitui a ordenação radial pelo produto escalar direcional.
- **Geração preditiva no servidor**: Alocação de tickets PLAYER_LOADING até 16 chunks à frente.
- **GameRules dinâmicas e comandos**: Controle completo através de velocityrender:* e /velocityrender.
- **Zero alocação de memória GC**: Execução ultrarrápida lock-free sem custos de Garbage Collection.

---

## 🏛️ Seletor de versão do Minecraft

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki|26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki|26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki|26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 Visão Geral
- [[Overview|pt_br-Overview]]
- [[English Portal|Home]]
