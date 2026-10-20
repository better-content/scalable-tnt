package com.bettercontent.scalabletnt.diagnostics;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class TntTelemetryTest {
    @Test public void observesNativeOutcomesWithoutClaimingExplosionCompletion() {
        List<String> records = new ArrayList<>();
        PlaytestLog log = new PlaytestLog("scalable_tnt", new PlaytestLog.Sink() {
            public boolean enabled(String category, String level) { return true; }
            public void write(String category, String level, String message) { records.add(message); }
        }, () -> 0L, new Properties());
        var context = PlaytestLog.Context.server("minecraft:overworld", 12);
        TntTelemetry.primed(log, false, context, 3.0f);
        TntTelemetry.primed(log, true, context, 4.5f);
        TntTelemetry.explosionHandoff(log, context, Float.NaN, 4.0f);
        if (records.size() != 3 || !records.get(0).contains("result=rejected reason=native_insertion_denied")
                || !records.get(1).contains("result=inserted reason=native_insertion")
                || !records.get(2).contains("result=handed_off reason=bounded_power")
                || records.stream().anyMatch(s -> s.contains("completed") || s.contains("subject=") || s.contains("pos="))) {
            throw new AssertionError(records.toString());
        }
    }
    public static void main(String[] args) { new TntTelemetryTest().observesNativeOutcomesWithoutClaimingExplosionCompletion(); }
}
