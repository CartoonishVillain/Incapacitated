package com.cartoonishvillain.incapacitated.capability;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;

import java.io.Serializable;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

public class NeoForgeIncapacitatedPlayerData implements Serializable {

    boolean incapacitated = false;
    int ticksUntilDeath = Incapacitated.configData.getDownTicks();
    int downsUntilDeath = Incapacitated.configData.getDownCounter();
    int reviveCounter = Incapacitated.configData.getReviveTicks();
    float lastDmgTaken = 0f;
    float lastHealthBeforeDamage = 20f;
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

    public float getLastHealthBeforeDamage() {
        return lastHealthBeforeDamage;
    }

    public void setLastHealthBeforeDamage(float lastHealthBeforeDamage) {
        this.lastHealthBeforeDamage = lastHealthBeforeDamage;
    }

    public float getLastDmgTaken() {
        return lastDmgTaken;
    }

    public void setLastDmgTaken(float lastDmgTaken) {
        this.lastDmgTaken = lastDmgTaken;
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
