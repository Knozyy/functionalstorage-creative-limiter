package com.knozyy.fscreativelimiter;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * Client side of {@link LimiterNetwork}. Only touched on the client, so the dedicated server never loads client classes.
 */
public class ClientSync {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            if (DrawerFilter.isServerListActive()) DrawerFilter.clearServerList();
        });
    }

    static void receive(List<String> entries) {
        // In singleplayer and on a LAN host the integrated server already shares this list
        if (Minecraft.getInstance().isLocalServer()) return;
        DrawerFilter.applyServerList(entries);
    }
}
