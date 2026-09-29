package com.knozyy.fscreativelimiter;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

@Mod(CreativeLimiter.MOD_ID)
public class CreativeLimiter {

    public static final String MOD_ID = "fscreativelimiter";
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeLimiter(IEventBus modBus, ModContainer container) {
        // Next to Functional Storage's own files in config/functionalstorage/.
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve("functionalstorage"));
        } catch (IOException e) {
            LOGGER.error("Couldn't create the functionalstorage config folder", e);
        }
        container.registerConfig(ModConfig.Type.COMMON, DrawerFilterConfig.SPEC, "functionalstorage/functionalstorage-creative-limiter.toml");
        modBus.addListener((ModConfigEvent.Loading event) -> onConfig(event.getConfig()));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfig(event.getConfig()));
    }

    private static void onConfig(ModConfig config) {
        if (config.getSpec() == DrawerFilterConfig.SPEC) DrawerFilter.reload(DrawerFilterConfig.BLOCKED_ITEMS.get());
    }
}
