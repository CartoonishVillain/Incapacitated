package com.cartoonishvillain.incapacitated.networking;

import net.minecraft.network.FriendlyByteBuf;

public class IncapPacket {

    private final int ID;
    private final Boolean isIncapacitated;

    private final short downCount;
    private final int downTicks;

    public int getDownTicks() {
        return downTicks;
    }

    public int getID() {
        return ID;
    }

    public Boolean getIncapacitated() {
        return isIncapacitated;
    }

    public short getDownCount() {
        return downCount;
    }

    public IncapPacket(int id, boolean isIncapacitated, short downCount, int downTicks){
        this.ID = id;
        this.isIncapacitated = isIncapacitated;
        this.downCount = downCount;
        this.downTicks = downTicks;
    }

    public IncapPacket(int id, boolean isIncapacitated, short downCount){
        this.ID = id;
        this.isIncapacitated = isIncapacitated;
        this.downCount = downCount;
        this.downTicks = -1;
    }

    public IncapPacket(FriendlyByteBuf packetBuffer) {
        ID = packetBuffer.readInt();
        isIncapacitated = packetBuffer.readBoolean();
        downCount = packetBuffer.readShort();
        this.downTicks = packetBuffer.readInt();
    }

    public static IncapPacket decode(FriendlyByteBuf buf) {
        return new IncapPacket(buf);
    }
}
