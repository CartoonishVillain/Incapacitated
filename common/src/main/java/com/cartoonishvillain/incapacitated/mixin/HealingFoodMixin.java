package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class HealingFoodMixin {

    @Inject(at = @At("HEAD"), method = "finishUsingItem")
    private void incapacitatedFinishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir){
        AbstractedIncapacitation.eat(livingEntity, itemStack);
    }
}