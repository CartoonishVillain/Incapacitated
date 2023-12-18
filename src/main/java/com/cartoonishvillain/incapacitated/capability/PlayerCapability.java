package com.cartoonishvillain.incapacitated.capability;

import com.cartoonishvillain.incapacitated.Incapacitated;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PlayerCapability {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Incapacitated.MODID);
    public static final Supplier<AttachmentType<IncapacitatedPlayerData>> INCAP_DATA = ATTACHMENT_TYPE.register(
            "incapacitated_data", () -> AttachmentType.builder(IncapacitatedPlayerData::new).serialize(new IncapacitatedSerializer()).build()
    );
    public static void loadDataAttachment(IEventBus modBus) {
        ATTACHMENT_TYPE.register(modBus);
    }

}
