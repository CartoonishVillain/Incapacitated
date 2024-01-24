package com.cartoonishvillain.incapacitated;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeIncapEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Constants.MOD_ID);

    public static final RegistryObject<MobEffect> incapSlow = MOB_EFFECT_REGISTER.register("incap_slow", () -> new ModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1B169290", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> incapWeak = MOB_EFFECT_REGISTER.register("incap_weak", () -> new AttackModdedPotionEffects(MobEffectCategory.HARMFUL, 4587519).addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-11AE-492C-9B6B-9971489B5BE5", -4.0, AttributeModifier.Operation.ADDITION));


    public static void init(){
        MOB_EFFECT_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    /*
        While code reusage is minimal would like to shout out the immersive engineering team and BluSunrize for having such a neat license to help me get through this bit in particular
     */
    public static class ModdedPotionEffects extends MobEffect {

        public ModdedPotionEffects(MobEffectCategory p_19451_, int p_19452_) {
            super(p_19451_, p_19452_);
        }
    }

    public static class AttackModdedPotionEffects extends MobEffect {

        protected AttackModdedPotionEffects(MobEffectCategory p_19426_, int p_19427_) {
            super(p_19426_, p_19427_);
        }
    }
}
