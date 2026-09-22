# 🚀 你跑得快，先渲染那兒 (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 基於速度向量偏置的各向異性區塊渲染與伺服端預生成優化模組

《你跑得快，先渲染那兒》是一款專為 Fabric 打造的高性能優化模組。透過將客戶端區塊網格化隊列與伺服端區塊生成票據直接與玩家即時速度向量同步，徹底解決高速飛行與狂奔時的區塊突然載入與虛空邊界。

---

## ⚡ 你跑得快，先渲染那兒 (Velocity Render) — 핵심 기능 / 主要特性

- **各向異性區塊網格化優先級**: 採用方向點積距離計算取代原版徑向排序，前進方向的區塊優先獲得編譯。
- **伺服端預測性區塊生成錐**: 沿運動方向預分配 PLAYER_LOADING 票據（最遠達 16 區塊），並配合 MSPT 監控保護 20 TPS。
- **動態遊戲規則與指令系統**: 提供完整的 velocityrender:* 遊戲規則與 /velocityrender 指令體系。
- **零 GC 分配超高效能**: 工作線程完全使用 volatile 原始型別快取，保證高效能零開銷。

---

## 🏛️ 版本選擇門戶

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki&#124;26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki&#124;26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki&#124;26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 技術概覽
- [[Overview|zh_tw-Overview]]
- [[English Portal|Home]]
