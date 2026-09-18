# 📚 你跑得快，先渲染那兒 (Velocity Render) — 技術概覽





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ 1. 各向異性區塊網格化優先級
採用方向點積距離計算取代原版徑向排序，前進方向的區塊優先獲得編譯。

### 数学公式 / Formulas:
$$\vec{v}_{\text{smooth}} = \alpha \cdot \vec{v}_{\text{eff}} + (1.0 - \alpha) \cdot \vec{v}_{\text{smooth}}, \quad \alpha = 0.65$$
$$\text{BiasedDistSqr} = \max\left(0.0,\, \text{DistSqr} - 2.0 \cdot (\vec{d}_{\text{norm}} \cdot \Delta\vec{P}) \cdot \text{LeadOffset}\right)$$

---

## 🌐 2. 伺服端預測性區塊生成錐
沿運動方向預分配 PLAYER_LOADING 票據（最遠達 16 區塊），並配合 MSPT 監控保護 20 TPS。

### Continuous MSPT Watchdog:
$$\text{msptFactor} = \begin{cases} 1.0 & \text{if } \text{mspt} \le 25.0 \\ \max\left(0.25,\, 1.0 - \frac{\text{mspt} - 25.0}{25.0}\right) & \text{if } \text{mspt} > 25.0 \end{cases}$$

---

## ⌨️ 3. 動態遊戲規則與指令系統
提供完整的 velocityrender:* 遊戲規則與 /velocityrender 指令體系。

### 指令列表 / Commands:
- `/velocityrender help`
- `/velocityrender status`
- `/velocityrender get <rule>`
- `/velocityrender set <rule> <value>`
- `/velocityrender reset`
- `/velocityrender reload`

---

## 🏛️ 4. 零 GC 分配超高效能
工作線程完全使用 volatile 原始型別快取，保證高效能零開銷。

---

## 🔙 Navigation
- [[English Portal|Home]]
- [[English Portal|Home]]
