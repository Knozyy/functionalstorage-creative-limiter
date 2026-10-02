package com.knozyy.fscreativelimiter;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

@Mod(CreativeLimiter.MOD_ID)
public class CreativeLimiter {

    public static final String MOD_ID = "fscreativelimiter";
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeLimiter() {
        // Next to Functional Storage's own files in config/functionalstorage/; Forge doesn't create the folder itself
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve("functionalstorage"));
        } catch (IOException e) {
            LOGGER.error("Couldn't create the functionalstorage config folder", e);
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DrawerFilterConfig.SPEC, "functionalstorage/functionalstorage-creative-limiter.toml");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((ModConfigEvent.Loading event) -> onConfig(event.getConfig()));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfig(event.getConfig()));
        LimiterNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(LimiterCommands::register);
        MinecraftForge.EVENT_BUS.addListener(LimiterCommands::registerPermissions);
        MinecraftForge.EVENT_BUS.addListener(LimiterNetwork::onLogin);
        if (FMLEnvironment.dist == Dist.CLIENT) ClientSync.init();
    }

    private static void onConfig(ModConfig config) {
        if (config.getSpec() != DrawerFilterConfig.SPEC) return;
        // A client on a remote server keeps the server's list; its own file applies again after leaving
        if (!DrawerFilter.isServerListActive()) DrawerFilter.reload(DrawerFilterConfig.BLOCKED_ITEMS.get());
        LimiterNetwork.syncAll();
    }
}
