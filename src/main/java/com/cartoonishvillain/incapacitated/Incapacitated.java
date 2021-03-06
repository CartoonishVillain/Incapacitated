package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.config.CommonConfig;
import com.cartoonishvillain.incapacitated.config.ConfigHelper;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("incapacitated")
public class Incapacitated
{
    // Directly reference a log4j logger.
    public static final String MODID = "incapacitated";
    private static final Logger LOGGER = LogManager.getLogger();
    public static CommonConfig config;
    public static ArrayList<ResourceLocation> ReviveFoods;
    public static ArrayList<ResourceLocation> HealingFoods;
    public static ArrayList<String> instantKillDamageSourcesMessageID;

    public Incapacitated() {
        IncapacitationMessenger.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        config = ConfigHelper.register(ModConfig.Type.COMMON, CommonConfig::new);
        instantKillDamageSourcesMessageID = new ArrayList<>(List.of("bleedout", DamageSource.OUT_OF_WORLD.msgId, DamageSource.LAVA.msgId, DamageSource.WITHER.msgId));


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        HealingFoods = getFoodForHealing();
        ReviveFoods = getFoodForReviving();

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private ArrayList<ResourceLocation> getFoodForReviving() {
        final String FoodList = config.REVIVEFOODS.get();
        String[] reviveFoods = FoodList.split(",");
        ArrayList<ResourceLocation> reviveFoodList = new ArrayList<>();
        try {
            for(String string : reviveFoods){
                ResourceLocation food = new ResourceLocation(string);
                reviveFoodList.add(food);
            }
        }catch(ResourceLocationException e){
            Incapacitated.LOGGER.error("Incapacitation: Revive foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of(new ResourceLocation("enchanted_golden_apple")));
        }
        return reviveFoodList;
    }

    private ArrayList<ResourceLocation> getFoodForHealing() {
        final String FoodList = config.HEALINGFOODS.get();
        String[] healFoods = FoodList.split(",");
        ArrayList<ResourceLocation> healFoodList = new ArrayList<>();
        try {
            for(String string : healFoods){
                ResourceLocation food = new ResourceLocation(string);
                healFoodList.add(food);
            }
        }catch(ResourceLocationException e){
            Incapacitated.LOGGER.error("Incapacitation: Healing foods not parsed. Non [a-z0-9_.-] character in config! Using default...");
            return new ArrayList<>(List.of(new ResourceLocation("golden_apple")));
        }
        return healFoodList;
    }

}
