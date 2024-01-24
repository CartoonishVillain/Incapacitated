package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.capability.NeoForgeIncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;

public class IncapPacketClientHandler {
    private static final IncapPacketClientHandler INSTANCE = new IncapPacketClientHandler();

    public static IncapPacketClientHandler getInstance() {
        return INSTANCE;
    }

    public void handleData(final IncapPacket incapPacket, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(incapPacket.getID()) ;
            if(entity instanceof Player){
                NeoForgeIncapacitatedPlayerData incapacitatedPlayerData = entity.getData(INCAP_DATA);
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
    }
}
