package com.cartoonishvillain.incapacitated.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.breakBlocks;

@Mixin(Player.class)
public class BreakBlocksMixin {

    @Inject(at = @At("HEAD"), method = "blockActionRestricted", cancellable = true)
    private void incapacitatedMayInteract(Level pLevel, BlockPos pPos, GameType pGameMode, CallbackInfoReturnable<Boolean> ci) {
        breakBlocks((Player) (Object) this, ci);
    }
}
