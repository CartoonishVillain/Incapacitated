package com.cartoonishvillain.incapacitated.config;

import java.io.Serializable;

public class IncapEffectData implements Serializable {
    String effectID;
    int amplifier;
    boolean ambient;

    public IncapEffectData(String effectID, int amplifier, boolean ambient) {
        this.effectID = effectID;
        this.amplifier = amplifier;
        this.ambient = ambient;
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
}
