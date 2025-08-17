package com.cartoonishvillain.incapacitated;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class NFIncapKeybind {
    public static KeyMapping GiveUpKeybind = new KeyMapping(
            "key.incapacitated.giveup",
            KeyConflictContext.UNIVERSAL,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DELETE,
            "key.incapacitated.category"
    );
}
