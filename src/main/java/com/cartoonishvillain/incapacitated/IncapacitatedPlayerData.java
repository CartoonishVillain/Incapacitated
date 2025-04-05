package com.cartoonishvillain.incapacitated;

import net.minecraft.world.damagesource.DamageSource;

import java.io.Serializable;

public class IncapacitatedPlayerData implements Serializable {

    boolean incapacitated = false;
    int ticksUntilDeath = FabricIncapacitated.configData.getDownTicks();
    int downsUntilDeath = FabricIncapacitated.configData.getDownCounter();
    int reviveCounter = FabricIncapacitated.configData.getReviveTicks();
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

    public boolean downReviveCount() {
        reviveCounter--;
        return reviveCounter <= 0;
    }

    public boolean countTicksUntilDeath() {
        ticksUntilDeath--;
        return ticksUntilDeath <= 0;
    }
}
