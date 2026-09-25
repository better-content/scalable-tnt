package com.bettercontent.scalabletnt;

import com.bettercontent.scalabletnt.policy.TntComposition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TntCompositionTest {
    @Test
    void sandHeavyRecipeIsBoundedBelowOrdinaryTnt() {
        final TntComposition.Variant variant = TntComposition.resolve(6, 3).orElseThrow();

        assertEquals(TntComposition.Variant.LOW_YIELD, variant);
        assertEquals(3.0F, variant.power());
        assertTrue(variant.power() < TntComposition.VANILLA_POWER);
    }

    @Test
    void gunpowderHeavyRecipeIsBoundedAboveOrdinaryTnt() {
        final TntComposition.Variant variant = TntComposition.resolve(3, 6).orElseThrow();

        assertEquals(TntComposition.Variant.HIGH_YIELD, variant);
        assertEquals(4.5F, variant.power());
        assertTrue(variant.power() > TntComposition.VANILLA_POWER);
        assertTrue(variant.power() <= TntComposition.MAX_VARIANT_POWER);
    }

    @Test
    void allOtherIngredientRatiosAreRejected() {
        assertTrue(TntComposition.resolve(4, 5).isEmpty());
        assertTrue(TntComposition.resolve(7, 2).isEmpty());
        assertTrue(TntComposition.resolve(2, 7).isEmpty());
        assertTrue(TntComposition.resolve(6, 4).isEmpty());
        assertTrue(TntComposition.resolve(-1, 10).isEmpty());
        assertTrue(TntComposition.resolve(Integer.MAX_VALUE, 9).isEmpty());
    }

    @Test
    void persistedStrengthIsClampedAndCorruptValuesFallBackToVanilla() {
        assertEquals(TntComposition.MIN_VARIANT_POWER, TntComposition.boundedPower(0.0F));
        assertEquals(TntComposition.MAX_VARIANT_POWER, TntComposition.boundedPower(99.0F));
        assertEquals(TntComposition.VANILLA_POWER, TntComposition.boundedPower(Float.NaN));
        assertEquals(TntComposition.VANILLA_POWER, TntComposition.boundedPower(Float.POSITIVE_INFINITY));
    }
}
