package com.bettercontent.scalabletnt.block;

import com.bettercontent.scalabletnt.ScalableTntMod;
import com.bettercontent.scalabletnt.policy.TntComposition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public final class ScalableTntBlock extends TntBlock {
    private final TntComposition.Variant variant;

    public ScalableTntBlock(final TntComposition.Variant variant) {
        super(Properties.copy(net.minecraft.world.level.block.Blocks.TNT));
        this.variant = variant;
    }

    public boolean primeAt(
            final Level level,
            final double x,
            final double y,
            final double z,
            final LivingEntity owner) {
        if (level.isClientSide) {
            return false;
        }
        final PrimedTnt primed = new PrimedTnt(level, x, y, z, owner);
        primed.getPersistentData().putFloat(ScalableTntMod.POWER_DATA_KEY, variant.power());
        return level.addFreshEntity(primed);
    }

    private void primeAndRemove(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final LivingEntity owner) {
        if (primeAt(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, owner)) {
            level.removeBlock(pos, false);
            level.gameEvent(owner, GameEvent.PRIME_FUSE, pos);
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onCaughtFire(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Direction face,
            final LivingEntity owner) {
        primeAndRemove(state, level, pos, owner);
    }

    @Override
    public void onPlace(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final BlockState oldState,
            final boolean isMoving) {
        if (!level.isClientSide && level.hasNeighborSignal(pos) && !state.getValue(UNSTABLE)) {
            primeAndRemove(state, level, pos, null);
        }
    }

    @Override
    public void neighborChanged(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final net.minecraft.world.level.block.Block neighborBlock,
            final BlockPos neighborPos,
            final boolean isMoving) {
        if (!level.isClientSide && level.hasNeighborSignal(pos) && !state.getValue(UNSTABLE)) {
            primeAndRemove(state, level, pos, null);
        }
    }

    @Override
    public void wasExploded(final Level level, final BlockPos pos, final Explosion explosion) {
        if (primeAt(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, null)) {
            level.gameEvent(null, GameEvent.PRIME_FUSE, pos);
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public InteractionResult use(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hit) {
        final ItemStack held = player.getItemInHand(hand);
        final boolean flintAndSteel = held.is(Items.FLINT_AND_STEEL);
        final boolean fireCharge = held.is(Items.FIRE_CHARGE);
        if (!flintAndSteel && !fireCharge) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && primeAt(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player)) {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.PRIME_FUSE, pos);
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (fireCharge) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            } else {
                held.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onProjectileHit(
            final Level level,
            final BlockState state,
            final BlockHitResult hit,
            final Projectile projectile) {
        if (!level.isClientSide && projectile.isOnFire()) {
            final LivingEntity owner = projectile.getOwner() instanceof LivingEntity living ? living : null;
            primeAndRemove(state, level, hit.getBlockPos(), owner);
        }
    }

    @Override
    public boolean dropFromExplosion(final Explosion explosion) {
        return false;
    }
}
