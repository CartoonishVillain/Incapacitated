package com.cartoonishvillain.incapacitated.events;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class BleedOutDamage extends DamageSource{
    private final DamageSource originalSource;

    public BleedOutDamage(Holder<DamageType> p_270475_, DamageSource originalKillMethod) {
        super(p_270475_);
        originalSource = originalKillMethod;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity player) {
        Component originalDeathMsg = originalSource.getLocalizedDeathMessage(player);
        return Component.translatable("KillPlayer.attack." + this.getMsgId(), originalDeathMsg);
    }
}