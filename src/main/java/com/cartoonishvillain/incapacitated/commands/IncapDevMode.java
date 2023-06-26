package com.cartoonishvillain.incapacitated.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static com.cartoonishvillain.incapacitated.Incapacitated.devMode;

public class IncapDevMode {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("incapacitated").then(Commands.literal("devMode").requires(cs -> cs.hasPermission(0)).executes(context -> {
                            return setIncapped(context.getSource());
                        })
                ));
    }

    private static int setIncapped(CommandSourceStack sourceStack) {
        if(devMode) {
            devMode = false;
            sourceStack.sendSuccess(() -> Component.literal("Dev mode disabled."), false);
        } else {
            devMode = true;
            sourceStack.sendSuccess(() -> Component.literal("Dev mode enabled"), true);
        }
        return 0;
    }
}
