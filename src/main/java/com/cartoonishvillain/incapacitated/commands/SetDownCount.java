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


public class SetDownCount {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("setDownCount").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("downCount", IntegerArgumentType.integer(0, 127)).executes(context ->
                        setDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                        IntegerArgumentType.getInteger(context, "downCount")))
        ))));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("setDownCount").requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("downCount", IntegerArgumentType.integer(0, 127)).executes(context ->
                        setDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"),
                                IntegerArgumentType.getInteger(context, "downCount")))
                ))));
    }

    private static int setDownCount(CommandSourceStack sourceStack, Collection<GameProfile> profiles, int downCount) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                AbstractedIncapacitation.setDownCount(serverPlayer, (short) downCount);
                sourceStack.sendSuccess(Component.translatable("command.return.setdowns", serverPlayer.getName(), downCount), true);
            }
        }
        return 0;
    }

}
