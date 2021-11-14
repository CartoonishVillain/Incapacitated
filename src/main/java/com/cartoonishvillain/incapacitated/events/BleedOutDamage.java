package com.cartoonishvillain.incapacitated.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class BleedOutDamage extends DamageSource {
    public BleedOutDamage(String p_i1566_1_) {
        super(p_i1566_1_);
    }
    public static DamageSource playerOutOfTime(Entity entity){
        return new EntityDamageSource("bleedout", entity).bypassArmor();
    }


}
