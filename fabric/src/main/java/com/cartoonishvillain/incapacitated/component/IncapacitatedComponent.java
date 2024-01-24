package com.cartoonishvillain.incapacitated.component;

import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

public class IncapacitatedComponent implements IncapacitatedInterface, AutoSyncedComponent {
    private final Object provider;
    protected boolean incapacitated = false;
    protected int ticksUntilDeath = FabricIncapacitated.downTicks;
    protected int downsUntilDeath = FabricIncapacitated.downCounter;
    protected int reviveCounter = FabricIncapacitated.reviveTicks;
    private DamageSource originalSource;

    public IncapacitatedComponent(Object provider){this.provider = provider;}

    @Override
    public boolean getIsIncapacitated() {
        return incapacitated;
    }

    @Override
    public void setIsIncapacitated(boolean isIncapacitated) {
        incapacitated = isIncapacitated;
        ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE.sync(this.provider);
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
        ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE.sync(this.provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        incapacitated = tag.getBoolean("incapacitation");
        ticksUntilDeath = tag.getInt("incapTimer");
        downsUntilDeath = tag.getInt("incapCounter");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("incapacitation", incapacitated);
        tag.putInt("incapTimer", ticksUntilDeath);
        tag.putInt("incapCounter", downsUntilDeath);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeBoolean(this.getIsIncapacitated());
        buf.writeInt(this.getTicksUntilDeath());
        buf.writeInt(this.getDownsUntilDeath());
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        this.setIsIncapacitated(buf.readBoolean());
        this.setTicksUntilDeath(buf.readInt());
        this.setDownsUntilDeath(buf.readInt());
    }
}
