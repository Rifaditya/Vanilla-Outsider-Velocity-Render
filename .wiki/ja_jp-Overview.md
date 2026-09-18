# 📚 速く走るなら、そこを先に描画せよ (Velocity Render) — 技術概要





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ 1. 異方性チャンクメッシュ優先度
従来の球状ソートを方向内積計算で置き換え、進行方向の地形を最優先でコンパイル。

### 数学公式 / Formulas:
$$\vec{v}_{\text{smooth}} = \alpha \cdot \vec{v}_{\text{eff}} + (1.0 - \alpha) \cdot \vec{v}_{\text{smooth}}, \quad \alpha = 0.65$$
$$\text{BiasedDistSqr} = \max\left(0.0,\, \text{DistSqr} - 2.0 \cdot (\vec{d}_{\text{norm}} \cdot \Delta\vec{P}) \cdot \text{LeadOffset}\right)$$

---

## 🌐 2. サーバー側予測チャンク生成
進行方向に最大 16 チャンク先の PLAYER_LOADING チケットを自動割り当て。

### Continuous MSPT Watchdog:
$$\text{msptFactor} = \begin{cases} 1.0 & \text{if } \text{mspt} \le 25.0 \\ \max\left(0.25,\, 1.0 - \frac{\text{mspt} - 25.0}{25.0}\right) & \text{if } \text{mspt} > 25.0 \end{cases}$$

---

## ⌨️ 3. 動的ゲームルールとコマンド
velocityrender:* ゲームルールと /velocityrender コマンドによる完全制御。

### 指令列表 / Commands:
- `/velocityrender help`
- `/velocityrender status`
- `/velocityrender get <rule>`
- `/velocityrender set <rule> <value>`
- `/velocityrender reset`
- `/velocityrender reload`

---

## 🏛️ 4. ゼロ GC メモリアーキテクチャ
描画ホットパスでのヒープ割り当てゼロを保証。

---

## 🔙 Navigation
- [[English Portal|Home]]
- [[English Portal|Home]]
