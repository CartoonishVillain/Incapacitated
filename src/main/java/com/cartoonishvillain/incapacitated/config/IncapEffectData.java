package com.cartoonishvillain.incapacitated.config;

import java.io.Serializable;

public class IncapEffectData implements Serializable {
    String effectID;
    int amplifier;
    boolean ambient;


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
