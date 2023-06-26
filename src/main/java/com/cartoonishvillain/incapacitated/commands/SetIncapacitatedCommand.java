package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.downOrKill;
import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.revive;

public class SetIncapacitatedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("setDowned").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("incapacitated", BoolArgumentType.bool()).executes(context -> {
                    return setIncapped(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                            BoolArgumentType.getBool(context, "incapacitated"));
                })
        ))));


        dispatcher.register(Commands.literal("incap").then(Commands.literal("setDowned").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("incapacitated", BoolArgumentType.bool()).executes(context -> {
                            return setIncapped(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                                    BoolArgumentType.getBool(context, "incapacitated"));
                        })
                ))));
    }

    private static int setIncapped(CommandSourceStack sourceStack, Collection<GameProfile> profiles, boolean isIncapped) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                if (isIncapped) {
                    downOrKill(serverPlayer);
                    sourceStack.sendSuccess(() -> Component.translatable("command.return.incapped", serverPlayer.getName()), true);
                } else {
                    revive(serverPlayer);
                    sourceStack.sendSuccess(() -> Component.translatable("command.return.revived", serverPlayer.getName()), true);
                }
            }
        }
        return 0;
    }

}
