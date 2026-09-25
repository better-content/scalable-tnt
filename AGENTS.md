# Scalable TNT

## Scope

Forge 1.20.1 mod that adds two shapeless sand/gunpowder TNT strengths while keeping the ordinary TNT block and vanilla fuse/ignition behavior. Its explosion travels through the ordinary Level explosion entry point so TNTUtils and Explosion Overhaul continue to observe it.

## Verification

The unit-only authoring phase may run `./gradlew test` and pure source/resource contract tests. Do not run GameTests, Minecraft, pack suites, candidate assembly, deployment or release unless separately authorized. Full repository validation is `./gradlew verifyFast` followed by `./gradlew verifyFull`; both exceed the current unit-only limit.

Do not commit or stage runtime output, local configs, logs, caches or generated worlds.
