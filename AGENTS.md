# Scalable TNT

## Scope

Forge 1.20.1 mod that adds two shapeless sand/gunpowder TNT strengths while keeping the ordinary TNT block and vanilla fuse/ignition behavior. Its explosion travels through the ordinary Level explosion entry point so TNTUtils and Explosion Overhaul continue to observe it.

## Verification

Run `./gradlew verifyFast` for composition and resource checks. Run `./gradlew verifyFull stageRuntimeJar` before committing or deploying; the full gate requires the authored-variant and vanilla TNT GameTest to execute and pass. The deployable reobfuscated JAR is `build/libs/scalable-tnt-0.1.0.jar`. Pack deployment and pack suites follow the modpack's separate policy.

Do not commit or stage runtime output, local configs, logs, caches or generated worlds.
