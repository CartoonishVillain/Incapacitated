package com.cartoonishvillain.incapacitated.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class IncapacitatedClientConfig {
    private static final ModConfigSpec.Builder CLIENTBUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue GRAYSCREEN = CLIENTBUILDER
            .comment("Does the player screen desaturate on their last down?")
            .define("lastDownDesaturate", true);

    public static final ModConfigSpec CLIENTSPEC = CLIENTBUILDER.build();

}
