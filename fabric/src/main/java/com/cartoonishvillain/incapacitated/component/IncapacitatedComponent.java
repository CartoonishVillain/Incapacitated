package com.cartoonishvillain.incapacitated.component;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.damage.BleedOutDamage;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import static com.cartoonishvillain.incapacitated.damage.IncapacitatedDamageSources.BLEEDOUT;

public class IncapacitatedComponent implements IncapacitatedInterface, AutoSyncedComponent {
    private final Object provider;
    protected boolean incapacitated = false;
    protected int ticksUntilDeath = Incapacitated.configData.getDownTicks();
    protected int downsUntilDeath = Incapacitated.configData.getDownCounter();
    protected int reviveCounter = Incapacitated.configData.getReviveTicks();
    int killsRequiredForRevive = Incapacitated.configData.isHunter();
    protected boolean isShader = false;
    float lastDmgTaken = 0f;
    float lastHealthBeforeDamage = 20f;
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

    public boolean isShader() {
        return isShader;
    }

    public void setShader(boolean shader) {
        isShader = shader;
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
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(BLEEDOUT);

        Holder.Reference<DamageType> fallOutOfWorld = level.registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(DamageTypes.FELL_OUT_OF_WORLD);

        return originalSource != null
                ? originalSource
                : new BleedOutDamage(damageType, new DamageSource(fallOutOfWorld));
    }

    @Override
    public void setSourceOfDeath(Level level, DamageSource causeOfDeath) {
        Holder.Reference<DamageType> damageType = level.registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(BLEEDOUT);


        originalSource = new BleedOutDamage(damageType, causeOfDeath);;
        ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE.sync(this.provider);
    }

    @Override
    public int getKillsRequiredForRevive() {
        return killsRequiredForRevive;
    }

    @Override
    public void setKillsRequiredForRevive(int killsRequiredForRevive) {
        this.killsRequiredForRevive = killsRequiredForRevive;
    }

    @Override
    public float getLastDmgTaken() {
        return lastDmgTaken;
    }

    @Override
    public void setLastDmgTaken(float lastDmgTaken) {
        this.lastDmgTaken = lastDmgTaken;
    }

    @Override
    public float getLastHealthBeforeDamage() {
        return lastHealthBeforeDamage;
    }

    @Override
    public void setLastHealthBeforeDamage(float lastHealthBeforeDamage) {
        this.lastHealthBeforeDamage = lastHealthBeforeDamage;
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeBoolean(this.getIsIncapacitated());
        buf.writeInt(this.getTicksUntilDeath());
        buf.writeInt(this.getDownsUntilDeath());
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        this.setIsIncapacitated(buf.readBoolean());
        this.setTicksUntilDeath(buf.readInt());
        this.setDownsUntilDeath(buf.readInt());
    }

    @Override
    public void readData(ValueInput valueInput) {
        incapacitated = valueInput.getBooleanOr("incapacitation", false);
        ticksUntilDeath = valueInput.getIntOr("incapTimer", 100);
        downsUntilDeath = valueInput.getIntOr("incapCounter", 3);
        isShader = valueInput.getBooleanOr("incapShader", false);
    }

    @Override
    public void writeData(ValueOutput valueOutput) {
        valueOutput.putBoolean("incapacitation", incapacitated);
        valueOutput.putInt("incapTimer", ticksUntilDeath);
        valueOutput.putInt("incapCounter", downsUntilDeath);
        valueOutput.putBoolean("incapShader", isShader);
    }
}
