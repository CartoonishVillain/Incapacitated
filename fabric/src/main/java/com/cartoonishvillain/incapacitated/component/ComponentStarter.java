package com.cartoonishvillain.incapacitated.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;

public class ComponentStarter implements EntityComponentInitializer {
    public static final ComponentKey<IncapacitatedComponent> INCAPACITATEDCOMPONENTINSTANCE =
            ComponentRegistryV3.INSTANCE.getOrCreate(new ResourceLocation("incapacitation:incapdata"), IncapacitatedComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(INCAPACITATEDCOMPONENTINSTANCE, IncapacitatedComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}
