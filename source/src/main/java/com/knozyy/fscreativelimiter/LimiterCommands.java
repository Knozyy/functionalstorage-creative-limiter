package com.knozyy.fscreativelimiter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * In-game editing of {@link DrawerFilterConfig#BLOCKED_ITEMS}: hold an item and run /creativelimiter add or
 * /creativelimiter remove. Changes are written to the config file, so they survive restarts.
 */
public class LimiterCommands {

    /**
     * fscreativelimiter.command: ops (level 2) by default. Permission mods such as LuckPerms or FTB Ranks can grant it
     * to server admins who aren't op.
     */
    public static final PermissionNode<Boolean> USE = new PermissionNode<>(CreativeLimiter.MOD_ID, "command",
            PermissionTypes.BOOLEAN, (player, uuid, context) -> player != null && player.hasPermissions(2));

    public static void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(USE);
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("creativelimiter")
                .requires(LimiterCommands::canUse)
                .then(Commands.literal("add").executes(context -> add(context.getSource())))
                .then(Commands.literal("remove").executes(context -> remove(context.getSource())))
                .then(Commands.literal("list").executes(context -> list(context.getSource()))));
    }

    private static boolean canUse(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        // The console and command blocks keep the vanilla check
        return player != null ? PermissionAPI.getPermission(player, USE) : source.hasPermission(2);
    }

    private static int add(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack held = heldItem(source);
        if (held == null) return 0;
        String id = idOf(held);
        if (DrawerFilter.isBlocked(held)) {
            // Covers exact ids as well as #tag and modid:* entries, so the list doesn't collect duplicates
            source.sendFailure(Component.literal(id + " is already blocked by the Creative Limiter list"));
            return 0;
        }
        List<String> entries = entries();
        entries.add(id);
        save(entries);
        source.sendSuccess(() -> Component.literal("Added " + id + " to the Creative Limiter list"), true);
        return 1;
    }

    private static int remove(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack held = heldItem(source);
        if (held == null) return 0;
        String id = idOf(held);
        List<String> entries = entries();
        if (!entries.remove(id)) {
            source.sendFailure(Component.literal(DrawerFilter.isBlocked(held)
                    ? id + " is blocked by a #tag or modid:* entry; edit the config file to change that"
                    : id + " isn't on the Creative Limiter list"));
            return 0;
        }
        save(entries);
        source.sendSuccess(() -> Component.literal("Removed " + id + " from the Creative Limiter list"), true);
        if (DrawerFilter.isBlocked(held)) {
            source.sendSuccess(() -> Component.literal(id + " is still blocked by a #tag or modid:* entry"), false);
        }
        return 1;
    }

    private static int list(CommandSourceStack source) {
        List<String> entries = entries();
        source.sendSuccess(() -> Component.literal(entries.isEmpty()
                ? "The Creative Limiter list is empty"
                : "Creative Limiter list (" + entries.size() + "): " + String.join(", ", entries)), false);
        return entries.size();
    }

    /** The main hand item, or null after telling the player to hold one. */
    private static ItemStack heldItem(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack held = source.getPlayerOrException().getMainHandItem();
        if (held.isEmpty()) {
            source.sendFailure(Component.literal("Hold the item in your main hand first"));
            return null;
        }
        return held;
    }

    private static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static List<String> entries() {
        List<String> entries = new ArrayList<>();
        for (String entry : DrawerFilterConfig.BLOCKED_ITEMS.get()) entries.add(entry.trim());
        return entries;
    }

    private static void save(List<String> entries) {
        DrawerFilterConfig.BLOCKED_ITEMS.set(entries);
        DrawerFilterConfig.BLOCKED_ITEMS.save();
        // Apply right away instead of waiting for the file watcher's reload event
        DrawerFilter.reload(entries);
        LimiterNetwork.syncAll();
    }
}
