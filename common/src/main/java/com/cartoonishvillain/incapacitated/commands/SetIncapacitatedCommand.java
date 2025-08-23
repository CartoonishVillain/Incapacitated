package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class SetIncapacitatedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incapacitated"))
                        .then((LiteralArgumentBuilder) Commands.literal("setDowned").requires((p_137736_) -> {
                                    return p_137736_.hasPermission(2);
                                })
                .then(((RequiredArgumentBuilder)Commands.argument("incapacitated", BoolArgumentType.bool()).executes((p_258228_) -> {
            return setIncapped(p_258228_.getSource(), BoolArgumentType.getBool(p_258228_, "incapacitated"));
        })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
            return setIncapped(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"), BoolArgumentType.getBool(p_258229_, "incapacitated"));
        })))));

        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incap"))
                .then((LiteralArgumentBuilder) Commands.literal("setDowned").requires((p_137736_) -> {
                            return p_137736_.hasPermission(2);
                        })
                        .then(((RequiredArgumentBuilder)Commands.argument("incapacitated", BoolArgumentType.bool()).executes((p_258228_) -> {
                            return setIncapped(p_258228_.getSource(), BoolArgumentType.getBool(p_258228_, "incapacitated"));
                        })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
                            return setIncapped(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"), BoolArgumentType.getBool(p_258229_, "incapacitated"));
                        })))));
    }

    private static int setIncapped(CommandSourceStack sourceStack, Collection<GameProfile> profiles, boolean isIncapped) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                if (isIncapped) {
                    AbstractedIncapacitation.downOrKill(serverPlayer);
                    sourceStack.sendSuccess(() -> Component.translatable("command.return.incapped", serverPlayer.getDisplayName()), true);
                } else {
                    AbstractedIncapacitation.revive(serverPlayer, null);
                    sourceStack.sendSuccess(() -> Component.translatable("command.return.revived", serverPlayer.getDisplayName()), true);
                }
            }
        }
        return 0;
    }

    private static int setIncapped(CommandSourceStack sourceStack, boolean isIncapped) {
        ServerPlayer serverPlayer = sourceStack.getPlayer();
        if (serverPlayer != null) {
            if (isIncapped) {
                AbstractedIncapacitation.downOrKill(serverPlayer);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.incapped", serverPlayer.getDisplayName()), true);
            } else {
                AbstractedIncapacitation.revive(serverPlayer, null);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.revived", serverPlayer.getDisplayName()), true);
            }
        } else {
            sourceStack.sendFailure(Component.literal("Player not found, if you're not a player in the game, define a player!"));
        }
        return 0;
    }

}
