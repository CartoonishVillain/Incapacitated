package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class KillPlayer {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("assets/incapacitated").then(Commands.literal("die").requires(cs -> cs.hasPermission(0)).executes(context ->
                killPlayerIfDown(context.getSource())
        )));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("die").requires(cs -> cs.hasPermission(0)).executes( context ->
                killPlayerIfDown(context.getSource())
        )));
    }

    private static int killPlayerIfDown(CommandSourceStack sourceStack) {
        ServerPlayer player = sourceStack.getPlayer();
        if (player != null && !FabricIncapacitated.configData.isDANGERDisableGiveUp()) {
            Services.killPlayerIfIncappedCommand(player);
        } else if (player != null && FabricIncapacitated.configData.isDANGERDisableGiveUp()) {
            sourceStack.sendFailure(Component.translatable("command.return.die.failed"));
        }
        return 0;
    }

}
