package com.cartoonishvillain.incapacitated.component;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import static org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy.LOSSLESS_ONLY;
import static org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy.copy;

public class ComponentStarter implements EntityComponentInitializer {
    public static final ComponentKey<IncapacitatedComponent> INCAPACITATEDCOMPONENTINSTANCE =
            ComponentRegistryV3.INSTANCE.getOrCreate(ResourceLocation.parse("incapacitation:incapdata"), IncapacitatedComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(INCAPACITATEDCOMPONENTINSTANCE, IncapacitatedComponent::new, LOSSLESS_ONLY);
    }
}

