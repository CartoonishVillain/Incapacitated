package com.cartoonishvillain.incapacitated.capability;

import com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig;
import com.cartoonishvillain.incapacitated.events.BleedOutDamage;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;

import java.io.Serializable;

import static com.cartoonishvillain.incapacitated.events.IncapacitatedDamageSources.BLEEDOUT;

public class IncapacitatedPlayerData implements Serializable {
    boolean incapacitated = false;
    int ticksUntilDeath = IncapacitatedCommonConfig.DOWNTICKS.get();
    int downsUntilDeath = IncapacitatedCommonConfig.DOWNCOUNT.get();
    int reviveCounter = IncapacitatedCommonConfig.REVIVETICKS.get();
    DamageSource damageSource = null;


    public boolean isIncapacitated() {
        return incapacitated;
    }

    public void setIncapacitated(boolean incapacitated) {
        this.incapacitated = incapacitated;
    }

    public int getTicksUntilDeath() {
        return ticksUntilDeath;
    }

    public void setTicksUntilDeath(int ticksUntilDeath) {
        this.ticksUntilDeath = ticksUntilDeath;
    }

    public int getDownsUntilDeath() {
        return downsUntilDeath;
    }

    public void setDownsUntilDeath(int downsUntilDeath) {
        this.downsUntilDeath = downsUntilDeath;
    }

    public int getReviveCounter() {
        return reviveCounter;
    }

    public void setReviveCounter(int reviveCounter) {
        this.reviveCounter = reviveCounter;
    }

    public DamageSource getDamageSource(Level level) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(BLEEDOUT);

        Holder.Reference<DamageType> fallOutOfWorld = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD);

        return damageSource != null
                ? damageSource
                : new BleedOutDamage(damageType, new DamageSource(fallOutOfWorld));
    }

    public void setDamageSource(Level level, DamageSource damageSource) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(BLEEDOUT);

        this.damageSource = new BleedOutDamage(damageType, damageSource);
    }

    public boolean downReviveCount() {
        reviveCounter--;
        return reviveCounter <= 0;
    }

    public boolean countTicksUntilDeath() {
        ticksUntilDeath--;
        return ticksUntilDeath <= 0;
    }
}
