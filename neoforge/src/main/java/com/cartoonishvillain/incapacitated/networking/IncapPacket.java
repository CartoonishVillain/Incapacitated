package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.Constants;
import com.cartoonishvillain.incapacitated.Incapacitated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class IncapPacket implements CustomPacketPayload {

    public static final ResourceLocation PACKET_ID = new ResourceLocation(Constants.MOD_ID, "incap_packet");

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


    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(getID());
        buffer.writeBoolean(getIncapacitated());
        buffer.writeShort(getDownCount());
        buffer.writeInt(getDownTicks());
    }

    @Override
    public ResourceLocation id() {
        return PACKET_ID;
    }
}
