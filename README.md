# Scalable TNT

## Scope and authority

This repository owns its mod-specific behavior and authoring inputs. Read [local instructions](AGENTS.md)
and the [shared documentation/policy index](../../better-content-modpack/docs/README.md).


Adds two shapeless sand/gunpowder compositions alongside vanilla TNT:

- Six ordinary sand and three gunpowder produce Low-yield TNT (blast power 3.0).
- Three ordinary sand and six gunpowder produce High-yield TNT (blast power 4.5).

Both recipes use nine total inputs and the pack's `kubejs:ordinary_sand` tag. Vanilla TNT stays at its vanilla blast power of 4.0. Variant strength is stored on the primed vanilla TNT entity, so the vanilla fuse and entity lifecycle remain in use; the adjusted strength is passed through the normal level explosion call for TNTUtils and Explosion Overhaul integrations. Strength is clamped to 3.0–4.5, and malformed saved values fall back to vanilla power.

Dispenser use primes the variant with the same fuse and consumes one item. Flint and steel, fire charge, fire arrows, redstone, and chain explosions prime the authored variant.

## Verification

`./gradlew verifyFast` runs composition and parsed-resource tests. `./gradlew verifyFull stageRuntimeJar` also starts the Forge GameTest server, verifies that both authored variants prime with distinct power tags through redstone and fire paths while ordinary TNT remains untagged, and stages the reobfuscated JAR. The full-pack Dist and Debug tiers cover packaged integration and world behavior.
