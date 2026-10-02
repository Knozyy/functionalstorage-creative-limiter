package com.knozyy.fscreativelimiter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

/**
 * Sends the server's list to players. Drawers render through the same mixins as the server, so without this a
 * client would draw the amounts with its own config file (e.g. an endless supply the server no longer hands out).
 */
public class LimiterNetwork {

    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CreativeLimiter.MOD_ID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public record SyncList(List<String> entries) {
        void encode(FriendlyByteBuf buf) {
            buf.writeCollection(entries, FriendlyByteBuf::writeUtf);
        }

        static SyncList decode(FriendlyByteBuf buf) {
            return new SyncList(buf.readList(FriendlyByteBuf::readUtf));
        }
    }

    public static void register() {
        CHANNEL.messageBuilder(SyncList.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncList::encode)
                .decoder(SyncList::decode)
                .consumerMainThread((message, context) -> ClientSync.receive(message.entries()))
                .add();
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), current());
        }
    }

    /** Sends the current list to everyone online; called after commands and config file reloads. */
    public static void syncAll() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        // Config reloads arrive on the file watcher thread
        server.execute(() -> CHANNEL.send(PacketDistributor.ALL.noArg(), current()));
    }

    private static SyncList current() {
        return new SyncList(List.copyOf(DrawerFilterConfig.BLOCKED_ITEMS.get()));
    }
}
