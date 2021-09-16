package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class PlayerCapability {
    @CapabilityInject(IPlayerCapability.class)
    public static Capability<IPlayerCapability> INSTANCE = null;
}
