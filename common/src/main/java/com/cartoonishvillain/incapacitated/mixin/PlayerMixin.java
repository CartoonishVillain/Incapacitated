package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(at = @At("HEAD"), method = "updatePlayerPose", cancellable = true)
    private void incapacitatedUpdatePlayerPose(CallbackInfo ci){
        AbstractedIncapacitation.pose((Player) (Object) this, ci, true);
    }
}