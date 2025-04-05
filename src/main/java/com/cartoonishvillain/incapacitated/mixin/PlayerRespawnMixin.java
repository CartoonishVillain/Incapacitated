package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.cartoonishvillain.incapacitated.FabricIncapacitated.configData;

@Mixin(ServerLevel.class)
public class PlayerRespawnMixin {
    @Inject(at = @At("TAIL"), method = "respawnPlayerIntoHub")
    private void incapacitatedRespawnedPlayer(ServerPlayer player, Consumer<ServerPlayer> consumer, CallbackInfo info) {
        IncapacitatedPlayerData data = Services.getPlayerData(player);
        if (configData.getShouldReturnLivesAcrossBiomes()) {
            data.setDownsUntilDeath(configData.getDownCounter().shortValue());
            data.setTicksUntilDeath(configData.getDownTicks());
            data.setIncapacitated(false);
            Services.writePlayerData(player, data);
            Services.sendIncapPacket(player, player.getId(), data.isIncapacitated(), (short)data.getDownsUntilDeath(), data.getTicksUntilDeath());
        } else {
            Services.sendIncapPacket(player, player.getId(), data.isIncapacitated(), (short)data.getDownsUntilDeath(), data.getTicksUntilDeath());
        }
    }
}
