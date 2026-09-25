# Scalable TNT

Adds two shapeless sand/gunpowder compositions alongside vanilla TNT:

- Six ordinary sand and three gunpowder produce Low-yield TNT (blast power 3.0).
- Three ordinary sand and six gunpowder produce High-yield TNT (blast power 4.5).

Both recipes use nine total inputs and the pack's `kubejs:ordinary_sand` tag. Vanilla TNT stays at its vanilla blast power of 4.0. Variant strength is stored on the primed vanilla TNT entity, so the vanilla fuse and entity lifecycle remain in use; the adjusted strength is passed through the normal level explosion call for TNTUtils and Explosion Overhaul integrations. Strength is clamped to 3.0–4.5, and malformed saved values fall back to vanilla power.

Dispenser use primes the variant with the same fuse and consumes one item. Flint and steel, fire charge, fire arrows, redstone, and chain explosions prime the authored variant.

## Verification

`./gradlew test` runs pure composition and resource-contract unit tests. Runtime mixin application, actual recipe registration, explosion structure effects, EMI presentation, and the client/server package pair remain outside this source slice.
