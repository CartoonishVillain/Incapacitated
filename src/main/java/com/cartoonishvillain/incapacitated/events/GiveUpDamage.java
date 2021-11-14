package com.cartoonishvillain.incapacitated.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class GiveUpDamage extends DamageSource {
    public GiveUpDamage(String p_i1566_1_) {
        super(p_i1566_1_);
    }
    public static DamageSource playerGaveUp(Entity entity){
        return new EntityDamageSource("giveup", entity).bypassArmor();
    }


}
