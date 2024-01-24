package com.cartoonishvillain.incapacitated.config;

import com.cartoonishvillain.incapacitated.Constants;
import com.cartoonishvillain.incapacitated.Incapacitated;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IncapacitatedCommonConfig
{
    private static final ModConfigSpec.Builder COMMONBUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue GLOWING = COMMONBUILDER
            .comment("Do players glow while downed to be easier to find?")
            .define("glowingWhileDowned", true);

    public static final ModConfigSpec.BooleanValue USESECONDS = COMMONBUILDER
            .comment("When reviving, do we display seconds until revive instead of the bar?")
            .define("useSecondsForRevive", false);

    public static final ModConfigSpec.BooleanValue SOMEINSTANTKILLS = COMMONBUILDER
            .comment("Do some damage types like Lava down players, or instantly kill?")
            .define("someInstantKills", true);

    public static final ModConfigSpec.BooleanValue GLOBALINCAPMESSAGES = COMMONBUILDER
            .comment("Are incapacitation messages global?")
            .define("globalIncapMessage", true);

    public static final ModConfigSpec.BooleanValue GLOBALREVIVEMESSAGES = COMMONBUILDER
            .comment("Are revive messages global?")
            .define("globalReviveMessage", true);

    public static final ModConfigSpec.BooleanValue REVIVE_MESSAGE = COMMONBUILDER
            .comment("Does the player receive a chat message when revived with information?")
            .define("reviveMessage", true);

    public static final ModConfigSpec.BooleanValue DOWNLOGGING = COMMONBUILDER
            .comment("Does the player die when they log out while downed?")
            .define("downLogging", false);

    public static final ModConfigSpec.BooleanValue UNLIMITEDDOWNS = COMMONBUILDER
            .comment("Does the player have unlimited downs?")
            .define("unlimitedDowns", false);

    public static final ModConfigSpec.BooleanValue REGENERATING = COMMONBUILDER
            .comment("Does being restful award players with another down?")
            .define("regenerating", false);

    public static final ModConfigSpec.BooleanValue WEAKENED = COMMONBUILDER
            .comment("Are Incapacitated players weakened dramatically?")
            .define("weakened", false);

    public static final ModConfigSpec.BooleanValue SLOW = COMMONBUILDER
            .comment("Are Incapacitated players slowed down dramatically?")
            .define("slow", false);

    public static final ModConfigSpec.BooleanValue HUNTER = COMMONBUILDER
            .comment("Can players revive themselves with a (non-player) kill?")
            .define("hunter", false);

    public static final ModConfigSpec.IntValue MERCIFUL = COMMONBUILDER
            .comment("Are players immune to damage while downed?")
            .comment("0: No, players are not immune to damage while downed.")
            .comment("1: Yes, but part of the damage received is removed from the down timer")
            .comment("2: Yes, with no caveats.")
            .defineInRange("merciful", 0, 0, 2);

    public static final ModConfigSpec.IntValue DOWNTICKS = COMMONBUILDER
            .comment("How many ticks a player can be downed without dying.")
            .defineInRange("downTicks", 2000, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue REVIVETICKS = COMMONBUILDER
            .comment("How long it takes to revive a downed player manually.")
            .defineInRange("reviveTicks", 150, 5, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue DOWNCOUNT = COMMONBUILDER
            .comment("How many times a player can go down without a healing or revive item, without instantly dying the next time they are supposed to go down.")
            .defineInRange("downCounter", 3, 1, Integer.MAX_VALUE);

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> REVIVEFOODS = COMMONBUILDER
            .comment("A list of comma separated item IDs for foods player can eat to revive themselves.")
            .defineListAllowEmpty("foodReviveList", List.of("minecraft:enchanted_golden_apple"), IncapacitatedCommonConfig::validateItemName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> HEALINGFOODS = COMMONBUILDER
        .comment("A list of comma separated item IDs for foods player can eat to reset their down counters.")
        .defineListAllowEmpty("foodHealList", List.of("minecraft:golden_apple"), IncapacitatedCommonConfig::validateItemName);

    public static final ModConfigSpec SPEC = COMMONBUILDER.build();

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
    }
}
