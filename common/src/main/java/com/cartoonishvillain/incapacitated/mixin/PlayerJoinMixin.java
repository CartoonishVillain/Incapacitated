package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {
    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    private void incapacitatedRespawnedPlayer(Connection pConnection, ServerPlayer player, CallbackInfo info) {
        IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData(player);
        Services.PLATFORM.sendIncapPacket(player, player.getId(), data.isIncapacitated(), (short)data.getDownsUntilDeath(), data.getTicksUntilDeath());
    }
}
