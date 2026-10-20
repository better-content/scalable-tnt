package com.bettercontent.scalabletnt.mixin;

import com.bettercontent.scalabletnt.ScalableTntMod;
import com.bettercontent.scalabletnt.policy.TntComposition;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PrimedTnt.class)
abstract class PrimedTntExplosionMixin {
    @ModifyArg(
            method = "explode()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"),
            index = 4,
            require = 1)
    private float scalableTnt$useAuthoredPower(final float vanillaPower) {
        final PrimedTnt entity = (PrimedTnt) (Object) this;
        if (!entity.getPersistentData().contains(ScalableTntMod.POWER_DATA_KEY, Tag.TAG_FLOAT)) {
            return vanillaPower;
        }
        final float rawPower = entity.getPersistentData().getFloat(ScalableTntMod.POWER_DATA_KEY);
        final float resolvedPower = TntComposition.boundedPower(rawPower);
        if (com.bettercontent.scalabletnt.PlaytestDiagnostics.LOG.enabled("explosion")) {
            com.bettercontent.scalabletnt.diagnostics.TntTelemetry.explosionHandoff(com.bettercontent.scalabletnt.PlaytestDiagnostics.LOG,
                    new com.bettercontent.scalabletnt.diagnostics.PlaytestLog.Context("server", entity.level().dimension().location().toString(),
                            entity.level().getGameTime(), entity.getUUID().toString(), ""), rawPower, resolvedPower);
        }
        return resolvedPower;
    }
}
