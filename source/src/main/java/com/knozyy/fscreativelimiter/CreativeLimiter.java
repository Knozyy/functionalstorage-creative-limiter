package com.knozyy.fscreativelimiter;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
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
        modBus.addListener(LimiterNetwork::register);
        NeoForge.EVENT_BUS.addListener(LimiterCommands::register);
        NeoForge.EVENT_BUS.addListener(LimiterCommands::registerPermissions);
        NeoForge.EVENT_BUS.addListener(LimiterNetwork::onLogin);
        if (FMLEnvironment.dist == Dist.CLIENT) ClientSync.init();
    }

    private static void onConfig(ModConfig config) {
        if (config.getSpec() != DrawerFilterConfig.SPEC) return;
        // A client on a remote server keeps the server's list; its own file applies again after leaving
        if (!DrawerFilter.isServerListActive()) DrawerFilter.reload(DrawerFilterConfig.BLOCKED_ITEMS.get());
        LimiterNetwork.syncAll();
    }
}
