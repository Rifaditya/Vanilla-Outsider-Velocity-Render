# 📚 Você corre rápido, renderize isso primeiro (Velocity Render) — Visão Geral





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ 1. Priorização anisotrópica de renderização
Substitui a ordenação radial pelo produto escalar direcional.

### 数学公式 / Formulas:
$$\vec{v}_{\text{smooth}} = \alpha \cdot \vec{v}_{\text{eff}} + (1.0 - \alpha) \cdot \vec{v}_{\text{smooth}}, \quad \alpha = 0.65$$
$$\text{BiasedDistSqr} = \max\left(0.0,\, \text{DistSqr} - 2.0 \cdot (\vec{d}_{\text{norm}} \cdot \Delta\vec{P}) \cdot \text{LeadOffset}\right)$$

---

## 🌐 2. Geração preditiva no servidor
Alocação de tickets PLAYER_LOADING até 16 chunks à frente.

### Continuous MSPT Watchdog:
$$\text{msptFactor} = \begin{cases} 1.0 & \text{if } \text{mspt} \le 25.0 \\ \max\left(0.25,\, 1.0 - \frac{\text{mspt} - 25.0}{25.0}\right) & \text{if } \text{mspt} > 25.0 \end{cases}$$

---

## ⌨️ 3. GameRules dinâmicas e comandos
Controle completo através de velocityrender:* e /velocityrender.

### 指令列表 / Commands:
- `/velocityrender help`
- `/velocityrender status`
- `/velocityrender get <rule>`
- `/velocityrender set <rule> <value>`
- `/velocityrender reset`
- `/velocityrender reload`

---

## 🏛️ 4. Zero alocação de memória GC
Execução ultrarrápida lock-free sem custos de Garbage Collection.

---

## 🔙 Navigation
- [[English Portal|Home]]
- [[English Portal|Home]]
