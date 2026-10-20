package com.bettercontent.scalabletnt;

import com.bettercontent.scalabletnt.diagnostics.PlaytestLog;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** Native lifecycle adapter; retains no level, player or entity references. */
@Mod.EventBusSubscriber(modid = ScalableTntMod.MOD_ID)
public final class PlaytestDiagnostics {
    public static final PlaytestLog LOG = new PlaytestLog(ScalableTntMod.MOD_ID);
    private PlaytestDiagnostics() {}

    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) LOG.summary("server");
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) { LOG.reset("server"); }

    @Mod.EventBusSubscriber(modid = ScalableTntMod.MOD_ID, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static final class ClientLifecycle {
        @SubscribeEvent public static void disconnect(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
            LOG.reset("client");
        }
    }

    @Mod.EventBusSubscriber(modid = ScalableTntMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Startup {
        @SubscribeEvent public static void setup(FMLCommonSetupEvent event) {
            String version = net.minecraftforge.fml.ModList.get().getModContainerById(ScalableTntMod.MOD_ID)
                    .map(container -> container.getModInfo().getVersion().toString()).orElse("UNKNOWN");
            var modFile = net.minecraftforge.fml.ModList.get().getModFileById(ScalableTntMod.MOD_ID);
            String source = modFile == null ? "UNKNOWN" : LOG.sourceIdentity(modFile.getFile().findResource("META-INF", "better-content-source.properties"));
            LOG.status("common", version, source, () -> PlaytestLog.fields(
                    "enabled", true, "integration", "native_tnt", "minimum_power", 3.0, "maximum_power", 4.5));
        }
    }
}
