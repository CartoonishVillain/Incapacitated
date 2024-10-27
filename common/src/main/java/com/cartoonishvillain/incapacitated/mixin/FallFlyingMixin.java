package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.config.IncapConfigData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.breakBlocks;

@Mixin(Player.class)
public class FallFlyingMixin {

    @Inject(at = @At("HEAD"), method = "tryToStartFallFlying", cancellable = true)
    private void incapacitatedMayInteract(CallbackInfoReturnable<Boolean> ci) {
        if ((Player) (Object) this instanceof ServerPlayer player && Incapacitated.configData.getShouldDisableFallFlying()) {
            //if the entity is a player, and we have the config disabled
            if (Services.PLATFORM.getPlayerData(player).isIncapacitated()) { //
                ci.setReturnValue(false);
            }
        }
    }
}
