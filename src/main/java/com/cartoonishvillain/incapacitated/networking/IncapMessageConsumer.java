package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.capability.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.MessageFunctions;

import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;

public class IncapMessageConsumer implements MessageFunctions.MessageConsumer<IncapPacket> {
    @Override
    public void handle(IncapPacket incapPacket, NetworkEvent.Context context) {
        context.enqueueWork(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(incapPacket.getID()) ;
            if(entity instanceof Player){
                IncapacitatedPlayerData incapacitatedPlayerData = entity.getData(INCAP_DATA);
                incapacitatedPlayerData.setIncapacitated(incapPacket.getIncapacitated());
                incapacitatedPlayerData.setDownsUntilDeath(incapPacket.getDownCount());

                if (incapPacket.getDownTicks() != -1) {
                    incapacitatedPlayerData.setTicksUntilDeath(incapPacket.getDownTicks());
                }

                if (!incapPacket.getIncapacitated()) {
                    ((Player) entity).setForcedPose(null);
                }

                entity.setData(INCAP_DATA, incapacitatedPlayerData);

                if (IncapacitatedClientConfig.GRAYSCREEN.get()) {
                    if (incapPacket.getDownCount() <= 0) {
                        ResourceLocation resourceLocation = new ResourceLocation("shaders/post/desaturate.json");
                        Minecraft.getInstance().gameRenderer.loadEffect(resourceLocation);
                    } else {
                        Minecraft.getInstance().gameRenderer.shutdownEffect();
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
