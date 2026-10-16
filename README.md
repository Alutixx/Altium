<p align="center">
  <img src="Icon-762x_128x128.png" width="128" alt="Altium icon">
</p>

<h1 align="center">Altium</h1>

<p align="center">
  A Sodium-based Minecraft rendering and performance project focused on making gameplay smoother on older and lower-end hardware.
</p>

> [!WARNING]
> **AI-assisted project:** AI tools are used to help with development, research, and code changes. Changes are reviewed and tested where possible, but AI assistance does not guarantee correctness. Please report bugs and regressions through the issue tracker.

## 🎯 Project goals

Altium is a fork of [Sodium](https://github.com/CaffeineMC/sodium), the Minecraft rendering optimization mod. The goal is to explore practical rendering and performance improvements, with particular interest in older CPUs and graphics hardware such as Intel HD Graphics 4000.

Areas of interest include:

- Reducing CPU overhead and unnecessary work in the rendering path.
- Improving frame pacing and reducing stutter, not just increasing average FPS.
- Keeping changes measurable and maintainable against upstream Sodium.
- Testing compatibility on older hardware and drivers where feasible.

These are development goals, not a promise that every system will see better performance. Results will vary by Minecraft version, mods, drivers, settings, and hardware.

## 📌 Project status

Altium is an actively modified fork. Performance changes should be considered experimental unless they are specifically documented and tested. No benchmark or compatibility claim is made here without published test results.

## 📥 Downloads

Check the repository's [Releases](https://github.com/Alutixx/Altium/releases) for published builds. If there is no release for your Minecraft version or mod loader, a compatible build may not be available yet.

## 🖥️ Installation

1. Install a Minecraft mod loader and Minecraft version supported by the specific Altium build.
2. Download the matching Altium release.
3. Place the mod JAR in your instance's `mods` directory.
4. Launch the game and check the log if it fails to load.

Do not assume a build supports every Minecraft version or both Fabric and NeoForge; check the release notes and included metadata.

## 🐛 Issues and feedback

Please use the [GitHub issue tracker](https://github.com/Alutixx/Altium/issues) to report bugs, crashes, compatibility problems, or reproducible performance regressions.

When reporting a performance issue, include your Minecraft version, mod loader, Java version, CPU and GPU, graphics driver, relevant mods, settings, and steps to reproduce the problem. Benchmark results are most useful when the test setup and method are included.

## 🛠️ Building from source

Altium uses Gradle. With a compatible JDK installed, run:

```bash
./gradlew build
```

On Windows, use `gradlew.bat build`. Build artifacts are produced in the project's Gradle build output directories.

Check the Gradle wrapper configuration and project build files for the exact Java and Gradle requirements for the current branch.

## 🙏 Upstream

Altium is based on [Sodium by CaffeineMC](https://github.com/CaffeineMC/sodium). Thanks to the Sodium contributors and the wider Minecraft modding community for their work. Altium is an independent fork and is not an official CaffeineMC project.

## 📜 License

Except where otherwise stated (see [third-party license notices](thirdparty/NOTICE.txt)), the content of this repository is provided
under the [Polyform Shield 1.0.0](LICENSE.md) license by [JellySquid](https://jellysquid.me).
