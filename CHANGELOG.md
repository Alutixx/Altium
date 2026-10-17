[ReleaseTag]() is automatically replaced with the Altium release tag, e.g. altium-mc26.2-0.1.0-alpha.1
[MCVersion]() is automatically replaced with the Minecraft version, e.g. 26.2
[AltiumVersion]() is automatically replaced with the Altium version, e.g. 0.1.0-alpha.1
Everything above the line is ignored and not included in the changelog.
----------
Altium [AltiumVersion]() is an experimental performance-focused renderer build for Minecraft [MCVersion]().
This build tests fair, priority-aware chunk job scheduling on low-core-count CPUs.

- Separate important and normal chunk job queues
- Bound urgent-job bursts to reduce starvation of normal work
- Fabric-only prototype artifact
