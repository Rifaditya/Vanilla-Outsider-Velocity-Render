# 🚀 速く走るなら、そこを先に描画せよ (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 速度ベクトルに基づく異方性チャンク描画優先度およびサーバー予測生成最適化

Velocity Render は、高速移動時のチャンク抜けや虚空の壁を根絶するために設計された Fabric 向け最適化 MOD です。

---

## ⚡ 速く走るなら、そこを先に描画せよ (Velocity Render) — 핵심 기능 / 主要特性

- **異方性チャンクメッシュ優先度**: 従来の球状ソートを方向内積計算で置き換え、進行方向の地形を最優先でコンパイル。
- **サーバー側予測チャンク生成**: 進行方向に最大 16 チャンク先の PLAYER_LOADING チケットを自動割り当て。
- **動的ゲームルールとコマンド**: velocityrender:* ゲームルールと /velocityrender コマンドによる完全制御。
- **ゼロ GC メモリアーキテクチャ**: 描画ホットパスでのヒープ割り当てゼロを保証。

---

## 🏛️ Minecraft バージョン選択ポータル

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki&#124;26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki&#124;26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki&#124;26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 技術概要
- [[Overview|ja_jp-Overview]]
- [[English Portal|Home]]
