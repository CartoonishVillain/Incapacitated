package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;


public class SetDownTicks {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("setDownTicks").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("downTicks", IntegerArgumentType.integer(1)).executes(context ->
                        setDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                        IntegerArgumentType.getInteger(context, "downTicks")))
        ))));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("setDownTicks").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("downTicks", IntegerArgumentType.integer(1)).executes(context ->
                        setDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                                IntegerArgumentType.getInteger(context, "downTicks")))
                ))));
    }

    private static int setDownCount(CommandSourceStack sourceStack, Collection<GameProfile> profiles, int downTicks) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                AbstractedIncapacitation.setDownTicks(serverPlayer, downTicks);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.setticks", serverPlayer.getName(), downTicks), true);
            }
        }
        return 0;
    }

}
