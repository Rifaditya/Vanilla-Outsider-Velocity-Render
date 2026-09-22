# 🚀 你跑得快，先渲染那儿 (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 基于速度向量偏置的各向异性区块渲染与服务端预生成优化模组

《你跑得快，先渲染那儿》是一款面向 Fabric 的终极性能优化模组。通过将客户端区块网格化队列与服务端区块生成票据与玩家的实时速度向量直接同步，彻底消除高速飞行或疾跑时的区块突然载入与虚空墙。

---

## ⚡ 你跑得快，先渲染那儿 (Velocity Render) — 핵심 기능 / 主要特性

- **各向异性区块网格化优先级**: 利用方向点积距离计算替代原版欧几里得径向排序，运动路径前方的地形区块获得更低的距离分值，优先被工作线程编译。
- **服务端预测性区块生成锥**: 沿前进方向预分配 PLAYER_LOADING 临时票据（最远可达 16 个区块），配合持续 MSPT 监视器，平滑缩放预测半径以确保 20 TPS。
- **动态游戏规则与指令套件**: 支持命名空间游戏规则（velocityrender:enabled 等）与全面的 /velocityrender 指令套件（status, get, set, reset, reload）。
- **零垃圾回收分配高频路径**: 渲染工作线程每秒执行数千次点积计算，全部通过 volatile 原始类型缓存读取，实现 100% 零堆内存分配。

---

## 🏛️ 版本选择门户

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki&#124;26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki&#124;26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki&#124;26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 技术概览
- [[Overview|zh_cn-Overview]]
- [[English Portal|Home]]
