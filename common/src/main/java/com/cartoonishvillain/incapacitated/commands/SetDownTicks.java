package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;


public class SetDownTicks {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incapacitated"))
                .then((LiteralArgumentBuilder) Commands.literal("setDownTicks").requires((p_137736_) -> {
                            return p_137736_.hasPermission(2);
                        })
                        .then(((RequiredArgumentBuilder)Commands.argument("downTicks", IntegerArgumentType.integer(1)).executes((p_258228_) -> {
                            return setDownCount(p_258228_.getSource(), IntegerArgumentType.getInteger(p_258228_, "downTicks"));
                        })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
                            return setDownCount(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"), IntegerArgumentType.getInteger(p_258229_, "downTicks"));
                        })))));

        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("incap"))
                .then((LiteralArgumentBuilder) Commands.literal("setDownTicks").requires((p_137736_) -> {
                            return p_137736_.hasPermission(2);
                        })
                        .then(((RequiredArgumentBuilder)Commands.argument("downTicks", IntegerArgumentType.integer(1)).executes((p_258228_) -> {
                            return setDownCount(p_258228_.getSource(), IntegerArgumentType.getInteger(p_258228_, "downTicks"));
                        })).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes((p_258229_) -> {
                            return setDownCount(p_258229_.getSource(), GameProfileArgument.getGameProfiles(p_258229_, "player"), IntegerArgumentType.getInteger(p_258229_, "downTicks"));
                        })))));
    }

    private static int setDownCount(CommandSourceStack sourceStack, Collection<GameProfile> profiles, int downTicks) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                AbstractedIncapacitation.setDownTicks(serverPlayer, downTicks);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.setticks", serverPlayer.getDisplayName(), downTicks), true);
            }
        }
        return 0;
    }

    private static int setDownCount(CommandSourceStack sourceStack, int downTicks) {
        ServerPlayer serverPlayer = sourceStack.getPlayer();
        if (serverPlayer != null) {
            AbstractedIncapacitation.setDownTicks(serverPlayer, (short) downTicks);
            sourceStack.sendSuccess(() -> Component.translatable("command.return.setticks", serverPlayer.getDisplayName(), downTicks), true);
        } else {
            sourceStack.sendFailure(Component.literal("Player not found, if you're not a player in the game, define a player!"));
        }
        return 0;
    }

}
