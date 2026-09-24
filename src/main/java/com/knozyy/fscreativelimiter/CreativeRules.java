package com.knozyy.fscreativelimiter;

import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import com.buuz135.functionalstorage.util.CompactingUtil;

import java.util.List;

/**
 * Replacements for Functional Storage's isCreative() checks, called from the mixins.
 */
public class CreativeRules {

    /**
     * Big drawers decide per slot: a blocked item keeps its real amount and limit, other slots stay infinite.
     */
    public static boolean isCreative(BigInventoryHandler handler, int slot) {
        if (!handler.isCreative()) return false;
        List<BigInventoryHandler.BigStack> stacks = handler.getStoredStacks();
        return slot >= stacks.size() || !DrawerFilter.isBlocked(stacks.get(slot).getStack());
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
