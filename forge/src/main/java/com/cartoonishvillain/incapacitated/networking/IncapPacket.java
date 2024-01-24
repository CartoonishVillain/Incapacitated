package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.ForgeIncapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class IncapPacket {

    private int ID;
    private Boolean isIncapacitated;

    private short downCount;
    private int downTicks;

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
        downTicks = packetBuffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(ID);
        buffer.writeBoolean(isIncapacitated);
        buffer.writeShort(downCount);
        buffer.writeInt(downCount);
    }

    public static IncapPacket decode(FriendlyByteBuf buf) {
        return new IncapPacket(buf);
    }


    public static void handle(IncapPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(packet.ID) ;
            if(entity != null){
                entity.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                    h.setIsIncapacitated(packet.isIncapacitated);
                    h.setDownsUntilDeath(packet.downCount);

                    if (packet.downTicks != -1) {
                        h.setTicksUntilDeath(packet.downTicks);
                    }

                    if (!packet.isIncapacitated && entity instanceof Player) {
                        ((Player) entity).setForcedPose(null);
                    }

                    if(ForgeIncapacitated.clientConfig.GRAYSCREEN.get()) {
                        if (packet.downCount <= 0) {
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
