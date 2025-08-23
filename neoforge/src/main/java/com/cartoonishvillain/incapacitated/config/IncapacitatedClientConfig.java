package com.cartoonishvillain.incapacitated.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class IncapacitatedClientConfig {
    private static final ModConfigSpec.Builder CLIENTBUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue GRAYSCREEN = CLIENTBUILDER
            .comment("Does the player screen desaturate on their last down?")
            .define("lastDownDesaturate", true);
    public static final ModConfigSpec.BooleanValue DOWNCOUNTERENABLED = CLIENTBUILDER
            .comment("Should the down counter render?")
            .define("renderDownCounter", true);
    public static final ModConfigSpec.BooleanValue DOWNCOUNTERCOLOURFUL = CLIENTBUILDER
            .comment("Should the down counter be colored based on the remaining downs?")
            .define("downCounterColorful", true);
    public static final ModConfigSpec.IntValue DOWNCOUNTERXMODIFIER = CLIENTBUILDER
            .comment("Modified X position value for the display of the down counter")
            .defineInRange("downCounterModX", 0, -10000, 10000);
    public static final ModConfigSpec.IntValue DOWNCOUNTERYMODIFIER = CLIENTBUILDER
            .comment("Modified Y position value for the display of the down counter")
            .defineInRange("downCounterModY", 0, -10000, 10000);

    public static final ModConfigSpec CLIENTSPEC = CLIENTBUILDER.build();

}
