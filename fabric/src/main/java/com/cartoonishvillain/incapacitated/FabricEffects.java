package com.cartoonishvillain.incapacitated;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;

public class FabricEffects {
    /*
    While code reusage is minimal would like to shout out the immersive engineering team and BluSunrize for having such a neat license to help me get through this bit in particular
    */

    public static Supplier<MobEffect> INCAPSLOW;
    public static Supplier<MobEffect> INCAPWEAK;

    public static void initEffects() {
        INCAPSLOW = registerEffect("incap_slow", new ModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("incapacitated:incap_slow"), (double)-0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        INCAPWEAK = registerEffect("incap_weak", new AttackModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519).addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.parse("incapacitated:incap_weak"), -4.0D, AttributeModifier.Operation.ADD_VALUE));
    }

    private static Supplier<MobEffect> registerEffect(String name, MobEffect effect) {
        MobEffect registered = Registry.register(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), effect);
        return () -> registered;
    }

    public static class ModdedPotionEffects extends MobEffect {

        protected ModdedPotionEffects(MobEffectCategory p_19451_, int p_19452_) {
            super(p_19451_, p_19452_);
        }
    }

    public static class AttackModdedPotionEffects extends MobEffect {

        protected AttackModdedPotionEffects(MobEffectCategory p_19426_, int p_19427_) {
            super(p_19426_, p_19427_);
        }
    }
}
