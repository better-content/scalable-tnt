# Scalable TNT

## Native fixture verification

`verifyFast` includes absent-settings/preserve-existing and strict ERROR/FATAL guard regressions.
Before source GameTest bootstrap, the actual working directory receives a defaults-only
`server.properties` only if absent; existing settings are never replaced. The full native gate
rejects ERROR/FATAL diagnostics even when required tests and the process report success.

## Playtest logging

`bc.playtest.scalable_tnt.priming` records the actual native insertion result;
`.explosion` records only the native power-argument handoff, not explosion completion.
These bounded DEBUG records share the existing primed entity UUID and dimension to correlate
chain reactions. They contain no owner/player identity or entity NBT. A focused TRACE can include
that entity's block position: set `bc.playtest.trace.scalable_tnt.dimension` and exactly one of
`.subject` (entity UUID) or `.pos` (`x,y,z`), and enable the native TRACE logger. It arms on the first
match for at most 60 seconds/1,000 records, once per process. Ordinary DEBUG/TRACE share 120/minute,
burst 20, 256 aggregation keys and 2,048-character records. Native tick boundaries emit coalesced
30-second summaries; server stop/client disconnect clear transient keys without rearming allowances.
Startup INFO identifies loaded version, effective mode and limits; unavailable source identity is
`UNKNOWN`. Pure emitter/contracts and the existing native authored/vanilla TNT assertions are
complementary checks, not full-pack acceptance.

## Scope

Adds two shapeless sand/gunpowder compositions alongside vanilla TNT:

- Six ordinary sand and three gunpowder produce Low-yield TNT (blast power 3.0).
- Three ordinary sand and six gunpowder produce High-yield TNT (blast power 4.5).

Both recipes use nine total inputs and the pack's `kubejs:ordinary_sand` tag. Vanilla TNT stays at its vanilla blast power of 4.0. Variant strength is stored on the primed vanilla TNT entity, so the vanilla fuse and entity lifecycle remain in use; the adjusted strength is passed through the normal level explosion call for TNTUtils and Explosion Overhaul integrations. Strength is clamped to 3.0–4.5, and malformed saved values fall back to vanilla power.

Dispenser use primes the variant with the same fuse and consumes one item. Flint and steel, fire charge, fire arrows, redstone, and chain explosions prime the authored variant.

## Verification

`./gradlew verifyFast` runs composition and parsed-resource tests. `./gradlew verifyFull stageRuntimeJar` also starts the Forge GameTest server, verifies that both authored variants prime with distinct power tags through redstone and fire paths while ordinary TNT remains untagged, and stages the reobfuscated JAR. The full-pack Dist and Debug tiers cover packaged integration and world behavior.
