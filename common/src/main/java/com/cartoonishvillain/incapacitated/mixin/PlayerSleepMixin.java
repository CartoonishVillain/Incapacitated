package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerSleepMixin {

    @Inject(at = @At("HEAD"), method = "stopSleepInBed")
    private void incapacitatedPlayer(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(entity instanceof Player && !entity.level().isClientSide) {
            AbstractedIncapacitation.sleep((Player) entity, wakeImmediately, updateLevel);
        }
    }
}
