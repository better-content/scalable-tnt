package com.bettercontent.scalabletnt.diagnostics;

/** Observes existing native outcomes; does not imply that an explosion completed. */
public final class TntTelemetry {
    private TntTelemetry() {}
    public static void primed(PlaytestLog log, boolean inserted, PlaytestLog.Context context, float power) {
        log.debug("priming", "tnt.prime", context, inserted ? "inserted" : "rejected",
                inserted ? "native_insertion" : "native_insertion_denied", () -> PlaytestLog.fields("power", power));
    }
    public static void explosionHandoff(PlaytestLog log, PlaytestLog.Context context, float raw, float resolved) {
        log.debug("explosion", "tnt.explosion_handoff", context, "handed_off",
                Float.compare(raw, resolved) == 0 ? "authored_power" : "bounded_power",
                () -> PlaytestLog.fields("power", resolved));
    }
}
