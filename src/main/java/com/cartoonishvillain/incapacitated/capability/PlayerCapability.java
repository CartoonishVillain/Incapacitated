package com.cartoonishvillain.incapacitated.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class PlayerCapability {
    @CapabilityInject(IPlayerCapability.class)
    public static Capability<IPlayerCapability> INSTANCE = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(IPlayerCapability.class, new Capability.IStorage<IPlayerCapability>() {
            @Override
            public INBT writeNBT(Capability<IPlayerCapability> capability, IPlayerCapability instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putBoolean("incapacitation", instance.getIsIncapacitated());
                tag.putInt("incapTimer", instance.getTicksUntilDeath());
                tag.putInt("incapCounter", instance.getDownsUntilDeath());
                tag.putInt("jumpCounter", instance.getJumpCount());
                return tag;
            }

            @Override
            public void readNBT(Capability<IPlayerCapability> capability, IPlayerCapability instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.setIsIncapacitated(tag.getBoolean("incapacitation"));
                instance.setTicksUntilDeath(tag.getInt("incapTimer"));
                instance.setDownsUntilDeath(tag.getInt("incapCounter"));
                instance.setJumpCount(tag.getInt("jumpCounter"));
            }
        }, new Callable<PlayerCapabilityManager>(){
        @Override
        public PlayerCapabilityManager call() throws Exception {
            return new PlayerCapabilityManager();
        }

    });
    }
}
