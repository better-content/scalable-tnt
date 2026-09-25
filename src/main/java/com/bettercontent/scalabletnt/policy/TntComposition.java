package com.bettercontent.scalabletnt.policy;

import java.util.Optional;

/** Bounded composition policy for the two authored alternatives to ordinary TNT. */
public final class TntComposition {
    public static final int TOTAL_INGREDIENTS = 9;
    public static final float VANILLA_POWER = 4.0F;
    public static final float MIN_VARIANT_POWER = 3.0F;
    public static final float MAX_VARIANT_POWER = 4.5F;

    private TntComposition() {
    }

    public static Optional<Variant> resolve(final int ordinarySand, final int gunpowder) {
        if (ordinarySand < 0 || gunpowder < 0 || ordinarySand + gunpowder != TOTAL_INGREDIENTS) {
            return Optional.empty();
        }
        if (ordinarySand == 6 && gunpowder == 3) {
            return Optional.of(Variant.LOW_YIELD);
        }
        if (ordinarySand == 3 && gunpowder == 6) {
            return Optional.of(Variant.HIGH_YIELD);
        }
        return Optional.empty();
    }

    public static float boundedPower(final float power) {
        if (!Float.isFinite(power)) {
            return VANILLA_POWER;
        }
        return Math.max(MIN_VARIANT_POWER, Math.min(MAX_VARIANT_POWER, power));
    }

    public enum Variant {
        LOW_YIELD(6, 3, 3.0F, "low_yield_tnt"),
        HIGH_YIELD(3, 6, 4.5F, "high_yield_tnt");

        private final int sand;
        private final int gunpowder;
        private final float power;
        private final String registryPath;

        Variant(final int sand, final int gunpowder, final float power, final String registryPath) {
            this.sand = sand;
            this.gunpowder = gunpowder;
            this.power = power;
            this.registryPath = registryPath;
        }

        public int sand() {
            return sand;
        }

        public int gunpowder() {
            return gunpowder;
        }

        public float power() {
            return power;
        }

        public String registryPath() {
            return registryPath;
        }
    }
}
