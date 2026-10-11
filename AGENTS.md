# Scalable TNT

Forge 1.20.1 / Java 17; mod ID `scalable_tnt`.
Two shapeless sand/gunpowder strengths retain ordinary TNT/fuse/ignition and Level explosion
entry point, so TNTUtils/Explosion Overhaul observe native behavior.
Deterministic: `./gradlew verifyFast`; source/runtime gate: `./gradlew verifyFull stageRuntimeJar`
(authored-variant and vanilla TNT GameTests must execute/pass).
Artifact: `build/libs/scalable-tnt-0.1.0.jar`.

Read [shared workspace policy](../../better-content-modpack/docs/policies/workspace.md)
and its linked testing/disposal policies. Docs-only changes use the shared document check
and `git diff --check`, not unrelated runtime builds.
