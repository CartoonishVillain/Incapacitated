package com.cartoonishvillain.incapacitated;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IncapEffects {
    public static DeferredHolder<MobEffect, MobEffect> incapSlow;
    public static DeferredHolder<MobEffect, MobEffect> incapWeak;

    public static void init(IEventBus modBus){
        incapSlow = MOB_EFFECTS.register("incap_slow", () -> new ModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519, new ResourceLocation(Constants.MOD_ID, "incap_slow")).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1B169290", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        incapWeak = MOB_EFFECTS.register("incap_weak", () -> new AttackModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519,  new ResourceLocation(Constants.MOD_ID, "incap_weak")).addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-11AE-492C-9B6B-9971489B5BE5", -4.0D, AttributeModifier.Operation.ADDITION));
        MOB_EFFECTS.register(modBus);
    }

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Constants.MOD_ID);

    /*
        While code reusage is minimal would like to shout out the immersive engineering team and BluSunrize for having such a neat license to help me get through this bit in particular
     */
    public static class ModdedPotionEffects extends MobEffect {

        protected ModdedPotionEffects(MobEffectCategory p_19451_, int p_19452_, ResourceLocation location) {
            super(p_19451_, p_19452_);
        }
    }

    public static class AttackModdedPotionEffects extends MobEffect {

        protected AttackModdedPotionEffects(MobEffectCategory p_19426_, int p_19427_, ResourceLocation location) {
            super(p_19426_, p_19427_);
        }
    }
}
