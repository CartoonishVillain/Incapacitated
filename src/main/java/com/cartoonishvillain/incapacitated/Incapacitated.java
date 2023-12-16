package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import com.mojang.logging.LogUtils;

import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig.*;
import static com.cartoonishvillain.incapacitated.events.IncapacitatedDamageSources.BLEEDOUT;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Incapacitated.MODID)
public class Incapacitated
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "incapacitated";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean devMode = false;
    public static List<String> ReviveFoods;
    public static List<String> HealingFoods;
    public static ArrayList<String> instantKillDamageSourcesMessageID;

    //User is invulnerable to damage while down
    public static Boolean merciful = false;
    //User can revive when they get a kill
    public static Boolean hunter = false;
    //User gets slowed while down
    public static Boolean slow = false;
    //User gets weakness while down
    public static Boolean weakened = false;
    //When sleeping successfully, users will gain +1 downs
    public static Boolean regenerating = false;
    //Does the down counter go down?
    public static Boolean unlimitedDowns = false;
//    //Does disconnecting while down kill the player.
//    public static Boolean downLogging = false;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Incapacitated(IEventBus modEventBus)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        PlayerCapability.loadDataAttachment(modEventBus);
        IncapEffects.init(modEventBus);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IncapacitatedCommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IncapacitatedClientConfig.CLIENTSPEC);
        instantKillDamageSourcesMessageID = new ArrayList<>(List.of(BLEEDOUT.location().getPath(), DamageTypes.FELL_OUT_OF_WORLD.location().getPath(), DamageTypes.LAVA.location().getPath(), DamageTypes.WITHER.location().getPath(), "outOfWorld" ));
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        IncapacitationMessenger.register();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        Incapacitated.HealingFoods = (List<String>) HEALINGFOODS.get();
        Incapacitated.ReviveFoods = (List<String>) REVIVEFOODS.get();
        Incapacitated.merciful = MERCIFUL.get();
        Incapacitated.hunter = HUNTER.get();
        Incapacitated.slow = SLOW.get();
        Incapacitated.weakened = WEAKENED.get();
        Incapacitated.regenerating = REGENERATING.get();
        Incapacitated.unlimitedDowns = UNLIMITEDDOWNS.get();
//        Incapacitated.downLogging = DOWNLOGGING.get();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
