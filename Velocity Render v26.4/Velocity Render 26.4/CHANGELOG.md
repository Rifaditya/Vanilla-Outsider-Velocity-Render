# 📜 Changelog: Velocity Render (MC 26.3)

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## [1.8.5+26.3] - 2026-10-13
- **Pillar 5 Sidedness & Mixin Contract Verification Suite**: Adopted comprehensive headless tests validating strict client/server code isolation, zero forbidden `net.minecraft.client.*` references in common sources and bytecode, and clean Mixin configuration separation.
- **MC 26.3 Toolchain & API Shift Resilience**: Parameterized `dasik_library_version` dependency in `build.gradle` and resolved the MC 26.3 `URI.create` API shift in `YaclScreenHelper`.
- **CommandSourceStack Headless Compatibility**: Updated command test harness to match MC 26.3 7-parameter constructor signature.
