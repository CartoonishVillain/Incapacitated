package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;


public class GetDownCount {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("getDownedCount").requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(context ->
                        getDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player")))
        )));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("getDownedCount").requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(context ->
                        getDownCount(context.getSource(), GameProfileArgument.getGameProfiles(context, "player")))
                )));
    }

    private static int getDownCount(CommandSourceStack sourceStack, Collection<GameProfile> profiles) {
        for(GameProfile gameProfile : profiles) {
            ServerPlayer serverPlayer = sourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer != null) {
                short amount = AbstractedIncapacitation.getDownCount(serverPlayer);
                sourceStack.sendSuccess(() -> Component.translatable("command.return.getdowns", serverPlayer.getName(), amount), true);
            }
        }
        return 0;
    }

}
