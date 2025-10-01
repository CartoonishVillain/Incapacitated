package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.Serializable;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

public class IncapacitatedPlayerData implements Serializable {

    boolean incapacitated = false;
    int ticksUntilDeath = Incapacitated.configData.getDownTicks();
    int downsUntilDeath = Incapacitated.configData.getDownCounter();
    int reviveCounter = Incapacitated.configData.getReviveTicks();
    int killsRequiredForRevive = Incapacitated.configData.isHunter();
    float lastDmgTaken = 0f;
    float lastHealthBeforeDamage = 20f;
    DamageSource originalSource = null;

    public boolean isIncapacitated() {
        return incapacitated;
    }

    public void setIncapacitated(boolean incapacitated) {
        this.incapacitated = incapacitated;
    }

    public int getKillsRequiredForRevive() {
        return killsRequiredForRevive;
    }

    public void setKillsRequiredForRevive(int killsRequiredForRevive) {
        this.killsRequiredForRevive = killsRequiredForRevive;
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

    public float getLastDmgTaken() {
        return lastDmgTaken;
    }

    public void setLastDmgTaken(float lastDmgTaken) {
        this.lastDmgTaken = lastDmgTaken;
    }

    public float getLastHealthBeforeDamage() {
        return lastHealthBeforeDamage;
    }

    public void setLastHealthBeforeDamage(float lastHealthBeforeDamage) {
        this.lastHealthBeforeDamage = lastHealthBeforeDamage;
    }

    public DamageSource getDamageSource(Level level, Player player) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .getOrThrow(Registries.DAMAGE_TYPE)
                .value().getOrThrow(BLEEDOUT);

        Holder.Reference<DamageType> fallOutOfWorld = level.registryAccess()
                .getOrThrow(Registries.DAMAGE_TYPE)
                .value().getOrThrow(DamageTypes.FELL_OUT_OF_WORLD);

        return originalSource != null
                ? originalSource
                : new BleedOutDamage(damageType, new DamageSource(fallOutOfWorld));
    }

    public void setDamageSource(Level level, DamageSource damageSource, Player player) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .getOrThrow(Registries.DAMAGE_TYPE)
                .value().getOrThrow(BLEEDOUT);

        originalSource = new BleedOutDamage(damageType, damageSource);
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
