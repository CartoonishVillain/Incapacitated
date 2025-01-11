package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.config.IncapConfigData;
import com.cartoonishvillain.incapacitated.config.IncapEffectData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge Events
// however it will be compatible with all supported mod loaders.
public class Incapacitated {

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.

    public static boolean devMode = false;
    public static IncapConfigData configData = null;
    public static ArrayList<String> instantKillDamageSourcesMessageID;
    public static ArrayList<String> noMercyDamageSourcesMessageID;
    public static List<String> reviveFoods;
    public static List<String> adrenalineFoods;
    public static List<String> healingFoods;
    public static ArrayList<MobEffectInstance> effectInstances = new ArrayList<>();
    public static void init() {
        loadConfig();
        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.

        //I _hate_ this implementation of needing to use these strings, but for some reason the damage type resource keys and the damage sources themselves are desynced, and that's just _the worst_.
        //And I don't know how to get that internal string from the keys so. Here we are, I guess.
        noMercyDamageSourcesMessageID = new ArrayList<>(List.of(BLEEDOUT.location().getPath(), "outOfWorld", "generic", "genericKill", "outsideBorder"));

    }

    public static void loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonReader reader = new JsonReader(new FileReader("config/incapacitated.json"));
            configData = gson.fromJson(reader, IncapConfigData.class);
            if (configData != null) {
                reviveFoods = getFoodForReviving();
                adrenalineFoods = getFoodForAdrenaline();
                healingFoods = getFoodForHealing();
                instantKillDamageSourcesMessageID = getInstantKills();
                getEffectInstances();
            }
        } catch (FileNotFoundException e) {
            try (Writer writer = new FileWriter("config/incapacitated.json")) {
                writer.flush();
                gson.toJson(IncapConfigData.buildDefaultConfig(), writer);
                configData = IncapConfigData.buildDefaultConfig();
                reviveFoods = getFoodForReviving();
                healingFoods = getFoodForHealing();
                instantKillDamageSourcesMessageID = getInstantKills();
                getEffectInstances();
            } catch (IOException ex) {
                Constants.LOG.error("Incapacitated: Failed to write default data!");
                throw new RuntimeException(ex);
            }
        }
    }


    private static ArrayList<String> getFoodForReviving() {
        final String FoodList = configData.getFoodReviveList();
        String[] reviveFoods = FoodList.split(",");
        ArrayList<String> reviveFoodList = new ArrayList<>();
        try {
            for(String string : reviveFoods){
                String food = new ResourceLocation(string).toString();
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
            final String FoodList = configData.getFoodAdrenalineList();
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
        final String FoodList = configData.getFoodHealList();
        String[] healFoods = FoodList.split(",");
        ArrayList<String> healFoodList = new ArrayList<>();
        try {
            for(String string : healFoods){
                String food = new ResourceLocation(string).toString();
                healFoodList.add(food);
            }
        } catch(ResourceLocationException e){
            Constants.LOG.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("minecraft:golden_apple"));
        }
        return healFoodList;
    }

    private static ArrayList<String> getInstantKills() {
        final String instantKillsString = configData.getInstantKills();
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
        effectInstances.clear();
        for (IncapEffectData effectData : configData.getIncapEffectData()) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectData.getEffectID()));
            if (effect != null) {
                MobEffectInstance effectInstance = new MobEffectInstance(
                        effect,
                        -1,
                        effectData.getAmplifier(),
                        !effectData.isAmbient(),
                        !effectData.isAmbient()
                );
                effectInstances.add(effectInstance);
            }
        }
    }
}