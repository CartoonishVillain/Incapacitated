package com.cartoonishvillain.incapacitated.damage;

import com.cartoonishvillain.incapacitated.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class IncapacitatedDamageSources {
    public static final ResourceKey<DamageType> BLEEDOUT =  ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "bleedout"));
}
