package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.DefaultConfig;
import com.cartoonishvillain.incapacitated.config.IncapConfigData;
import com.cartoonishvillain.incapacitated.config.IncapEffectData;
import com.cartoonishvillain.incapacitated.config.SimpleConfig;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

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

        Incapacitated.noMercyDamageSourcesMessageID = new ArrayList<>(List.of(BLEEDOUT.location().getPath(), "outOfWorld", "generic", "genericKill", "outsideBorder"));
    }


    public static void loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonReader reader = new JsonReader(new FileReader("config/incapacitated.json"));
            Incapacitated.configData = gson.fromJson(reader, IncapConfigData.class);
            if (Incapacitated.configData != null) {
                Incapacitated.reviveFoods = getFoodForReviving();
                Incapacitated.adrenalineFoods = getFoodForAdrenaline();
                Incapacitated.healingFoods = getFoodForHealing();
                Incapacitated.instantKillDamageSourcesMessageID = getInstantKills();
                getEffectInstances();
            }
        } catch (FileNotFoundException e) {
            try (Writer writer = new FileWriter("config/incapacitated.json")) {
                writer.flush();
                gson.toJson(IncapConfigData.buildDefaultConfig(), writer);
                Incapacitated.configData = IncapConfigData.buildDefaultConfig();
                Incapacitated.reviveFoods = getFoodForReviving();
                Incapacitated.healingFoods = getFoodForHealing();
                Incapacitated.instantKillDamageSourcesMessageID = getInstantKills();
                getEffectInstances();
            } catch (IOException ex) {
                Constants.LOG.error("Incapacitated: Failed to write default data!");
                throw new RuntimeException(ex);
            }
        }
    }


    private static ArrayList<String> getFoodForReviving() {
        final String FoodList = Incapacitated.configData.getFoodReviveList();
        String[] reviveFoods = FoodList.split(",");
        ArrayList<String> reviveFoodList = new ArrayList<>();
        try {
            for(String string : reviveFoods){
                String food = ResourceLocation.parse(string).toString();
                reviveFoodList.add(food);
            }
        } catch(ResourceLocationException e){
            Constants.LOG.error("Incapacitation: Revive foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("minecraft:enchanted_golden_apple"));
        }
        return reviveFoodList;
    }

    private static ArrayList<String> getFoodForAdrenaline() {
        try {
            final String FoodList = Incapacitated.configData.getFoodAdrenalineList();
            String[] reviveFoods = FoodList.split(",");
            ArrayList<String> reviveFoodList = new ArrayList<>();
            try {
                for (String string : reviveFoods) {
                    String food = ResourceLocation.parse(string).toString();
                    reviveFoodList.add(food);
                }
            } catch (ResourceLocationException e) {
                Constants.LOG.error("Incapacitation: Adrenaline foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
                return new ArrayList<>(List.of());
            }
            return reviveFoodList;
        } catch (NullPointerException e) {
            Constants.LOG.error("Incapacitation: Adrenaline foods not parsed. It is likely this value is not in your config file! Using default...");
            return new ArrayList<>(List.of());
        }
    }

    private static ArrayList<String> getFoodForHealing() {
        final String FoodList = Incapacitated.configData.getFoodHealList();
        String[] healFoods = FoodList.split(",");
        ArrayList<String> healFoodList = new ArrayList<>();
        try {
            for(String string : healFoods){
                String food = ResourceLocation.parse(string).toString();
                healFoodList.add(food);
            }
        } catch(ResourceLocationException e){
            Constants.LOG.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("minecraft:golden_apple"));
        }
        return healFoodList;
    }

    private static ArrayList<String> getInstantKills() {
        final String instantKillsString = Incapacitated.configData.getInstantKills();
        String[] damageTypes = instantKillsString.split(",");
        ArrayList<String> instantKills;
        try {
            instantKills = new ArrayList<>(Arrays.asList(damageTypes));
        } catch(ResourceLocationException e){
            Constants.LOG.error("Incapacitation: Instant Kills. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("wither", "lava", "outOfWorld"));
        }
        return instantKills;
    }

    private static void getEffectInstances() {
        Incapacitated.effectInstances.clear();
        for (IncapEffectData effectData : Incapacitated.configData.getIncapEffectData()) {
            Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectData.getEffectID()));
            if (effect.isPresent()) {
                MobEffectInstance effectInstance = new MobEffectInstance(
                        BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect.get().value()),
                        -1,
                        effectData.getAmplifier(),
                        !effectData.isAmbient(),
                        !effectData.isAmbient()
                );
                Incapacitated.effectInstances.add(effectInstance);
            }
        }
    }
}
