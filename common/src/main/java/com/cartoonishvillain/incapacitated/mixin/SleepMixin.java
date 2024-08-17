package com.cartoonishvillain.incapacitated.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.breakBlocks;
import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.sleep;

@Mixin(ServerPlayer.class)
public class SleepMixin {

    @Inject(at = @At("HEAD"), method = "startSleepInBed", cancellable = true)
    private void incapacitatedMayInteract(BlockPos pAt, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> ci) {
        sleep((ServerPlayer) (Object) this, ci);
    }
}
