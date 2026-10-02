package com.knozyy.fscreativelimiter;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

/**
 * Sends the server's list to players. Drawers render through the same mixins as the server, so without this a
 * client would draw the amounts with its own config file (e.g. an endless supply the server no longer hands out).
 */
public class LimiterNetwork {

    public record SyncList(List<String> entries) implements CustomPacketPayload {
        public static final Type<SyncList> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreativeLimiter.MOD_ID, "list"));
        public static final StreamCodec<ByteBuf, SyncList> CODEC =
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(SyncList::new, SyncList::entries);

        @Override
        public Type<SyncList> type() {
            return TYPE;
        }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SyncList.TYPE, SyncList.CODEC,
                (message, context) -> ClientSync.receive(message.entries()));
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, current());
    }

    /** Sends the current list to everyone online; called after commands and config file reloads. */
    public static void syncAll() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        // Config reloads arrive on the file watcher thread
        server.execute(() -> PacketDistributor.sendToAllPlayers(current()));
    }

    private static SyncList current() {
        return new SyncList(List.copyOf(DrawerFilterConfig.BLOCKED_ITEMS.get()));
    }
}
