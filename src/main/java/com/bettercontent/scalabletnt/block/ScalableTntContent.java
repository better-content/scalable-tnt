package com.bettercontent.scalabletnt.block;

import com.bettercontent.scalabletnt.policy.TntComposition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Position;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ScalableTntContent {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "scalable_tnt");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "scalable_tnt");

    public static final RegistryObject<ScalableTntBlock> LOW_YIELD_BLOCK = BLOCKS.register(
            TntComposition.Variant.LOW_YIELD.registryPath(),
            () -> new ScalableTntBlock(TntComposition.Variant.LOW_YIELD));
    public static final RegistryObject<ScalableTntBlock> HIGH_YIELD_BLOCK = BLOCKS.register(
            TntComposition.Variant.HIGH_YIELD.registryPath(),
            () -> new ScalableTntBlock(TntComposition.Variant.HIGH_YIELD));
    public static final RegistryObject<Item> LOW_YIELD_ITEM = ITEMS.register(
            TntComposition.Variant.LOW_YIELD.registryPath(),
            () -> new BlockItem(LOW_YIELD_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> HIGH_YIELD_ITEM = ITEMS.register(
            TntComposition.Variant.HIGH_YIELD.registryPath(),
            () -> new BlockItem(HIGH_YIELD_BLOCK.get(), new Item.Properties()));

    private ScalableTntContent() {
    }

    public static void registerDispenserBehaviors(final IEventBus modBus) {
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(LOW_YIELD_ITEM.get(), createDispenser(LOW_YIELD_BLOCK.get()));
            DispenserBlock.registerBehavior(HIGH_YIELD_ITEM.get(), createDispenser(HIGH_YIELD_BLOCK.get()));
        }));
    }

    private static OptionalDispenseItemBehavior createDispenser(final ScalableTntBlock block) {
        return new OptionalDispenseItemBehavior() {
            @Override
            protected net.minecraft.world.item.ItemStack execute(
                    final BlockSource source,
                    final net.minecraft.world.item.ItemStack stack) {
                final Position position = DispenserBlock.getDispensePosition(source);
                if (block.primeAt(source.getLevel(), position.x(), position.y(), position.z(), null)) {
                    stack.shrink(1);
                    setSuccess(true);
                } else {
                    setSuccess(false);
                }
                return stack;
            }
        };
    }
}
