package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.DefaultConfig;
import com.cartoonishvillain.incapacitated.config.SimpleConfig;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
        FabricEffects.initEffects();
        FabricStats.modConstruction();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            GetDownCount.register(dispatcher);
            KillPlayer.register(dispatcher);
            SetDownCount.register(dispatcher);
            ConfigCommands.register(dispatcher);
            SetIncapacitatedCommand.register(dispatcher);
            SetDownTicks.register(dispatcher);

            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                IncapDevMode.register(dispatcher);
            }
        }));

        CommonLifecycleEvents.TAGS_LOADED.register((listen, listen2) -> {
            FabricStats.setup();
        });
    }
}
