package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapabilityManager;
import com.cartoonishvillain.incapacitated.commands.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event){
        SetIncapacitatedCommand.register(event.getDispatcher());
        SetDownCount.register(event.getDispatcher());
        GetDownCount.register(event.getDispatcher());
        KillPlayer.register(event.getDispatcher());

        if(!FMLLoader.isProduction()) {
            IncapDevMode.register(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event) {
        if(!event.isWasDeath()){
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            AtomicBoolean incapacitated = new AtomicBoolean(false);
            AtomicInteger ticksUntilDeath = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger downsUntilDeath = new AtomicInteger(Integer.MAX_VALUE);

            originalPlayer.revive();

            originalPlayer.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                incapacitated.set(h.getIsIncapacitated());
                ticksUntilDeath.set(h.getTicksUntilDeath());
                downsUntilDeath.set(h.getDownsUntilDeath());
            });

            originalPlayer.kill();

            newPlayer.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                h.setIsIncapacitated(incapacitated.get());
                h.setTicksUntilDeath(ticksUntilDeath.get());
                h.setDownsUntilDeath(downsUntilDeath.get());
            });
        }
    }

    @SubscribeEvent
    public static void playerRegister(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player){
            PlayerCapabilityManager provider = new PlayerCapabilityManager();
            event.addCapability(new ResourceLocation(Constants.MOD_ID, "incapacitated"), provider);
        }
    }
}
