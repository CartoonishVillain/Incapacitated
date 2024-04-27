package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapPacketClientHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod(Constants.MOD_ID)
public class NFIncapacitated {

    public NFIncapacitated(IEventBus modEventBus) {
        Incapacitated.init();

        PlayerCapability.loadDataAttachment(modEventBus);
        IncapEffects.init(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IncapacitatedClientConfig.CLIENTSPEC);

        NeoForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void commandLoad(RegisterCommandsEvent event){
        SetIncapacitatedCommand.register(event.getDispatcher());
        SetDownCount.register(event.getDispatcher());
        GetDownCount.register(event.getDispatcher());
        KillPlayer.register(event.getDispatcher());
        ConfigCommands.register(event.getDispatcher());

        if(!FMLLoader.isProduction()) {
            IncapDevMode.register(event.getDispatcher());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(final RegisterPayloadHandlerEvent event)
        {
            final IPayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
            registrar.play(IncapPacket.PACKET_ID, IncapPacket::new, handler -> handler
                    .client(IncapPacketClientHandler.getInstance()::handleData)
                    .server(IncapPacketClientHandler.getInstance()::handleData));
        }
    }
}