package com.cartoonishvillain.incapacitated;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/*
 * Credit to BluSunrize and their team, this stats registration and handling is largely based on Immersive Engineering's model.
 */

public class NFIncapStats {

    private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(
            Registries.CUSTOM_STAT, Constants.MOD_ID
    );

    private static final List<Runnable> RUN_IN_SETUP = new ArrayList<>();

    public static final Holder<ResourceLocation> TIMES_INCAPPED = registerCustomStat("times_incapped", StatFormatter.DEFAULT);
    public static final Holder<ResourceLocation> TIMES_REVIVED = registerCustomStat("times_revived", StatFormatter.DEFAULT);
    public static final Holder<ResourceLocation> TIMES_REVIVED_SELF = registerCustomStat("times_revived_self", StatFormatter.DEFAULT);

    public static void modConstruction(IEventBus modBus)
    {
        REGISTER.register(modBus);
    }

    public static void setup()
    {
        RUN_IN_SETUP.forEach(Runnable::run);
    }

    private static Holder<ResourceLocation> registerCustomStat(String name, StatFormatter formatter)
    {
        return REGISTER.register(name, () -> {
            ResourceLocation regName = new ResourceLocation(Constants.MOD_ID, name);
            RUN_IN_SETUP.add(() -> Stats.CUSTOM.get(regName, formatter));
            return regName;
        });
    }
}
