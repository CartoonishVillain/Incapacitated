package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerGoalFixerMixin {
    @Inject(at = @At("RETURN"), method = "isSpectator", cancellable = true)
    private void incapacitatedRespawnedPlayer(CallbackInfoReturnable<Boolean> info) {
        ServerPlayer player = ((ServerPlayer) (Object) this);
        if (!info.getReturnValue() && Incapacitated.configData.getDANGERManipulateGoalToAvoidDownPlayers()) { //if not already in spectator and feature is enabled
            IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData(player);
            info.setReturnValue(data.isIncapacitated()); // set spectator flag to true
        }
    }
}
