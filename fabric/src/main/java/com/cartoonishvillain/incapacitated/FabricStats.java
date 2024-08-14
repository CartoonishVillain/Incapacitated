package com.cartoonishvillain.incapacitated;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * Credit to BluSunrize and their team, this stats registration and handling is largely based on Immersive Engineering's model.
 */

public class FabricStats {
    private static final List<Runnable> RUN_IN_SETUP = new ArrayList<>();

    public static Supplier<ResourceLocation> TIMES_INCAPPED;
    public static Supplier<ResourceLocation> TIMES_REVIVED;
    public static Supplier<ResourceLocation> TIMES_REVIVED_SELF;

    public static void modConstruction()
    {
        TIMES_INCAPPED = registerCustomStat("times_incapped", StatFormatter.DEFAULT);
        TIMES_REVIVED = registerCustomStat("times_revived", StatFormatter.DEFAULT);
        TIMES_REVIVED_SELF = registerCustomStat("times_revived_self", StatFormatter.DEFAULT);
    }

    public static void setup()
    {
        RUN_IN_SETUP.forEach(Runnable::run);
    }

    private static Supplier<ResourceLocation> registerCustomStat(String name, StatFormatter formatter)
    {
        ResourceLocation regName = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, regName, regName);
        RUN_IN_SETUP.add(() -> Stats.CUSTOM.get(regName, formatter));
        return () -> regName;
    }
}
