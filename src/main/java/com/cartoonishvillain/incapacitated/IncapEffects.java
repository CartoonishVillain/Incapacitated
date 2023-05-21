package com.cartoonishvillain.incapacitated;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

public class IncapEffects {
    public static MobEffect incapSlow;
    public static MobEffect incapWeak;

    public static void init(){
        incapSlow = new ModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519, new ResourceLocation(Incapacitated.MODID, "incap_slow")).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1B169290", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        incapWeak = new AttackModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519, -20.0D, new ResourceLocation(Incapacitated.MODID, "incap_weak")).addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-11AE-492C-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION);
    }

    /*
        While code reusage is minimal would like to shout out the immersive engineering team and BluSunrize for having such a neat license to help me get through this bit in particular
     */
    public static class ModdedPotionEffects extends MobEffect {

        protected ModdedPotionEffects(MobEffectCategory p_19451_, int p_19452_, ResourceLocation location) {
            super(p_19451_, p_19452_);
            ForgeRegistries.MOB_EFFECTS.register(this.setRegistryName(location));
        }
    }

    public static class AttackModdedPotionEffects extends AttackDamageMobEffect {

        protected AttackModdedPotionEffects(MobEffectCategory p_19426_, int p_19427_, double p_19428_, ResourceLocation location) {
            super(p_19426_, p_19427_, p_19428_);
            ForgeRegistries.MOB_EFFECTS.register(this.setRegistryName(location));
        }
    }
}
