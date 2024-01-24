package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;


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
        ServerPlayer player = sourceStack.getPlayer();
        if (player != null) {
            Services.PLATFORM.killPlayerIfIncappedCommand(player);
        }
        return 0;
    }

}
