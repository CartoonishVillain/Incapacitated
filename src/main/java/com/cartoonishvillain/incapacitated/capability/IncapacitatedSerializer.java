package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class IncapacitatedSerializer implements IAttachmentSerializer<CompoundTag, IncapacitatedPlayerData> {
    @Override
    public IncapacitatedPlayerData read(CompoundTag tag) {
        IncapacitatedPlayerData playerData = new IncapacitatedPlayerData();
        playerData.setIncapacitated(tag.getBoolean("incapacitated"));
        playerData.setTicksUntilDeath(tag.getInt("ticksuntildeath"));
        playerData.setDownsUntilDeath(tag.getInt("downsuntildeath"));
        return playerData;
    }

    @Override
    public CompoundTag write(IncapacitatedPlayerData attachment) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("incapacitated",  attachment.isIncapacitated());
        compoundTag.putInt("ticksuntildeath", attachment.getTicksUntilDeath());
        compoundTag.putInt("downsuntildeath", attachment.getDownsUntilDeath());
        return compoundTag;
    }
}
