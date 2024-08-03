package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class IncapacitatedSerializer implements IAttachmentSerializer<CompoundTag, NeoForgeIncapacitatedPlayerData> {

    @Override
    public NeoForgeIncapacitatedPlayerData read(IAttachmentHolder iAttachmentHolder, CompoundTag tag, HolderLookup.Provider provider) {
        NeoForgeIncapacitatedPlayerData playerData = new NeoForgeIncapacitatedPlayerData();
        playerData.setIncapacitated(tag.getBoolean("incapacitated"));
        playerData.setTicksUntilDeath(tag.getInt("ticksuntildeath"));
        playerData.setDownsUntilDeath(tag.getInt("downsuntildeath"));
        return playerData;
    }

    @Override
    public @Nullable CompoundTag write(NeoForgeIncapacitatedPlayerData attachment, HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("incapacitated",  attachment.isIncapacitated());
        compoundTag.putInt("ticksuntildeath", attachment.getTicksUntilDeath());
        compoundTag.putInt("downsuntildeath", attachment.getDownsUntilDeath());
        return compoundTag;
    }
}
