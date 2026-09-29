package com.knozyy.fscreativelimiter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parsed form of {@link DrawerFilterConfig#BLOCKED_ITEMS}. Drawer handlers ask
 * {@link #isBlocked(ItemStack)} before treating a stored item as infinite.
 */
public class DrawerFilter {

    // Swapped as a whole on reload, so readers never see a half-built set
    private static volatile Set<ResourceLocation> blockedIds = Set.of();
    private static volatile Set<TagKey<Item>> blockedTags = Set.of();
    private static volatile Set<String> blockedNamespaces = Set.of();

    public static void reload(List<? extends String> entries) {
        Set<ResourceLocation> ids = new HashSet<>();
        Set<TagKey<Item>> tags = new HashSet<>();
        Set<String> namespaces = new HashSet<>();
        for (String raw : entries) {
            String entry = raw.trim();
            if (entry.endsWith(":*")) {
                namespaces.add(entry.substring(0, entry.length() - 2));
                continue;
            }
            boolean isTag = entry.startsWith("#");
            ResourceLocation location = ResourceLocation.tryParse(isTag ? entry.substring(1) : entry);
            if (location == null) {
                CreativeLimiter.LOGGER.warn("Ignoring invalid Creative Limiter entry '{}'", raw);
            } else if (isTag) {
                tags.add(TagKey.create(Registries.ITEM, location));
            } else {
                ids.add(location);
            }
        }
        blockedIds = Set.copyOf(ids);
        blockedTags = Set.copyOf(tags);
        blockedNamespaces = Set.copyOf(namespaces);
        CreativeLimiter.LOGGER.info("Creative Limiter list loaded: {} items, {} tags, {} mods", ids.size(), tags.size(), namespaces.size());
    }

    public static boolean isBlocked(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        // Matches on the item id only, so renamed/enchanted/NBT variants are blocked too
        if (id != null && (blockedIds.contains(id) || blockedNamespaces.contains(id.getNamespace()))) return true;
        for (TagKey<Item> tag : blockedTags) {
            if (stack.is(tag)) return true;
        }
        return false;
    }
}
