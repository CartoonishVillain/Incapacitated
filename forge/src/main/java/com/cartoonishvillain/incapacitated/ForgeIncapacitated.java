package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.IPlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.config.ClientConfig;
import com.cartoonishvillain.incapacitated.config.ConfigHelper;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(Constants.MOD_ID)
public class ForgeIncapacitated {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ClientConfig clientConfig;
    
    public ForgeIncapacitated() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::register);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::capabilityRegister);
        ForgeIncapEffects.init();
        clientConfig = ConfigHelper.register(ModConfig.Type.CLIENT, ClientConfig::new);
        // Use Forge to bootstrap the Common mod.
        Incapacitated.init();
    }

    private void register(final RegisterEvent event) {
        for (var channel : new Channel[]{IncapacitationMessenger.INSTANCE});
    }

    public void capabilityRegister(final RegisterCapabilitiesEvent event){
        event.register(IPlayerCapability.class);
        PlayerCapability.INSTANCE = CapabilityManager.get(new CapabilityToken<IPlayerCapability>() {});
    }
}