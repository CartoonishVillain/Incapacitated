package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class PlayerJoinMixin {
    @Inject(at = @At("TAIL"), method = "addNewPlayer")
    private void incapacitatedAddPlayer(ServerPlayer player, CallbackInfo info) {
        IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData(player);
        Services.PLATFORM.sendIncapPacket(player, player.getId(), data.isIncapacitated(), (short)data.getDownsUntilDeath(), data.getTicksUntilDeath());
        player.sendSystemMessage(Component.translatable("incapacitated.info.joinrevivetutorial"));;
    }

    @Inject(at = @At("TAIL"), method = "addRespawnedPlayer")
    private void incapacitatedRespawnedPlayer(ServerPlayer player, CallbackInfo info) {
        IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData(player);
        Services.PLATFORM.sendIncapPacket(player, player.getId(), data.isIncapacitated(), (short)data.getDownsUntilDeath(), data.getTicksUntilDeath());
    }
}
