package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import com.cartoonishvillain.incapacitated.platform.Services;
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
    int ticksUntilDeath = Services.PLATFORM.commonConfigDownTicks();
    int downsUntilDeath = Services.PLATFORM.commonConfigDownCount();
    int reviveCounter = Services.PLATFORM.commonConfigReviveTicks();
    DamageSource originalSource = null;

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

    public DamageSource getDamageSource(Level level, Player player) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(BLEEDOUT);

        Holder.Reference<DamageType> fallOutOfWorld = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD);

        return originalSource != null
                ? originalSource
                : new BleedOutDamage(damageType, new DamageSource(fallOutOfWorld));
    }

    public void setDamageSource(Level level, DamageSource damageSource, Player player) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(BLEEDOUT);

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
