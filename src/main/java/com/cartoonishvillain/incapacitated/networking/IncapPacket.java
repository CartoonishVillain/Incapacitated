package com.cartoonishvillain.incapacitated.networking;

import net.minecraft.network.FriendlyByteBuf;

public class IncapPacket {

    private final int ID;
    private final Boolean isIncapacitated;

    private final short downCount;

    public int getID() {
        return ID;
    }

    public Boolean getIncapacitated() {
        return isIncapacitated;
    }

    public short getDownCount() {
        return downCount;
    }

    public IncapPacket(int id, boolean isIncapacitated, short downCount){
        this.ID = id;
        this.isIncapacitated = isIncapacitated;
        this.downCount = downCount;
    }

    public IncapPacket(FriendlyByteBuf packetBuffer) {
        ID = packetBuffer.readInt();
        isIncapacitated = packetBuffer.readBoolean();
        downCount = packetBuffer.readShort();
    }

    public static IncapPacket decode(FriendlyByteBuf buf) {
        return new IncapPacket(buf);
    }
}
