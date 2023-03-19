package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class IncapacitatedDamageSources {
    public static final ResourceKey<DamageType> BLEEDOUT =  ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Incapacitated.MODID, "bleedout"));
}
