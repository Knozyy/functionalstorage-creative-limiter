package com.knozyy.fscreativelimiter.mixin;

import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import com.knozyy.fscreativelimiter.CreativeRules;
import com.knozyy.fscreativelimiter.DrawerFilter;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Covers normal drawers and ender drawers (EnderInventoryHandler extends BigInventoryHandler).
 */
@Mixin(value = BigInventoryHandler.class, remap = false)
public abstract class BigInventoryHandlerMixin {

    private static final String IS_CREATIVE = "Lcom/buuz135/functionalstorage/inventory/BigInventoryHandler;isCreative()Z";

    @Redirect(method = "getStackInSlot", at = @At(value = "INVOKE", target = IS_CREATIVE))
    private boolean fscreativelimiter$getStackInSlot(BigInventoryHandler self, int slot) {
        return CreativeRules.isCreative(self, slot);
    }

    @Redirect(method = "getSlotLimit(I)I", at = @At(value = "INVOKE", target = IS_CREATIVE))
    private boolean fscreativelimiter$getSlotLimit(BigInventoryHandler self, int slot) {
        return CreativeRules.isCreative(self, slot);
    }

    // Functional Storage 1.21 also asks for a limit using the incoming stack.
    @Redirect(method = "getSlotLimit(ILnet/minecraft/world/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = IS_CREATIVE))
    private boolean fscreativelimiter$getIncomingSlotLimit(BigInventoryHandler self, int slot, ItemStack stack) {
        return CreativeRules.isCreative(self, stack);
    }

    @Redirect(method = "extractItem", at = @At(value = "INVOKE", target = IS_CREATIVE))
    private boolean fscreativelimiter$extractItem(BigInventoryHandler self, int slot, int amount, boolean simulate) {
        return CreativeRules.isCreative(self, slot);
    }

    // Creative drawers void inserts of items they already hold; blocked items are stored normally instead
    @Redirect(method = "insertItem", at = @At(value = "INVOKE", target = IS_CREATIVE))
    private boolean fscreativelimiter$insertItem(BigInventoryHandler self, int slot, ItemStack stack, boolean simulate) {
        return self.isCreative() && !DrawerFilter.isBlocked(stack);
    }
}
