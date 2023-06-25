package com.cartoonishvillain.incapacitated.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class BleedOutDamage extends DamageSource{
    private final DamageSource originalSource;

    public BleedOutDamage(DamageSource originalKillMethod) {
        super("bleedout");
        originalSource = originalKillMethod;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity player) {
        Component originalDeathMsg = originalSource.getLocalizedDeathMessage(player);
        return Component.translatable("death.attack." + this.msgId, originalDeathMsg);
    }
}