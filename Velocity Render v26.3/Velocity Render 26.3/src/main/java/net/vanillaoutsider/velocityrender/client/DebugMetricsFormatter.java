// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client;

import java.util.Locale;

/**
 * Pure stateless utility for formatting Velocity Render background engine diagnostics
 * for display on Minecraft's F3 debug screen overlay.
 */
public final class DebugMetricsFormatter {

    private DebugMetricsFormatter() {
    }

    /**
     * Formats server-integrated telemetry line:
     * "[VelocityRender] Active Tickets: %d | Shed: %.1f%% | Cone: %d° | Pool: %d/%d"
     *
     * @param activeTickets current player active forward tickets (clamped >= 0)
     * @param shedPct       current watchdog load shedding percentage (clamped 0.0 to 100.0)
     * @param coneDeg       current turning corridor cone angle in degrees (clamped 0 to 180)
     * @param serverTickets total active forward tickets across server (clamped >= 0)
     * @param serverBudget  total server-wide ticket budget (clamped >= 1)
     * @return formatted diagnostic string
     */
    public static String formatIntegrated(int activeTickets, double shedPct, int coneDeg, int serverTickets, int serverBudget) {
        int clampedActive = Math.max(0, activeTickets);
        double clampedShed = Math.max(0.0, Math.min(100.0, shedPct));
        int clampedCone = Math.max(0, Math.min(180, coneDeg));
        int clampedServer = Math.max(0, serverTickets);
        int clampedBudget = Math.max(1, serverBudget);

        StringBuilder sb = new StringBuilder(64);
        sb.append("[VelocityRender] Active Tickets: ").append(clampedActive)
                .append(" | Shed: ").append(String.format(Locale.ROOT, "%.1f%%", clampedShed))
                .append(" | Cone: ").append(clampedCone).append("°")
                .append(" | Pool: ").append(clampedServer).append("/").append(clampedBudget);
        return sb.toString();
    }

    /**
     * Formats client-only meshing telemetry line for multiplayer dedicated server connections:
     * "[VelocityRender] Bias: %s | Lead: %.1fm | Cone: %d°"
     *
     * @param activeBias whether velocity-biased chunk meshing is currently active
     * @param leadOffset distance offset ahead of the camera in blocks/meters (clamped 0.0 to 512.0)
     * @param coneDeg    current client turning corridor cone angle in degrees (clamped 0 to 180)
     * @return formatted diagnostic string
     */
    public static String formatClientOnly(boolean activeBias, double leadOffset, int coneDeg) {
        double clampedLead = Math.max(0.0, Math.min(512.0, leadOffset));
        int clampedCone = Math.max(0, Math.min(180, coneDeg));

        StringBuilder sb = new StringBuilder(64);
        sb.append("[VelocityRender] Bias: ").append(activeBias ? "Active" : "Idle")
                .append(" | Lead: ").append(String.format(Locale.ROOT, "%.1fm", clampedLead))
                .append(" | Cone: ").append(clampedCone).append("°");
        return sb.toString();
    }
}
