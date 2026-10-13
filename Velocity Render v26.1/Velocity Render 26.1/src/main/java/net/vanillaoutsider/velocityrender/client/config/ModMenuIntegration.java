// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.dasik.social.api.config.GuiHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * ModMenu API integration for Velocity Render.
 * Resolves optional YetAnotherConfigLib (YACL v3) GUI screen factory via deferred reflection.
 * Prevents classloader crashes on headless dedicated servers and vanilla clients where YACL is absent.
 */
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiHelper.getOptionalYaclFactory(
                "velocity-render",
                "net.vanillaoutsider.velocityrender.client.config.YaclScreenHelper",
                "createScreen"
        );
    }
}
