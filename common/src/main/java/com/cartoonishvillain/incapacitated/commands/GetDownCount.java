package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;


public class GetDownCount {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incapacitated"))
                .then(((LiteralArgumentBuilder) Commands.literal("getDownedCount").requires((p_137736_) -> {
                            return p_137736_.hasPermission(0);
                        }).executes((p_258228_) -> {
                            return getDownCount(p_258228_.getSource());
                        })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
                            return getDownCount(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"));
                        }))));

        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incap"))
                .then(((LiteralArgumentBuilder) Commands.literal("getDownedCount").requires((p_137736_) -> {
                    return p_137736_.hasPermission(0);
                }).executes((p_258228_) -> {
                    return getDownCount(p_258228_.getSource());
                })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
                    return getDownCount(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"));
                }))));
    }

    private static int getDownCount(CommandSourceStack sourceStack, Collection<GameProfile> profiles) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                short amount = AbstractedIncapacitation.getDownCount(serverPlayer);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.getdowns", serverPlayer.getDisplayName(), amount), true);
            }
        }
        return 0;
    }

    private static int getDownCount(CommandSourceStack sourceStack) {
        ServerPlayer serverPlayer = sourceStack.getPlayer();
        if (serverPlayer != null) {
            short amount = AbstractedIncapacitation.getDownCount(serverPlayer);
            sourceStack.sendSuccess(() -> Component.translatable("command.return.getdowns", serverPlayer.getDisplayName(), amount), true);
        } else {
            sourceStack.sendFailure(Component.literal("Player not found, if you're not a player in the game, define a player!"));
        }
        return 0;
    }

}
