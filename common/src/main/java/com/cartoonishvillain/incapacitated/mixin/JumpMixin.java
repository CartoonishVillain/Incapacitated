package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class JumpMixin {

    @Inject(at = @At("HEAD"), method = "aiStep", cancellable = true)
    private void incapacitatedJump(CallbackInfo ci) {
        IncapacitatedPlayerData playerData = Services.PLATFORM.getPlayerData((LocalPlayer) (Object) this);
        if (!Incapacitated.configData.isCanJumpWhileDown() && playerData.isIncapacitated()) {
            ci.cancel();
        }
    }
}
