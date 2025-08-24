package com.cartoonishvillain.incapacitated.gui;

import com.cartoonishvillain.incapacitated.Constants;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class IncapacitatedOverlay {

    public static void renderOverlay(GuiGraphics guiGraphics) {
        ResourceLocation downHolder = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/down_icon.png");
        Minecraft minecraft = Minecraft.getInstance();
        int downCount = Services.PLATFORM.getPlayerData(minecraft.player).getDownsUntilDeath();

        int xPos = (guiGraphics.guiWidth() / 2) - 100;
        int yPos = guiGraphics.guiHeight() - 38;

        int color = 16777215; //White for all case not listed below

        if (Services.PLATFORM.shouldGUIDownCounterBeColorful()) {
            switch (downCount) {
                default -> {}
                case 0 -> color = 12845056;
                case 1 -> color = 16091183;
                case 2 -> color = 16118319;
                case 3 -> color = 5961007;
                case 4 -> color = 2848022;
                case 5 -> color = 3013116;
                case 6 -> color = 2965244;
                case 7 -> color = 11546108;
            }
        }

        guiGraphics.blit(downHolder, xPos-16+Services.PLATFORM.GUIDownCounterXModifier(), yPos-2+Services.PLATFORM.GUIDownCounterYModifier(), 0.0F, 0.0f, 10, 10, 10, 10 );
        guiGraphics.drawCenteredString(minecraft.font, Component.literal(String.valueOf(downCount)), xPos-1+Services.PLATFORM.GUIDownCounterXModifier(), yPos-1+Services.PLATFORM.GUIDownCounterYModifier(), 0);
        guiGraphics.drawCenteredString(minecraft.font, Component.literal(String.valueOf(downCount)), xPos+1+Services.PLATFORM.GUIDownCounterXModifier(), yPos-1+Services.PLATFORM.GUIDownCounterYModifier(), 0);
        guiGraphics.drawCenteredString(minecraft.font, Component.literal(String.valueOf(downCount)), xPos+Services.PLATFORM.GUIDownCounterXModifier(), yPos-2+Services.PLATFORM.GUIDownCounterYModifier(), 0);
        guiGraphics.drawCenteredString(minecraft.font, Component.literal(String.valueOf(downCount)), xPos+Services.PLATFORM.GUIDownCounterXModifier(), yPos+Services.PLATFORM.GUIDownCounterYModifier(), 0);
        guiGraphics.drawCenteredString(minecraft.font, Component.literal(String.valueOf(downCount)), xPos+Services.PLATFORM.GUIDownCounterXModifier(), yPos-1+Services.PLATFORM.GUIDownCounterYModifier(), color);
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (Services.PLATFORM.shouldShowGUIDownCounter() && !minecraft.options.hideGui && (minecraft.gameMode.getPlayerMode() == GameType.SURVIVAL || minecraft.gameMode.getPlayerMode() == GameType.ADVENTURE)) {
            renderOverlay(guiGraphics);
        }
    }
}
