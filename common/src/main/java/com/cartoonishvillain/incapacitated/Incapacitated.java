package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.config.IncapConfigData;
import com.cartoonishvillain.incapacitated.config.IncapEffectData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.io.*;
import java.util.ArrayList;

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

    public static ArrayList<IncapEffectData> effectInstances = new ArrayList<>();
    public static ArrayList<IncapEffectData> reviveInstances = new ArrayList<>();
    public static final TagKey<EntityType<?>> NOTFORHUNTING = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "not_for_hunting"));
    public static final TagKey<DamageType> instantKillDamageSources = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "no_incap"));
    public static final TagKey<DamageType> noMercyDamageSources = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "no_mercy"));;
    public static final TagKey<Item> reviveFoods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "revive_food"));
    public static final TagKey<Item> healingFoods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "healing_food"));
    public static final TagKey<Item> adrenalineFoods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "adrenaline_food"));

    public static void init() {
        loadConfig();
        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
    }

    public static void loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonReader reader = new JsonReader(new FileReader("config/incapacitated.json"));
            configData = gson.fromJson(reader, IncapConfigData.class);
            if (configData != null) {
                getEffectInstances();
            }
        } catch (FileNotFoundException e) {
            try (Writer writer = new FileWriter("config/incapacitated.json")) {
                writer.flush();
                gson.toJson(IncapConfigData.buildDefaultConfig(), writer);
                configData = IncapConfigData.buildDefaultConfig();;
                getEffectInstances();
            } catch (IOException ex) {
                Constants.LOG.error("Incapacitated: Failed to write default data!");
                throw new RuntimeException(ex);
            }
        }
    }

    private static void getEffectInstances() {
        effectInstances.clear();
        effectInstances = configData.getIncapEffectData();

        reviveInstances.clear();
        reviveInstances = configData.getReviveEffectData();
    }
}