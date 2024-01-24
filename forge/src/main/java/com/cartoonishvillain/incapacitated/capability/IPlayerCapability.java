package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public interface IPlayerCapability {
    boolean getIsIncapacitated();
    void setIsIncapacitated(boolean isIncapacitated);
    int getTicksUntilDeath();
    boolean countTicksUntilDeath();
    void setTicksUntilDeath(int ticks);
    int getDownsUntilDeath();
    void setDownsUntilDeath(int downs);
    int getReviveCount();
    void setReviveCount(int count);
    DamageSource getSourceOfDeath(Level level);
    void setSourceOfDeath(Level level, DamageSource causeOfDeath);
}
