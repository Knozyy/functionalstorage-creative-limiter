package com.knozyy.fscreativelimiter.mixin;

import com.buuz135.functionalstorage.inventory.item.CompactingStackItemHandler;
import com.knozyy.fscreativelimiter.CreativeRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Compacting drawers carried as items. This class only exists since Functional Storage 1.20.1-1.2.3,
 * @Pseudo lets older versions load without it.
 */
@Pseudo
@Mixin(value = CompactingStackItemHandler.class, remap = false)
public abstract class CompactingStackItemHandlerMixin {

    @Redirect(method = {"getStackInSlot", "insertItem", "extractItem", "getSlotLimit"},
            at = @At(value = "INVOKE", target = "Lcom/buuz135/functionalstorage/inventory/item/CompactingStackItemHandler;isCreative()Z"))
    private boolean fscreativelimiter$isCreative(CompactingStackItemHandler self) {
        return CreativeRules.isCreative(self.isCreative(), self.getResultList());
    }
}
