package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityHurtMixin {
    @Inject(at = @At("HEAD"), method = "actuallyHurt", cancellable = true)
    private void incapacitatedHurt(ServerLevel level, DamageSource damageSource, float f, CallbackInfo cir){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(checkIfDamageIsValid(damageSource, cir) && entity instanceof Player && !entity.level().isClientSide)
            AbstractedIncapacitation.hurt((Player) entity, damageSource, cir, f);
    }

    /**
     * Cancels damage with the correct config set and the player downed
     * @param source The damage source to evaluate for the above conditions
     * @param cir The method to cancel the damage if needed
     * @return True if the damage is not canceled, false if it isn't
     */
    private boolean checkIfDamageIsValid(DamageSource source, CallbackInfo cir) {
        boolean validDamage = true;
        if (FabricIncapacitated.configData.getDANGERDisableIncapPlayerDamage()) { //Stop the check if the config isn't enabled in the first place
            if (source.getEntity() instanceof ServerPlayer entity) {
                //if the getEntity is a downed player with the config on, cancel the attack
                if (Services.getPlayerData(entity).isIncapacitated()) {
                    validDamage = false;
                    cir.cancel();
                }
            }

            if (source.getDirectEntity() instanceof ServerPlayer entity) {
                //if the getDirectEntity is a downed player with the config on, cancel the attack
                if (Services.getPlayerData(entity).isIncapacitated()) {
                    validDamage = false;
                    cir.cancel();
                }
            }
        }
        return validDamage;
    }
}