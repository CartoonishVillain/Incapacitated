package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.DefaultConfig;
import com.cartoonishvillain.incapacitated.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FabricIncapacitated implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final SimpleConfig CONFIG = SimpleConfig.of("incapacitated").provider(DefaultConfig::provider).request();
    public static boolean lastDownDesaturate = CONFIG.getOrDefault("lastDownDesaturate", true);
    public static int merciful = CONFIG.getOrDefault("merciful", 0);
    public static boolean hunter = CONFIG.getOrDefault("hunter", false);
    public static boolean slow = CONFIG.getOrDefault("slow", false);
    public static boolean weakened = CONFIG.getOrDefault("weakened", false);
    public static boolean regenerating = CONFIG.getOrDefault("regenerating", false);
    public static boolean unlimitedDowns = CONFIG.getOrDefault("unlimitedDowns", false);
    public static boolean downLogging = CONFIG.getOrDefault("downLogging", false);
    public static boolean reviveMessage = CONFIG.getOrDefault("reviveMessage", true);
    public static String reviveFoods = CONFIG.getOrDefault("foodReviveList", "minecraft:enchanted_golden_apple");
    public static String healFoods = CONFIG.getOrDefault("foodHealList", "minecraft:golden_apple");
    public static int downTicks = CONFIG.getOrDefault("downTicks", 2000);
    public static int reviveTicks = CONFIG.getOrDefault("reviveTicks", 150);
    public static int downCounter = CONFIG.getOrDefault("downCounter", 3);
    public static boolean glowingWhileDowned = CONFIG.getOrDefault("glowingWhileDowned", true);
    public static boolean globalIncapMessage = CONFIG.getOrDefault("globalIncapMessage", true);
    public static boolean globalReviveMessage = CONFIG.getOrDefault("globalReviveMessage", true);
    public static boolean useSecondsForRevive = CONFIG.getOrDefault("useSecondsForRevive", false);
    public static boolean someInstantKills = CONFIG.getOrDefault("someInstantKills", true);

    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Incapacitated.init();
        Incapacitated.HealingFoods = getFoodForHealing();
        Incapacitated.ReviveFoods = getFoodForReviving();
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Constants.MOD_ID, "incap_slow"), FabricEffects.incapSlow);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Constants.MOD_ID, "incap_weak"), FabricEffects.incapWeak);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            GetDownCount.register(dispatcher);
            IncapDevMode.register(dispatcher);
            KillPlayer.register(dispatcher);
            SetDownCount.register(dispatcher);
            SetIncapacitatedCommand.register(dispatcher);
        }));
    }

    private ArrayList<String> getFoodForReviving() {
        final String FoodList = reviveFoods;
        String[] reviveFoods = FoodList.split(",");
        ArrayList<String> reviveFoodList = new ArrayList<>();
        try {
            for(String string : reviveFoods){
                String food = new ResourceLocation(string).getPath();
                reviveFoodList.add(food);
            }
        }catch(ResourceLocationException e){
            LOGGER.error("Incapacitation: Revive foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("enchanted_golden_apple"));
        }
        return reviveFoodList;
    }

    private ArrayList<String> getFoodForHealing() {
        final String FoodList = healFoods;
        String[] healFoods = FoodList.split(",");
        ArrayList<String> healFoodList = new ArrayList<>();
        try {
            for(String string : healFoods){
                String food = new ResourceLocation(string).getPath();
                healFoodList.add(food);
            }
        }catch(ResourceLocationException e){
            LOGGER.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of("golden_apple"));
        }
        return healFoodList;
    }
}
