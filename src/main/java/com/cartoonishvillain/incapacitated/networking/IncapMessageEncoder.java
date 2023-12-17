package com.cartoonishvillain.incapacitated.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.simple.MessageFunctions;

public class IncapMessageEncoder implements MessageFunctions.MessageEncoder<IncapPacket> {
    @Override
    public void encode(IncapPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.getID());
        buffer.writeBoolean(message.getIncapacitated());
        buffer.writeShort(message.getDownCount());
        buffer.writeInt(message.getDownTicks());
    }
}
