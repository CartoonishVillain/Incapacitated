package com.cartoonishvillain.incapacitated.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.downLogging;

@Mixin(PlayerList.class)
public class PlayerRemoveMixin {
    @Inject(at = @At("HEAD"), method = "remove")
    private void incapacitatedRemovePlayer(ServerPlayer player, CallbackInfo info) {
        downLogging(player);
    }
}
