package com.knozyy.fscreativelimiter.mixin;

import com.buuz135.functionalstorage.inventory.CompactingInventoryHandler;
import com.knozyy.fscreativelimiter.CreativeRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CompactingInventoryHandler.class, remap = false)
public abstract class CompactingInventoryHandlerMixin {

    @Redirect(method = {"getStackInSlot", "insertItem", "extractItem", "getSlotLimit"},
            at = @At(value = "INVOKE", target = "Lcom/buuz135/functionalstorage/inventory/CompactingInventoryHandler;isCreative()Z"))
    private boolean fscreativelimiter$isCreative(CompactingInventoryHandler self) {
        return CreativeRules.isCreative(self.isCreative(), self.getResultList());
    }
}
