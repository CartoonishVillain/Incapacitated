package com.cartoonishvillain.incapacitated.capability;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.events.BleedOutDamage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerCapabilityManager implements IPlayerCapability, ICapabilityProvider, INBTSerializable<CompoundTag> {
    protected boolean incapacitated = false;
    protected int ticksUntilDeath = Incapacitated.config.DOWNTICKS.get();
    protected int downsUntilDeath = Incapacitated.config.DOWNCOUNT.get();
    protected int giveUpJumps = 3;
    protected int reviveCounter = Incapacitated.config.REVIVETICKS.get();
    protected int jumpDelay = 0;
    public final LazyOptional<IPlayerCapability> holder = LazyOptional.of(()->this);
    private DamageSource originalSource;
    
    @Override
    public boolean getIsIncapacitated() {
        return incapacitated;
    }

    @Override
    public void setIsIncapacitated(boolean isIncapacitated) {
        incapacitated = isIncapacitated;
    }

    @Override
    public int getTicksUntilDeath() {
        return ticksUntilDeath;
    }

    @Override
    public boolean countTicksUntilDeath() {
        ticksUntilDeath--;
        return ticksUntilDeath <= 0;
    }

    @Override
    public void setTicksUntilDeath(int ticks) {
        ticksUntilDeath = ticks;
    }

    @Override
    public int getDownsUntilDeath() {
        return downsUntilDeath;
    }

    @Override
    public void setDownsUntilDeath(int downs) {
        downsUntilDeath = downs;
    }

    @Override
    public boolean giveUpJumpCount() {
        giveUpJumps--;
        return giveUpJumps <= 0;
    }

    @Override
    public void resetGiveUpJumps() {
        giveUpJumps = 3;
    }

    @Override
    public int getJumpCount() {
        return giveUpJumps;
    }

    @Override
    public void setJumpCount(int jumps) {
        giveUpJumps = jumps;
    }

    @Override
    public boolean downReviveCount() {
        reviveCounter--;
        return reviveCounter <= 0;
    }

    @Override
    public int getReviveCount() {
        return reviveCounter;
    }

    @Override
    public void setReviveCount(int count) {
        reviveCounter = count;
    }

    @Override
    public int getJumpDelay() {
        return jumpDelay;
    }

    @Override
    public void setJumpDelay(int delay) {
        jumpDelay = delay;
    }

    @Override
    public void countDelay() {
        if(jumpDelay > 0) jumpDelay--;
        else if(jumpDelay < 0) jumpDelay = 0;
    }

    @Override
    public DamageSource getSourceOfDeath() {
        return originalSource != null
                ? originalSource.bypassArmor()
                : new BleedOutDamage(DamageSource.OUT_OF_WORLD).bypassArmor();
    }
    
    @Override
    public void setSourceOfDeath(DamageSource causeOfDeath) {
        originalSource = new BleedOutDamage(causeOfDeath);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == PlayerCapability.INSTANCE){ return PlayerCapability.INSTANCE.orEmpty(cap, this.holder); }
        else return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("incapacitation", incapacitated);
        tag.putInt("incapTimer", ticksUntilDeath);
        tag.putInt("incapCounter", downsUntilDeath);
        tag.putInt("jumpCounter", giveUpJumps);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        incapacitated = tag.getBoolean("incapacitation");
        ticksUntilDeath = tag.getInt("incapTimer");
        downsUntilDeath = tag.getInt("incapCounter");
        giveUpJumps = tag.getInt("jumpCounter");
    }

}
