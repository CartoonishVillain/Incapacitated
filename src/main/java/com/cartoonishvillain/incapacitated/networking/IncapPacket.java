package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class IncapPacket {

    private int ID;
    private Boolean isIncapacitated;

    public IncapPacket(int id, boolean isIncapacitated){
        this.ID = id;
        this.isIncapacitated = isIncapacitated;
    }

    public IncapPacket(FriendlyByteBuf packetBuffer) {
        ID = packetBuffer.readInt();
        isIncapacitated = packetBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(ID);
        buffer.writeBoolean(isIncapacitated);
    }

    public static IncapPacket decode(FriendlyByteBuf buf) {
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
