package com.cartoonishvillain.incapacitated.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Inventory.class)
public interface IncapacitatedInventoryAccessor {
    @Accessor
    NonNullList<ItemStack> getItems();
}
