# 📌 Velocity Render Backlog

<!-- Highest Historical ID: BL-VR-017 (Next Ticket Must Start at BL-VR-018) -->

This file tracks planned features, technical refinements, performance optimizations, and deferred improvements for **Velocity Render**.

---

## 📊 Backlog Summary

| ID | Category | Title | Priority | Target Version | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `[BL-VR-010]` | `[FEATURE]` | Multi-Era Anchor Porting: Velocity Render (Master Parity Initiative) | `[HIGH]` | `All Anchors` | `📌 DEFERRED` |
| `[BL-VR-013]` | `[FEATURE]` | Multi-Era Anchor Port: MC 26.2 (`Velocity Render v26.2`) | `[HIGH]` | `MC 26.2` | `✅ RESOLVED` |
| `[BL-VR-014]` | `[FEATURE]` | Multi-Era Anchor Port: MC 26.1.2 (`Velocity Render v26.1.2`) | `[HIGH]` | `MC 26.1.2` | `📌 DEFERRED` |
| `[BL-VR-015]` | `[FEATURE]` | Multi-Era Anchor Port: MC 1.21.11 (`Velocity Render v1.21.11`) | `[HIGH]` | `MC 1.21.11` | `📌 DEFERRED` |
| `[BL-VR-016]` | `[FEATURE]` | Multi-Era Anchor Port: MC 1.21.1 (`Velocity Render v1.21.1`) | `[HIGH]` | `MC 1.21.1` | `📌 DEFERRED` |
| `[BL-VR-017]` | `[FEATURE]` | Multi-Era Anchor Port: MC 1.20.1 (`Velocity Render v1.20.1`) | `[HIGH]` | `MC 1.20.1` | `📌 DEFERRED` |

---

## 🏷 Legend & Status Tags
- **Categories**: `[FEATURE]`, `[REFINEMENT]`, `[BUGFIX]`, `[PERF]`, `[TECH_DEBT]`
- **Priorities**: `[HIGH]` (Critical logic/porting task), `[MEDIUM]` (Quality of life), `[LOW]` (Minor polish)
- **Statuses**: `📌 DEFERRED` (Queued for future work), `🚧 IN_PROGRESS` (Active development), `✅ RESOLVED` (Implemented and verified)

---

## 📝 Detailed Backlog Entries

### [BL-VR-010] Multi-Era Anchor Porting: Velocity Render (Master Parity Initiative)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `All Anchors`
- **Status**: `📌 DEFERRED`
- **Date Added**: 2026-10-01
- **Target Component(s)**: Multi-subproject directories, `build.gradle`, `fabric.mod.json`, `RELEASE_QUEUE.md`

