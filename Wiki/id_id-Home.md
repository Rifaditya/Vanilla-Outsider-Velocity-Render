# 🚀 Kamu Lari Cepat, Render Itu Dulu (Velocity Render)





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 📖 Optimalisasi prioritas rendering chunk anisotropik dan generasi prediktif berbasis vektor kecepatan

Velocity Render adalah mod optimasi Fabric performa tinggi yang dirancang untuk menghilangkan pop-in chunk dan dinding hampa saat bepergian dengan kecepatan tinggi.

---

## ⚡ Kamu Lari Cepat, Render Itu Dulu (Velocity Render) — 핵심 기능 / 主要特性

- **Prioritas Meshing Chunk Anisotropik**: Menggantikan pengurutan radial vanilla dengan perhitungan dot product terarah.
- **Generasi Chunk Prediktif Server**: Mengalokasikan tiket PLAYER_LOADING sementara hingga 16 chunk di depan arah perjalanan.
- **GameRules Dinamis & Perintah**: Dukungan penuh GameRules velocityrender:* dan perintah Brigadier /velocityrender.
- **Nol Alokasi Memori GC**: Eksekusi lock-free berbasis tipe data primitif tanpa beban Garbage Collector.

---

## 🏛️ Portal Pilihan Versi Minecraft

| Minecraft Version | Documentation Link | Client Queue Mixin | Minimum Java |
| :--- | :--- | :--- | :--- |
| **Minecraft 26.3** | [[👉 Enter MC 26.3 Wiki|26.3-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.2** | [[👉 Enter MC 26.2 Wiki|26.2-Home]] | `SectionTaskDynamicQueue` | Java 25+ |
| **Minecraft 26.1** | [[👉 Enter MC 26.1 Wiki|26.1-Home]] | `CompileTaskDynamicQueue` | Java 25+ |

---

## 🔗 Ikhtisar Teknis
- [[Overview|id_id-Overview]]
- [[English Portal|Home]]
