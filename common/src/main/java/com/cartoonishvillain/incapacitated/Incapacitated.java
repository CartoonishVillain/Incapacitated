package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.config.IncapConfigData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraft.world.damagesource.DamageTypes;

import java.io.*;
import java.util.ArrayList;
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
    public static List<String> ReviveFoods;
    public static List<String> HealingFoods;
    public static void init() {
        loadConfig();

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        instantKillDamageSourcesMessageID = new ArrayList<>(List.of(BLEEDOUT.location().getPath(), DamageTypes.FELL_OUT_OF_WORLD.location().getPath(), DamageTypes.LAVA.location().getPath(), DamageTypes.WITHER.location().getPath(), "outOfWorld"));

        //I _hate_ this implementation of needing to use these strings, but for some reason the damage type resource keys and the damage sources themselves are desynced, and that's just _the worst_.
        //And I don't know how to get that internal string from the keys so. Here we are, I guess.
        noMercyDamageSourcesMessageID = new ArrayList<>(List.of(BLEEDOUT.location().getPath(), "outOfWorld", "generic", "genericKill", "outsideBorder"));

    }

    public static void loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonReader reader = new JsonReader(new FileReader("config/incapacitated.json"));
            configData = gson.fromJson(reader, IncapConfigData.class);
        } catch (FileNotFoundException e) {
            try (Writer writer = new FileWriter("config/incapacitated.json")) {
                gson.toJson(IncapConfigData.buildDefaultConfig(), writer);
                loadConfig();
            } catch (IOException ex) {
                Constants.LOG.error("Incapacitated: Failed to write default data!");
                throw new RuntimeException(ex);
            }
        }
    }
}