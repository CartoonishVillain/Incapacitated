package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityDieMixin {
    @Inject(at = @At("HEAD"), method = "die")
    private void incapacitatedDie(DamageSource damageSource, CallbackInfo ci){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(!(entity instanceof Player) && !entity.level().isClientSide && damageSource.getEntity() instanceof Player) {
            IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData((Player) damageSource.getEntity());
            if (data.isIncapacitated() && Services.PLATFORM.commonConfigHunter()) {
                AbstractedIncapacitation.revive((Player) damageSource.getEntity());
            }
        }
    }
}