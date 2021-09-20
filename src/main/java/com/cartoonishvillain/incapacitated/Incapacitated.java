package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.config.CommonConfig;
import com.cartoonishvillain.incapacitated.config.ConfigHelper;
import com.cartoonishvillain.incapacitated.networking.incapacitationMessenger;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Incapacitated() {
        incapacitationMessenger.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        config = ConfigHelper.register(ModConfig.Type.COMMON, CommonConfig::new);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        HealingFoods = getFoodForHealing();
        ReviveFoods = getFoodForReviving();
        PlayerCapability.register();
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
            ArrayList<ResourceLocation> errorReturnList = new ArrayList<>();
            errorReturnList.add(new ResourceLocation("enchanted_golden_apple"));
            return errorReturnList;
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
            ArrayList<ResourceLocation> errorReturnList = new ArrayList<>();
            errorReturnList.add(new ResourceLocation("golden_apple"));
            return errorReturnList;
        }
        return healFoodList;
    }
}
