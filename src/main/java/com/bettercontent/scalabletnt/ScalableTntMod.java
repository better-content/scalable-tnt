package com.bettercontent.scalabletnt;

import com.bettercontent.scalabletnt.block.ScalableTntContent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ScalableTntMod.MOD_ID)
public final class ScalableTntMod {
    public static final String MOD_ID = "scalable_tnt";
    public static final String POWER_DATA_KEY = "scalable_tnt:blast_power";

    public ScalableTntMod() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ScalableTntContent.BLOCKS.register(modBus);
        ScalableTntContent.ITEMS.register(modBus);
        modBus.addListener(this::addCreativeItems);
        ScalableTntContent.registerDispenserBehaviors(modBus);
    }

    private void addCreativeItems(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ScalableTntContent.LOW_YIELD_ITEM);
            event.accept(ScalableTntContent.HIGH_YIELD_ITEM);
        }
    }
}
