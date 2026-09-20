// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DebugMetricsFormatterTest {

    @Test
    @DisplayName("Integrated formatting should render canonical string with 1-decimal shed precision")
    void testFormatIntegratedStandard() {
        String result = DebugMetricsFormatter.formatIntegrated(8, 0.0, 45, 12, 64);
        Assertions.assertEquals("[VelocityRender] Active Tickets: 8 | Shed: 0.0% | Cone: 45° | Pool: 12/64", result);

        String shedResult = DebugMetricsFormatter.formatIntegrated(16, 25.5, 30, 48, 64);
        Assertions.assertEquals("[VelocityRender] Active Tickets: 16 | Shed: 25.5% | Cone: 30° | Pool: 48/64", shedResult);
    }

    @Test
    @DisplayName("Integrated formatting should safely clamp boundary and negative inputs")
    void testFormatIntegratedClamping() {
        // Negative active tickets and server tickets clamped to 0
        // Negative shed clamped to 0.0%, negative cone clamped to 0°, budget < 1 clamped to 1
        String underflow = DebugMetricsFormatter.formatIntegrated(-5, -10.0, -15, -2, 0);
        Assertions.assertEquals("[VelocityRender] Active Tickets: 0 | Shed: 0.0% | Cone: 0° | Pool: 0/1", underflow);

        // Shed > 100% clamped to 100.0%, cone > 180° clamped to 180°
        String overflow = DebugMetricsFormatter.formatIntegrated(20, 150.0, 270, 64, 64);
        Assertions.assertEquals("[VelocityRender] Active Tickets: 20 | Shed: 100.0% | Cone: 180° | Pool: 64/64", overflow);
    }

    @Test
    @DisplayName("Client-only formatting should render standard active and idle strings")
    void testFormatClientOnlyStandard() {
        String active = DebugMetricsFormatter.formatClientOnly(true, 24.0, 32);
        Assertions.assertEquals("[VelocityRender] Bias: Active | Lead: 24.0m | Cone: 32°", active);

        String idle = DebugMetricsFormatter.formatClientOnly(false, 0.0, 0);
        Assertions.assertEquals("[VelocityRender] Bias: Idle | Lead: 0.0m | Cone: 0°", idle);
    }

    @Test
    @DisplayName("Client-only formatting should clamp negative and extreme lead offsets")
    void testFormatClientOnlyClamping() {
        String underflow = DebugMetricsFormatter.formatClientOnly(true, -10.5, -20);
        Assertions.assertEquals("[VelocityRender] Bias: Active | Lead: 0.0m | Cone: 0°", underflow);

        String overflow = DebugMetricsFormatter.formatClientOnly(true, 999.9, 360);
        Assertions.assertEquals("[VelocityRender] Bias: Active | Lead: 512.0m | Cone: 180°", overflow);
    }
}
