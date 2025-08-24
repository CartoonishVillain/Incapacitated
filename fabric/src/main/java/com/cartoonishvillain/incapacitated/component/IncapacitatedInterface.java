package com.cartoonishvillain.incapacitated.component;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import org.ladysnake.cca.api.v3.component.Component;

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
    float getLastDmgTaken();
    void setLastDmgTaken(float lastDmgTaken);
    float getLastHealthBeforeDamage();
    void setLastHealthBeforeDamage(float lastHealthBeforeDamage);
    DamageSource getSourceOfDeath(Level level);
    void setSourceOfDeath(Level level, DamageSource causeOfDeath);
    int getKillsRequiredForRevive();
    void setKillsRequiredForRevive(int killsRequiredForRevive);
}
