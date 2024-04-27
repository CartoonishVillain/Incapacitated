package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.DefaultConfig;
import com.cartoonishvillain.incapacitated.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricIncapacitated implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final SimpleConfig CONFIG = SimpleConfig.of("incapacitated").provider(DefaultConfig::provider).request();
    public static boolean lastDownDesaturate = CONFIG.getOrDefault("lastDownDesaturate", true);

    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Incapacitated.init();
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Constants.MOD_ID, "incap_slow"), FabricEffects.incapSlow);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Constants.MOD_ID, "incap_weak"), FabricEffects.incapWeak);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            GetDownCount.register(dispatcher);
            IncapDevMode.register(dispatcher);
            KillPlayer.register(dispatcher);
            SetDownCount.register(dispatcher);
            SetIncapacitatedCommand.register(dispatcher);
            ConfigCommands.register(dispatcher);
        }));
    }
}
