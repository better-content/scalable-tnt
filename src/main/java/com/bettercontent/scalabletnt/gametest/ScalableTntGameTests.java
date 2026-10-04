package com.bettercontent.scalabletnt.gametest;

import com.bettercontent.scalabletnt.ScalableTntMod;
import com.bettercontent.scalabletnt.block.ScalableTntContent;
import com.bettercontent.scalabletnt.policy.TntComposition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@PrefixGameTestTemplate(false)
public final class ScalableTntGameTests {
    private ScalableTntGameTests() {
    }

    @GameTest(templateNamespace = ScalableTntMod.MOD_ID, template = "empty")
    public static void authoredVariantsPrimeWithDistinctPowerAndVanillaRemainsUntagged(
            final GameTestHelper helper) {
        final var level = helper.getLevel();
        final BlockPos low = helper.absolutePos(new BlockPos(2, 2, 2));
        final BlockPos high = helper.absolutePos(new BlockPos(5, 2, 2));
        final BlockPos vanilla = helper.absolutePos(new BlockPos(8, 2, 2));

        level.setBlockAndUpdate(low.west(), Blocks.REDSTONE_BLOCK.defaultBlockState());
        level.setBlockAndUpdate(low, ScalableTntContent.LOW_YIELD_BLOCK.get().defaultBlockState());
        level.setBlockAndUpdate(high, ScalableTntContent.HIGH_YIELD_BLOCK.get().defaultBlockState());
        ScalableTntContent.HIGH_YIELD_BLOCK.get().onCaughtFire(
                level.getBlockState(high), level, high, Direction.UP, null);
        level.setBlockAndUpdate(vanilla, Blocks.TNT.defaultBlockState());
        Blocks.TNT.onCaughtFire(level.getBlockState(vanilla), level, vanilla, Direction.UP, null);

        assertPower(helper, low, TntComposition.Variant.LOW_YIELD.power());
        assertPower(helper, high, TntComposition.Variant.HIGH_YIELD.power());
        final var ordinary = level.getEntitiesOfClass(PrimedTnt.class, new AABB(vanilla).inflate(1.0D));
        if (ordinary.size() != 1 || ordinary.get(0).getPersistentData().contains(
                ScalableTntMod.POWER_DATA_KEY, Tag.TAG_FLOAT)) {
            helper.fail("Vanilla TNT acquired an authored blast-power tag");
            return;
        }
        if (!level.getBlockState(low).isAir()) {
            helper.fail("Redstone priming did not remove the low-yield TNT block: " + level.getBlockState(low));
            return;
        }
        helper.succeed();
    }

    private static void assertPower(final GameTestHelper helper, final BlockPos position, final float expected) {
        final var primed = helper.getLevel().getEntitiesOfClass(
                PrimedTnt.class, new AABB(position).inflate(1.0D));
        if (primed.size() != 1 || !primed.get(0).getPersistentData().contains(
                ScalableTntMod.POWER_DATA_KEY, Tag.TAG_FLOAT)
                || primed.get(0).getPersistentData().getFloat(ScalableTntMod.POWER_DATA_KEY) != expected) {
            helper.fail("Expected one primed TNT with blast power " + expected + " at " + position
                    + "; found=" + primed.size() + " tags="
                    + primed.stream().map(entity -> entity.position() + " " + entity.getPersistentData()).toList());
        }
    }
}
