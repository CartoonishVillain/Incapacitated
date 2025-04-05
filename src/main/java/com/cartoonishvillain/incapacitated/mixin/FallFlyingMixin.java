package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class FallFlyingMixin {

    @Inject(at = @At("HEAD"), method = "tryToStartFallFlying", cancellable = true)
    private void incapacitatedMayInteract(CallbackInfoReturnable<Boolean> ci) {
        if ((Player) (Object) this instanceof ServerPlayer player && FabricIncapacitated.configData.getShouldDisableFallFlying()) {
            //if the entity is a player, and we have the config disabled
            if (Services.getPlayerData(player).isIncapacitated()) { //
                ci.setReturnValue(false);
            }
        }
    }
}
