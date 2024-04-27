package com.cartoonishvillain.incapacitated.commands;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("config").requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("reload").executes(context ->
                    reloadConfig(context.getSource())
                ))));

        dispatcher.register(Commands.literal("incap").then(Commands.literal("config").requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("reload").executes(context ->
                        reloadConfig(context.getSource())
                ))));
    }

    private static int reloadConfig(CommandSourceStack sourceStack) {
        Incapacitated.loadConfig();
        return 0;
    }
}
