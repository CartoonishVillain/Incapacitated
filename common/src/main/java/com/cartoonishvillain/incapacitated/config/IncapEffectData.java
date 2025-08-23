package com.cartoonishvillain.incapacitated.config;

import java.io.Serializable;

public class IncapEffectData implements Serializable {
    String effectID;
    int amplifier;
    boolean ambient;
    boolean infinite;
    int ticksActive;

    public IncapEffectData(String effectID, int amplifier, boolean ambient, boolean infinite, int ticksActive) {
        this.effectID = effectID;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.infinite = infinite;
        this.ticksActive = ticksActive;
    }

    public String getEffectID() {
        return effectID;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public int getTicksActive() {
        return ticksActive;
    }
}
