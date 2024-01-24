package com.cartoonishvillain.incapacitated.capability;

import com.cartoonishvillain.incapacitated.ForgeIncapacitated;
import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

public class PlayerCapabilityManager implements IPlayerCapability, ICapabilityProvider, INBTSerializable<CompoundTag> {
    protected boolean incapacitated = false;
    protected int ticksUntilDeath = ForgeIncapacitated.config.DOWNTICKS.get();
    protected int downsUntilDeath = ForgeIncapacitated.config.DOWNCOUNT.get();
    protected int reviveCounter = ForgeIncapacitated.config.REVIVETICKS.get();
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
    public int getReviveCount() {
        return reviveCounter;
    }

    @Override
    public void setReviveCount(int count) {
        reviveCounter = count;
    }


    @Override
    public DamageSource getSourceOfDeath(Level level) {
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
    
    @Override
    public void setSourceOfDeath(Level level, DamageSource causeOfDeath) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(BLEEDOUT);

        originalSource = new BleedOutDamage(damageType, causeOfDeath);
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
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        incapacitated = tag.getBoolean("incapacitation");
        ticksUntilDeath = tag.getInt("incapTimer");
        downsUntilDeath = tag.getInt("incapCounter");
    }

}
