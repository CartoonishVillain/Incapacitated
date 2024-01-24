package com.cartoonishvillain.incapacitated.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public interface IncapacitatedInterface extends Component {
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
