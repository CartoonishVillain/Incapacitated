package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.DefaultConfig;
import com.cartoonishvillain.incapacitated.config.SimpleConfig;
import com.cartoonishvillain.incapacitated.networking.GiveUpPacket;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricIncapacitated implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    private MinecraftServer server;
    private static final SimpleConfig CONFIG = SimpleConfig.of("incapacitated").provider(DefaultConfig::provider).request();
    public static boolean lastDownDesaturate = CONFIG.getOrDefault("lastDownDesaturate", true);
    public static boolean renderDownCounter = CONFIG.getOrDefault("renderDownCounter", true);
    public static boolean downCounterColorful = CONFIG.getOrDefault("downCounterColorful", true);
    public static int downCounterModX = CONFIG.getOrDefault("downCounterModX", 0);
    public static int downCounterModY = CONFIG.getOrDefault("downCounterModY", 0);

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

        PayloadTypeRegistry.playC2S().register(GiveUpPacket.TYPE, GiveUpPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GiveUpPacket.TYPE, ((incapPacket, context) -> {
            context.server().execute(() -> {
                Entity entity = server.overworld().getEntity(incapPacket.ID());
                Player player = server.getPlayerList().getPlayer(incapPacket.gameProfile().getId());
                if (player == null && entity instanceof Player) {
                    player = (Player) entity;
                }
                if (player != null) {
                    if (!Incapacitated.configData.isDANGERDisableGiveUp()) {
                        Services.PLATFORM.killPlayerIfIncappedCommand((ServerPlayer) player);
                    }
                }
            });
        }));

        ServerTickEvents.END_SERVER_TICK.register(server1 -> {
            server = server1;
        });

        //Example implementation of the event call back register.
//        IncapacitatedRevivalCallback.EVENT.register(((revivingPlayer, downedPlayer) -> {
//            if (!revivingPlayer.isCreative()) {
//                revivingPlayer.displayClientMessage(Component.literal("Must be in creative to revive"), true);
//                return RevivePlayerState.INCAPABLE_OF_REVIVING;
//            }
//            return RevivePlayerState.CAPABLE_OF_REVIVING;
//        }));

        CommonLifecycleEvents.TAGS_LOADED.register((listen, listen2) -> {
            FabricStats.setup();
        });
    }
}
