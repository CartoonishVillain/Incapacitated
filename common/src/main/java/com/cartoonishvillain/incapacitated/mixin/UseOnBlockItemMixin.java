package com.cartoonishvillain.incapacitated.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.sleep;
import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.useOn;

@Mixin(BlockItem.class)
public class UseOnBlockItemMixin {

    @Inject(at = @At("HEAD"), method = "useOn", cancellable = true)
    private void incapacitatedMayInteract(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> ci) {
        if (pContext.getPlayer() != null) {
            useOn(pContext, ci);
        }
    }
}
