package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.Constants;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GiveUpPacket(int ID, GameProfile gameProfile) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GiveUpPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "incap_giveup_payload"));

    public static final StreamCodec<ByteBuf, GiveUpPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            GiveUpPacket::ID,
            ByteBufCodecs.GAME_PROFILE,
            GiveUpPacket::gameProfile,
            GiveUpPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}