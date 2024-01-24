package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class IncapacitatedSerializer implements IAttachmentSerializer<CompoundTag, NeoForgeIncapacitatedPlayerData> {
    @Override
    public NeoForgeIncapacitatedPlayerData read(IAttachmentHolder entity, CompoundTag tag) {
        NeoForgeIncapacitatedPlayerData playerData = new NeoForgeIncapacitatedPlayerData();
        playerData.setIncapacitated(tag.getBoolean("incapacitated"));
        playerData.setTicksUntilDeath(tag.getInt("ticksuntildeath"));
        playerData.setDownsUntilDeath(tag.getInt("downsuntildeath"));
        return playerData;
    }

    @Override
    public CompoundTag write(NeoForgeIncapacitatedPlayerData attachment) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("incapacitated",  attachment.isIncapacitated());
        compoundTag.putInt("ticksuntildeath", attachment.getTicksUntilDeath());
        compoundTag.putInt("downsuntildeath", attachment.getDownsUntilDeath());
        return compoundTag;
    }
}
