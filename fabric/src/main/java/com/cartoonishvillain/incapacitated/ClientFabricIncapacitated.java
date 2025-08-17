package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.networking.GiveUpPacket;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import static com.cartoonishvillain.incapacitated.FabricKeybind.giveUpKeybind;

public class ClientFabricIncapacitated implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        giveUpKeybind = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.incapacitated.giveup",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_DELETE,
                        "key.incapacitated.category"
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (giveUpKeybind.consumeClick()) {
                if (Services.PLATFORM.getPlayerData(Minecraft.getInstance().player).isIncapacitated()) {
                    ClientPlayNetworking.send(new GiveUpPacket(Minecraft.getInstance().player.getId(), Minecraft.getInstance().player.getGameProfile()));
                }
            }
        });
    }
}
