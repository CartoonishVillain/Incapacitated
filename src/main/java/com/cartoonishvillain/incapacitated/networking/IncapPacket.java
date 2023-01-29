package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public class IncapPacket {

    private int ID;
    private Boolean isIncapacitated;

    private short downCount;

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

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(ID);
        buffer.writeBoolean(isIncapacitated);
        buffer.writeShort(downCount);
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
                    h.setDownsUntilDeath(downCount);

                    if(Incapacitated.clientConfig.GRAYSCREEN.get()) {
                        if (downCount <= 0) {
                            ResourceLocation resourceLocation = new ResourceLocation("shaders/post/desaturate.json");
                            Minecraft.getInstance().gameRenderer.loadEffect(resourceLocation);
                        } else {
                            Minecraft.getInstance().gameRenderer.shutdownEffect();
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}
