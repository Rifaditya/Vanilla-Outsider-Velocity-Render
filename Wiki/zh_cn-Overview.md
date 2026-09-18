# 📚 你跑得快，先渲染那儿 (Velocity Render) — 技术概览





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ 1. 各向异性区块网格化优先级
利用方向点积距离计算替代原版欧几里得径向排序，运动路径前方的地形区块获得更低的距离分值，优先被工作线程编译。

### 数学公式 / Formulas:
$$\vec{v}_{\text{smooth}} = \alpha \cdot \vec{v}_{\text{eff}} + (1.0 - \alpha) \cdot \vec{v}_{\text{smooth}}, \quad \alpha = 0.65$$
$$\text{BiasedDistSqr} = \max\left(0.0,\, \text{DistSqr} - 2.0 \cdot (\vec{d}_{\text{norm}} \cdot \Delta\vec{P}) \cdot \text{LeadOffset}\right)$$

---

## 🌐 2. 服务端预测性区块生成锥
沿前进方向预分配 PLAYER_LOADING 临时票据（最远可达 16 个区块），配合持续 MSPT 监视器，平滑缩放预测半径以确保 20 TPS。

### Continuous MSPT Watchdog:
$$\text{msptFactor} = \begin{cases} 1.0 & \text{if } \text{mspt} \le 25.0 \\ \max\left(0.25,\, 1.0 - \frac{\text{mspt} - 25.0}{25.0}\right) & \text{if } \text{mspt} > 25.0 \end{cases}$$

---

## ⌨️ 3. 动态游戏规则与指令套件
支持命名空间游戏规则（velocityrender:enabled 等）与全面的 /velocityrender 指令套件（status, get, set, reset, reload）。

### 指令列表 / Commands:
- `/velocityrender help`
- `/velocityrender status`
- `/velocityrender get <rule>`
- `/velocityrender set <rule> <value>`
- `/velocityrender reset`
- `/velocityrender reload`

---

## 🏛️ 4. 零垃圾回收分配高频路径
渲染工作线程每秒执行数千次点积计算，全部通过 volatile 原始类型缓存读取，实现 100% 零堆内存分配。

---

## 🔙 Navigation
- [[English Portal|Home]]
- [[English Portal|Home]]
