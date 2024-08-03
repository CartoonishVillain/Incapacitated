package com.cartoonishvillain.incapacitated.networking;

import com.cartoonishvillain.incapacitated.Constants;
import com.cartoonishvillain.incapacitated.NFIncapacitated;
import com.cartoonishvillain.incapacitated.capability.NeoForgeIncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;

public class IncapPacketClientHandler implements IPayloadHandler<NFIncapacitated.IncapPayload> {
    private static final IncapPacketClientHandler INSTANCE = new IncapPacketClientHandler();

    public static IncapPacketClientHandler getInstance() {
        return INSTANCE;
    }

    public static void handleData(NFIncapacitated.IncapPayload incapPacket, IPayloadContext context) {
        context.enqueueWork(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(incapPacket.ID()) ;
            if(entity instanceof Player){
                NeoForgeIncapacitatedPlayerData incapacitatedPlayerData = entity.getData(INCAP_DATA);
                incapacitatedPlayerData.setIncapacitated(incapPacket.isIncapacitated());
                incapacitatedPlayerData.setDownsUntilDeath(incapPacket.downCount());

                if (incapPacket.downTicks() != -1) {
                    incapacitatedPlayerData.setTicksUntilDeath(incapPacket.downTicks());
                }

                if (!incapPacket.isIncapacitated()) {
                    ((Player) entity).setForcedPose(null);
                }

                entity.setData(INCAP_DATA, incapacitatedPlayerData);

                if (IncapacitatedClientConfig.GRAYSCREEN.get()) {
                    if (incapPacket.downCount() <= 0) {
                        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shaders/post/desaturate.json");
                        Minecraft.getInstance().gameRenderer.loadEffect(resourceLocation);
                    } else {
                        Minecraft.getInstance().gameRenderer.shutdownEffect();
                    }
                }
            }
        });
    }

    @Override
    public void handle(NFIncapacitated.IncapPayload incapPacket, IPayloadContext context) {
        context.enqueueWork(() ->{
            Entity entity = Minecraft.getInstance().level.getEntity(incapPacket.ID()) ;
            if(entity instanceof Player){
                NeoForgeIncapacitatedPlayerData incapacitatedPlayerData = entity.getData(INCAP_DATA);
                incapacitatedPlayerData.setIncapacitated(incapPacket.isIncapacitated());
                incapacitatedPlayerData.setDownsUntilDeath(incapPacket.downCount());

                if (incapPacket.downTicks() != -1) {
                    incapacitatedPlayerData.setTicksUntilDeath(incapPacket.downTicks());
                }

                if (!incapPacket.isIncapacitated()) {
                    ((Player) entity).setForcedPose(null);
                }

                entity.setData(INCAP_DATA, incapacitatedPlayerData);

                if (IncapacitatedClientConfig.GRAYSCREEN.get()) {
                    if (incapPacket.downCount() <= 0) {
                        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shaders/post/desaturate.json");
                        Minecraft.getInstance().gameRenderer.loadEffect(resourceLocation);
                    } else {
                        Minecraft.getInstance().gameRenderer.shutdownEffect();
                    }
                }
            }
        });
    }
}
