package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;


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
        Player player = null;
        try {
            player = sourceStack.getPlayerOrException();
            Player finalPlayer = player;
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                if (h.getIsIncapacitated()) {
                    finalPlayer.hurt(h.getSourceOfDeath(), finalPlayer.getMaxHealth() * 10);
                    finalPlayer.setForcedPose(null);
                    h.setReviveCount(Incapacitated.config.DOWNCOUNT.get());
                    h.resetGiveUpJumps();
                    h.setIsIncapacitated(false);
                    finalPlayer.removeEffect(MobEffects.GLOWING);
                    IncapacitationMessenger.sendTo(new IncapPacket(finalPlayer.getId(), false, (short) h.getDownsUntilDeath()), finalPlayer);
                }
            });
        } catch (CommandSyntaxException e) {

        }
        return 0;
    }

}
