package com.knozyy.fscreativelimiter;

import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import com.buuz135.functionalstorage.util.CompactingUtil;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Replacements for Functional Storage's isCreative() checks, called from the mixins.
 */
public class CreativeRules {

    /**
     * Big drawers decide per slot: a blocked item keeps its real amount and limit, other slots stay infinite.
     */
    public static boolean isCreative(BigInventoryHandler handler, int slot) {
        return isCreative(handler.isCreative(), handler.getStoredStacks(), slot);
    }

    public static boolean isCreative(boolean creative, List<BigInventoryHandler.BigStack> stacks, int slot) {
        return creative && (slot >= stacks.size() || !DrawerFilter.isBlocked(stacks.get(slot).getStack()));
    }

    /** The 1.21 item-aware slot limit must check the incoming item even when the slot is empty. */
    public static boolean isCreative(BigInventoryHandler handler, ItemStack stack) {
        return handler.isCreative() && !DrawerFilter.isBlocked(stack);
    }

    /**
     * Compacting drawers share one amount across the chain, so one blocked tier disables creative for all of them,
     * otherwise an infinite allowed tier could be crafted into the blocked one.
     */
    public static boolean isCreative(boolean creative, List<CompactingUtil.Result> results) {
        if (!creative) return false;
        for (CompactingUtil.Result result : results) {
            if (DrawerFilter.isBlocked(result.getResult())) return false;
        }
        return true;
    }
}
