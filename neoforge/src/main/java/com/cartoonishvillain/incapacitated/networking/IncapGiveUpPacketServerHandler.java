package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.NFIncapacitated;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class IncapGiveUpPacketServerHandler implements IPayloadHandler<NFIncapacitated.IncapGiveupPayload> {
    private static final IncapGiveUpPacketServerHandler INSTANCE = new IncapGiveUpPacketServerHandler();

    public static IncapGiveUpPacketServerHandler getInstance() {
        return INSTANCE;
    }

    public static void handleData(NFIncapacitated.IncapGiveupPayload incapPacket, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = NFIncapacitated.server.overworld().getEntity(incapPacket.ID());
            Player player = NFIncapacitated.server.getPlayerList().getPlayer(incapPacket.gameProfile().getId());
            if (player == null && entity instanceof Player) {
                player = (Player) entity;
            }
            if (player instanceof ServerPlayer) {
                if (!Incapacitated.configData.isDANGERDisableGiveUp()) {
                    Services.PLATFORM.killPlayerIfIncappedCommand((ServerPlayer) player);
                }
            }
        });
    }

    @Override
    public void handle(NFIncapacitated.IncapGiveupPayload incapGiveupPayload, IPayloadContext iPayloadContext) {
        handleData(incapGiveupPayload, iPayloadContext);
    }
}
