package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class JumpMixin {

    @Inject(at = @At("HEAD"), method = "jumpFromGround", cancellable = true)
    private void incapacitatedJump(CallbackInfo ci) {
        IncapacitatedPlayerData playerData = Services.PLATFORM.getPlayerData((Player) (Object) this);
        if (!Incapacitated.configData.isCanJumpWhileDown() && playerData.isIncapacitated()) {
            ci.cancel();
        }
    }
}
