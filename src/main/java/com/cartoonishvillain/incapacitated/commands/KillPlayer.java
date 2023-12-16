package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.capability.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;


public class KillPlayer {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("die").requires(cs -> cs.hasPermission(0)).executes( context ->
                killPlayerIfDown(context.getSource())
        )));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("die").requires(cs -> cs.hasPermission(0)).executes( context ->
                killPlayerIfDown(context.getSource())
        )));
    }

    private static int killPlayerIfDown(CommandSourceStack sourceStack) {
        Player player = sourceStack.getPlayer();
        if (player != null) {
            IncapacitatedPlayerData playerData = sourceStack.getPlayer().getData(INCAP_DATA);
            if (playerData.isIncapacitated()) {
                player.hurt(playerData.getDamageSource(player.level()), player.getMaxHealth() * 10);
                player.setForcedPose(null);
                playerData.setReviveCounter(IncapacitatedCommonConfig.DOWNCOUNT.get());
                playerData.setIncapacitated(false);
                player.removeEffect(MobEffects.GLOWING);
                IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false, (short) playerData.getDownsUntilDeath()), player);
            }
        }
        return 0;
    }

}
