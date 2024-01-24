package com.cartoonishvillain.incapacitated.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;


public class IncapacitationMessenger {
    private static final int ProtocolVersion = 1;
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation("incapacitated", "main"))
            .networkProtocolVersion(ProtocolVersion)
            .simpleChannel()
            .messageBuilder(IncapPacket.class, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(IncapPacket::encode)
            .decoder(IncapPacket::decode)
            .consumerNetworkThread(IncapPacket::handle)
            .add();
    private static int ID = 0;
    private static int nextID(){
        return ID++;
    }




}
