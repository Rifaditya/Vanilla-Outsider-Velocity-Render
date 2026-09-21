// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Isolated YetAnotherConfigLib v3 GUI screen factory for Velocity Render.
 * Constructs 3 semantic categories, sliders with step/range bounds, tick boxes,
 * and top-pinned Ko-fi solo developer support button.
 *
 * <p>Enforces zero-crash reflection classloading: this class is only referenced
 * dynamically via {@link net.dasik.social.api.config.GuiHelper} when YACL is loaded on the client.
 */
public final class YaclScreenHelper {

    private YaclScreenHelper() {
    }

    public static ConfigScreenFactory<?> createFactory() {
        return YaclScreenHelper::createScreen;
    }

    public static ConfigScreenFactory<?> createScreen() {
        return YaclScreenHelper::createScreen;
    }

    public static Screen createScreen(Screen parent) {
        VelocityRenderConfig config = VelocityRenderConfig.get();

        // 1. Flight Biasing & Meshing Category
        ConfigCategory.Builder flightCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.velocityrender.category.flight"));

        // Top-pinned Solo Developer Ko-fi Support Button
        Option<?> supportButton = createSupportButton();
        if (supportButton != null) {
            flightCategory.option(supportButton);
        }

        flightCategory
                // Master Enable Switch
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.enabled"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.enabled.description")))
                        .binding(true, () -> config.enabled, val -> config.enabled = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                // Forward Lead Multiplier (0% - 300%)
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.velocityrender.leadMultiplier"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.leadMultiplier.description")))
                        .binding(100, () -> config.leadMultiplier, val -> config.leadMultiplier = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 300).step(5))
                        .build())

                // Minimum Speed Threshold (1 - 200, representing 0.01 - 2.00 b/t)
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.velocityrender.minSpeedThresholdPct"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.minSpeedThresholdPct.description")))
                        .binding(20, () -> config.minSpeedThresholdPct, val -> config.minSpeedThresholdPct = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 200).step(1))
                        .build())

                // Banked Turn Widening
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.turnWidening"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.turnWidening.description")))
                        .binding(true, () -> config.turnWidening, val -> config.turnWidening = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                // Vertical Lookahead
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.verticalLookahead"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.verticalLookahead.description")))
                        .binding(true, () -> config.verticalLookahead, val -> config.verticalLookahead = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build());

        // 2. Server Quotas & WorldGen Category
        ConfigCategory.Builder serverCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.velocityrender.category.server"))

                // Server Ticket Budget (16 - 256)
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.velocityrender.serverTicketBudget"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.serverTicketBudget.description")))
                        .binding(64, () -> config.serverTicketBudget, val -> config.serverTicketBudget = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(16, 256).step(4))
                        .build())

                // Nether Reach Clamp (10% - 100%)
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.velocityrender.netherReachClampPct"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.netherReachClampPct.description")))
                        .binding(60, () -> config.netherReachClampPct, val -> config.netherReachClampPct = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(10, 100).step(5))
                        .build())

                // Dense Dimension Reach Clamp (10% - 100%)
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.velocityrender.defaultDenseReachClampPct"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.defaultDenseReachClampPct.description")))
                        .binding(80, () -> config.defaultDenseReachClampPct, val -> config.defaultDenseReachClampPct = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(10, 100).step(5))
                        .build());

        // 3. Telemetry & Compatibility Category
        ConfigCategory.Builder telemetryCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.velocityrender.category.telemetry"))

                // Bobby & Distant Horizons LOD Trajectory Hooks
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.lodTrajectoryHooks"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.lodTrajectoryHooks.description")))
                        .binding(true, () -> config.lodTrajectoryHooks, val -> config.lodTrajectoryHooks = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                // F3 Right-Side Screen Diagnostics
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.f3Debug"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.f3Debug.description")))
                        .binding(true, () -> config.f3Debug, val -> config.f3Debug = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                // Developer Debug Diagnostics
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("config.velocityrender.debugMode"))
                        .description(OptionDescription.of(Component.translatable("config.velocityrender.debugMode.description")))
                        .binding(false, () -> config.debugMode, val -> config.debugMode = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build());

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.velocityrender.title"))
                .category(flightCategory.build())
                .category(serverCategory.build())
                .category(telemetryCategory.build())
                .save(VelocityRenderConfig::save)
                .build()
                .generateScreen(parent);
    }

    private static Option<?> createSupportButton() {
        try {
            Class<?> helperClass = Class.forName("net.dasik.social.api.config.DasikSupportHelper");
            Object button = helperClass.getMethod("createYaclButton").invoke(null);
            if (button instanceof Option<?>) {
                return (Option<?>) button;
            }
        } catch (Throwable ignored) {
        }

        try {
            return ButtonOption.createBuilder()
                    .name(Component.translatable("dasiklibrary.support.kofi.button"))
                    .description(OptionDescription.of(Component.translatable("dasiklibrary.support.kofi.tooltip")))
                    .action((screen, opt) -> ConfirmLinkScreen.confirmLinkNow(screen, "https://ko-fi.com/dasikigaijin"))
                    .build();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
