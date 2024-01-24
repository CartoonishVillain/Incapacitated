package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityHurtMixin {
    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void incapacitatedHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(entity instanceof Player && !entity.level().isClientSide)
            AbstractedIncapacitation.hurt((Player) entity, damageSource, cir, f);
    }
}