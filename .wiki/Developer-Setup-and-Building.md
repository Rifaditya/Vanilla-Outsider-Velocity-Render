# 💻 Developer Setup & Building Guide





> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 🛠️ Development Environment Setup

This project uses modern Gradle with Fabric Loom targeting Java 25+. Each supported Minecraft version is maintained as an independent subproject with dedicated `build.gradle` and `gradle.properties` manifests.

### 1. Prerequisites
- **Java Development Kit (JDK)**: JDK 25 or higher (Eclipse Temurin, Microsoft OpenJDK, or Oracle GraalVM).
- **Git**: 2.40+
- **IDE**: IntelliJ IDEA 2024.3+ or Eclipse with Gradle Buildship.

### 2. Cloning the Repository
```bash
git clone https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render.git
cd "Vanilla-Outsider-Velocity-Render"
```

---

## 🏗️ Multi-Version Directory Layout

```
Vanilla-Outsider-Velocity-Render/
├── You Run Fast Render That First v26.1/
│   └── You Run Fast Render That First 26.1/
│       ├── build.gradle
│       ├── gradle.properties
│       └── src/
├── You Run Fast Render That First v26.2/
│   └── You Run Fast Render That First 26.2/
│       ├── build.gradle
│       ├── gradle.properties
│       └── src/
├── You Run Fast Render That First v26.3/
│   └── You Run Fast Render That First 26.3/
│       ├── build.gradle
│       ├── gradle.properties
│       └── src/
├── Wiki/
└── README.md
```

---

## 🚀 Building & Testing Commands

To build and test a specific Minecraft version anchor, navigate to its subproject directory:

### MC 26.3 Build & Test
```powershell
cd "You Run Fast Render That First v26.3\You Run Fast Render That First 26.3"
./gradlew check --no-daemon
./gradlew build --no-daemon
```

### MC 26.2 Build & Test
```powershell
cd "You Run Fast Render That First v26.2\You Run Fast Render That First 26.2"
./gradlew check --no-daemon
./gradlew build --no-daemon
```

### MC 26.1 Build & Test
```powershell
cd "You Run Fast Render That First v26.1\You Run Fast Render That First 26.1"
./gradlew check --no-daemon
./gradlew build --no-daemon
```

---

## 🧪 Automated Multi-Case Test Suite

Automated verification tests are located in `src/test/java/net/vanillaoutsider/velocityrender/AnisotropicMathTest.java`:

- `testForwardPriorityOverRear()`: Proves forward chunk section receives lower distance score than rear chunk.
- `testBelowSpeedThreshold()`: Verifies low speed ($< 0.20\text{ b/t}$) retains exact Euclidean distance.
- `testVelocityCalculator()`: Verifies exponential moving average ($\alpha = 0.65$) smoothing and unit vectors.
- `testBitPackedChunkPositions()`: Verifies zero-allocation 64-bit coordinate packing.
- `testMsptWatchdogFormula()`: Validates reach tapering under server lag.
- `testNullSafety()`: Ensures null origin or camera position returns `Double.MAX_VALUE`.

Run the test suite standalone:
```powershell
./gradlew test --info
```

---

## 🏛️ Engineering Standards & Code Hygiene

1. **License Header**: Every `.java` file must include the exact header:
   ```java
   // Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
   ```
2. **1 File 1 Purpose**: Classes, helpers, and mixins focus strictly on one cohesive responsibility.
3. **Zero Allocation in Hot Paths**: Render polling runs inside worker threads at 1000+ Hz. Never allocate objects (`new`, lambdas) inside `AnisotropicDistanceHelper.calculateBiasedDistanceSqr`.
