package com.knozyy.fscreativelimiter.mixin;

import com.buuz135.functionalstorage.inventory.item.DrawerStackItemHandler;
import com.knozyy.fscreativelimiter.CreativeRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Normal drawers exposed as item capabilities also report infinite contents and limits. */
@Mixin(value = DrawerStackItemHandler.class, remap = false)
public abstract class DrawerStackItemHandlerMixin {

    private static final String IS_CREATIVE =
            "Lcom/buuz135/functionalstorage/inventory/item/DrawerStackItemHandler;isCreative:Z";

    @Redirect(method = {"getStackInSlot", "getSlotLimit"},
            at = @At(value = "FIELD", target = IS_CREATIVE))
    private boolean fscreativelimiter$isCreative(DrawerStackItemHandler self, int slot) {
        return CreativeRules.isCreative(self.isCreative(), self.getStoredStacks(), slot);
    }
}
