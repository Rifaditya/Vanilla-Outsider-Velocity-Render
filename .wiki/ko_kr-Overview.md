# 📚 빠르게 달린다면, 그곳을 먼저 렌더링하라 (Velocity Render) — 기술 개요





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ 1. 이방성 청크 메시 우선순위화
방향성 내적 거리 계산으로 진행 경로 앞쪽의 청크를 최우선 컴파일합니다.

### 数学公式 / Formulas:
$$\vec{v}_{\text{smooth}} = \alpha \cdot \vec{v}_{\text{eff}} + (1.0 - \alpha) \cdot \vec{v}_{\text{smooth}}, \quad \alpha = 0.65$$
$$\text{BiasedDistSqr} = \max\left(0.0,\, \text{DistSqr} - 2.0 \cdot (\vec{d}_{\text{norm}} \cdot \Delta\vec{P}) \cdot \text{LeadOffset}\right)$$

---

## 🌐 2. 서버 예측 청크 생성 콘
진행 방향으로 최대 16청크 앞까지 PLAYER_LOADING 티켓을 사전 할당합니다.

### Continuous MSPT Watchdog:
$$\text{msptFactor} = \begin{cases} 1.0 & \text{if } \text{mspt} \le 25.0 \\ \max\left(0.25,\, 1.0 - \frac{\text{mspt} - 25.0}{25.0}\right) & \text{if } \text{mspt} > 25.0 \end{cases}$$

---

## ⌨️ 3. 동적 게임 규칙 및 명령어
velocityrender:* 게임 규칙 및 /velocityrender 명령어로 실시간 제어가 가능합니다.

### 指令列表 / Commands:
- `/velocityrender help`
- `/velocityrender status`
- `/velocityrender get <rule>`
- `/velocityrender set <rule> <value>`
- `/velocityrender reset`
- `/velocityrender reload`

---

## 🏛️ 4. GC 할당 제로 고속 경로
렌더링 스레드에서 100% 힙 할당 없는 원시 타입 캐시를 사용합니다.

---

## 🔙 Navigation
- [[English Portal|Home]]
- [[English Portal|Home]]
