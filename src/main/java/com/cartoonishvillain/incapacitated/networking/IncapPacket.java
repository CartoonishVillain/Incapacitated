package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public class IncapPacket {

    private int ID;
    private Boolean isIncapacitated;

    public IncapPacket(int id, boolean isIncapacitated){
        this.ID = id;
        this.isIncapacitated = isIncapacitated;
    }

    public IncapPacket(PacketBuffer packetBuffer) {
        ID = packetBuffer.readInt();
        isIncapacitated = packetBuffer.readBoolean();
    }

    public void encode(PacketBuffer buffer){
        buffer.writeInt(ID);
        buffer.writeBoolean(isIncapacitated);
    }

    public static IncapPacket decode(PacketBuffer buf) {
        return new IncapPacket(buf);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(ID) ;
            if(entity != null){
                entity.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                    h.setIsIncapacitated(isIncapacitated);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
