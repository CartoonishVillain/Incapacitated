package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = Player.class, priority = 999999)
public class LivingEntityHurtMixin {
    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void incapacitatedHurtReturn(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir){
        Player entity = ((Player) (Object) this);
        if(checkIfDamageIsValid(damageSource, cir) && entity instanceof Player && !entity.level().isClientSide) {
            //handled in a neoforge event, don't want to overwrite
            AbstractedIncapacitation.hurt((Player) entity, damageSource, cir, f);
        }
    }

    @Inject(at = @At("HEAD"), method = "actuallyHurt", cancellable = true)
    private void incapacitatedHurt(DamageSource damageSource, float f, CallbackInfo cir){
        Player entity = ((Player) (Object) this);
        if(entity instanceof Player && !entity.level().isClientSide) {
            //handled in a neoforge event, don't want to overwrite
            if (!Objects.equals(Services.PLATFORM.getPlatformName(), "NeoForge")) {
                Services.PLATFORM.setLastHealthBeforeDamage(entity.getHealth(), (Player) entity);
                Services.PLATFORM.setLastDmgTaken(f, (Player) entity);
            }
        }
    }

    /**
     * Cancels damage with the correct config set and the player downed
     * @param source The damage source to evaluate for the above conditions
     * @param cir The method to cancel the damage if needed
     * @return True if the damage is not canceled, false if it isn't
     */
    private boolean checkIfDamageIsValid(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        boolean validDamage = true;
        if (Incapacitated.configData.getDANGERDisableIncapPlayerDamage()) { //Stop the check if the config isn't enabled in the first place
            if (source.getEntity() instanceof ServerPlayer entity) {
                //if the getEntity is a downed player with the config on, cancel the attack
                if (Services.PLATFORM.getPlayerData(entity).isIncapacitated()) {
                    validDamage = false;
                    cir.cancel();
                }
            }

            if (source.getDirectEntity() instanceof ServerPlayer entity) {
                //if the getDirectEntity is a downed player with the config on, cancel the attack
                if (Services.PLATFORM.getPlayerData(entity).isIncapacitated()) {
                    validDamage = false;
                    cir.cancel();
                }
            }
        }
        return validDamage;
    }
}