#### ❓ Problem / Context
Velocity Render is currently established strictly on modern sovereign lead **MC 26.3** (`Velocity Render v26.3`, v1.8.5+26.3). Per the **Multi-Era Anchor Parity Law** and **1 Jar 1 Version Policy**, the studio mandates parity across all 6 active version anchors (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2`, `26.3+`).

Per the **Whole-Backlog Pipelining & Ticket Splitting Audit Law**, this master initiative is decomposed into dedicated atomic companion tickets per compiler/toolchain era:
- **Phase 1 (Modern Sovereign Anchors)**: `[BL-VR-013]` (MC 26.2) and `[BL-VR-014]` (MC 26.1.2).
- **Phase 2 (Older Anchors)**: `[BL-VR-015]` (MC 1.21.11), `[BL-VR-016]` (MC 1.21.1), and `[BL-VR-017]` (MC 1.20.1).

#### 🧪 Verification & Acceptance Criteria
- [ ] All 5 companion port tickets (`[BL-VR-013]` through `[BL-VR-017]`) successfully compiled, tested, and deployed to 4-point archives.
- [ ] Subproject queues and changelogs registered.
- [ ] `MASTER_ANCHOR_BOARD.md` synchronized to 6/6 anchors (100% parity).

---

### [BL-VR-013] Multi-Era Anchor Port: MC 26.2 (`Velocity Render v26.2`)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 26.2`
- **Status**: `✅ RESOLVED`
- **Date Added**: 2026-10-13
- **Date Resolved**: 2026-10-13 (v1.8.5+26.2)
- **Target Component(s)**: `Velocity Render v26.2/Velocity Render 26.2/`

#### ❓ Problem / Context
Anchor MC 26.2 requires dedicated subproject scaffolding under the 1 Jar 1 Version policy. Shares identical modern runtime architecture with MC 26.3 (`SectionTaskDynamicQueue`, `Identifier.fromNamespaceAndPath`, `DynamicGameRuleManager`, Java 25 / Loom 1.15+).

#### 💡 Proposed Solution & Technical Specifications
1. Scaffold `Velocity Render v26.2/Velocity Render 26.2/` subproject directory.
2. Mirror full feature suite from 26.3 reference (`src/main/java`, `src/main/resources`, `src/test/java`).
3. Set `mod_version = 1.8.5+26.2` in `gradle.properties` and configure Loom for Minecraft 26.2.
4. Verify all 15 test suites / 78 tests pass cleanly on `./gradlew test`.
5. Compile `velocity-render-1.8.5+26.2.jar` and deploy to 4-point archive locations.
6. Initialize subproject `RELEASE_QUEUE.md` and `CHANGELOG.md` with target anchor headers.

#### 🧪 Verification & Acceptance Criteria
- [x] Headless test suite passes cleanly on `./gradlew test` (78/78 tests pass).
- [x] Clean binary compilation (`./gradlew build`).
- [x] Mandatory 4-point distribution (Local Archive, Hub Archive, External Vault `D:\`, Launcher Profile).
- [x] Queue registered in subproject `RELEASE_QUEUE.md` (`- [ ] **1.8.5+26.2**`).

---

### [BL-VR-014] Multi-Era Anchor Port: MC 26.1.2 (`Velocity Render v26.1.2`)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 26.1.2`
- **Status**: `📌 DEFERRED`
- **Date Added**: 2026-10-13
- **Target Component(s)**: `Velocity Render v26.1.2/Velocity Render 26.1.2/`

#### ❓ Problem / Context
Anchor MC 26.1.2 requires subproject scaffolding and chunk compilation hook adaptation: in MC 26.1, `SectionTaskDynamicQueue` is named `CompileTaskDynamicQueue`.

#### 💡 Proposed Solution & Technical Specifications
1. Scaffold `Velocity Render v26.1.2/Velocity Render 26.1.2/` subproject directory.
2. Adapt chunk dynamic queue Mixin from `SectionTaskDynamicQueue` to `CompileTaskDynamicQueue` targeting `poll()`.
3. Configure `dasik-library` 26.1 bindings and DynamicGameRule registrations.
4. Execute test suite on `./gradlew test`.
5. Compile `velocity-render-1.8.5+26.1.2.jar` and deploy to 4-point archive locations.
6. Initialize subproject `RELEASE_QUEUE.md` and `CHANGELOG.md`.

#### 🧪 Verification & Acceptance Criteria
- [ ] Headless test suite passes cleanly on `./gradlew test`.
- [ ] Clean binary compilation (`./gradlew build`).
- [ ] Mandatory 4-point distribution.
- [ ] Queue registered in subproject `RELEASE_QUEUE.md` (`- [ ] **1.8.5+26.1.2**`).

---

### [BL-VR-015] Multi-Era Anchor Port: MC 1.21.11 (`Velocity Render v1.21.11`)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 1.21.11`
- **Status**: `📌 DEFERRED`
- **Date Added**: 2026-10-13
- **Target Component(s)**: `Velocity Render v1.21.11/Velocity Render 1.21.11/`

#### ❓ Problem / Context
Anchor MC 1.21.11 requires Java 21, Loom 1.15-SNAPSHOT (`fabric-loom-remap`), Mojang mappings, `Identifier.of`, and chunk meshing queue adaptation.

#### 💡 Proposed Solution & Technical Specifications
1. Scaffold `Velocity Render v1.21.11/Velocity Render 1.21.11/` subproject directory.
2. Adapt chunk meshing injection hook and server ticket management for 1.21.11 engine.
3. Configure `dasik-library` 1.21.11 dependencies and DynamicGameRule registrations.
4. Execute test suite on `./gradlew test`.
5. Compile `velocity-render-1.0.0+1.21.11.jar` and deploy to 4-point archive locations.
6. Initialize subproject `RELEASE_QUEUE.md` and `CHANGELOG.md`.

#### 🧪 Verification & Acceptance Criteria
- [ ] Headless test suite passes cleanly on `./gradlew test`.
- [ ] Clean binary compilation (`./gradlew build`).
- [ ] Mandatory 4-point distribution.
- [ ] Queue registered in subproject `RELEASE_QUEUE.md`.

---

### [BL-VR-016] Multi-Era Anchor Port: MC 1.21.1 (`Velocity Render v1.21.1`)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 1.21.1`
- **Status**: `📌 DEFERRED`
- **Date Added**: 2026-10-13
- **Target Component(s)**: `Velocity Render v1.21.1/Velocity Render 1.21.1/`

#### ❓ Problem / Context
Anchor MC 1.21.1 requires Java 21, Loom 1.10+, Mojang mappings, `Identifier.of`, `DataComponents`, and chunk meshing queue adaptation.

#### 💡 Proposed Solution & Technical Specifications
1. Scaffold `Velocity Render v1.21.1/Velocity Render 1.21.1/` subproject directory.
2. Adapt chunk meshing queue and server ticket manager for 1.21.1 engine.
3. Configure `dasik-library` 1.21.1 dependencies and GameRule registrations.
4. Execute test suite on `./gradlew test`.
5. Compile `velocity-render-1.0.0+1.21.1.jar` and deploy to 4-point archive locations.
6. Initialize subproject `RELEASE_QUEUE.md` and `CHANGELOG.md`.

#### 🧪 Verification & Acceptance Criteria
- [ ] Headless test suite passes cleanly on `./gradlew test`.
- [ ] Clean binary compilation (`./gradlew build`).
- [ ] Mandatory 4-point distribution.
- [ ] Queue registered in subproject `RELEASE_QUEUE.md`.

---

### [BL-VR-017] Multi-Era Anchor Port: MC 1.20.1 (`Velocity Render v1.20.1`)
- **Category**: `[FEATURE]`
- **Priority**: `[HIGH]`
- **Target Version**: `MC 1.20.1`
- **Status**: `📌 DEFERRED`
- **Date Added**: 2026-10-13
- **Target Component(s)**: `Velocity Render v1.20.1/Velocity Render 1.20.1/`

#### ❓ Problem / Context
Anchor MC 1.20.1 requires Java 17, `compatibilityLevel = "JAVA_17"` in mixins, Loom 1.4-1.10, Mojang mappings, `new Identifier`, primitive NBT CompoundTag, and legacy chunk meshing hooks.

#### 💡 Proposed Solution & Technical Specifications
1. Scaffold `Velocity Render v1.20.1/Velocity Render 1.20.1/` subproject directory.
2. Configure `compatibilityLevel = "JAVA_17"` across mixins JSON.
3. Adapt chunk render queue and ticket manager for 1.20.1 engine.
4. Configure `dasik-library` 1.20.1 dependencies.
5. Execute test suite on `./gradlew test`.
6. Compile `velocity-render-1.0.0+1.20.1.jar` and deploy to 4-point archive locations.
7. Initialize subproject `RELEASE_QUEUE.md` and `CHANGELOG.md`.

#### 🧪 Verification & Acceptance Criteria
- [ ] Headless test suite passes cleanly on `./gradlew test`.
- [ ] Clean binary compilation (`./gradlew build`).
- [ ] Mandatory 4-point distribution.
- [ ] Queue registered in subproject `RELEASE_QUEUE.md`.
