package com.cartoonishvillain.incapacitated.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class CommonConfig {
        public static final String CCATEGORY_CONFIGS = "Options";
        public ConfigHelper.ConfigValueListener<String> REVIVEFOODS;
        public ConfigHelper.ConfigValueListener<String> HEALINGFOODS;
        public ConfigHelper.ConfigValueListener<Integer> DOWNTICKS;
        public ConfigHelper.ConfigValueListener<Integer> REVIVETICKS;
        public ConfigHelper.ConfigValueListener<Integer> DOWNCOUNT;
        public ConfigHelper.ConfigValueListener<Boolean> GLOWING;
        public ConfigHelper.ConfigValueListener<Boolean> INVINCIBLEDOWN;
        public ConfigHelper.ConfigValueListener<Boolean> SOMEINSTANTKILLS;
        public ConfigHelper.ConfigValueListener<Boolean> GLOBALINCAPMESSAGES;

        public CommonConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber){
            builder.comment("Modify Components that can be handled serverside").push(CCATEGORY_CONFIGS);
            this.REVIVEFOODS = subscriber.subscribe(builder.comment("A list of comma separated item IDs for foods player can eat to revive themselves. IE: minecraft:enchanted_golden_apple,minecraft:apple").define("foodReviveList", "minecraft:enchanted_golden_apple"));
            this.HEALINGFOODS = subscriber.subscribe(builder.comment("A list of comma separated item IDs for foods player can eat to reset their down counters. IE: minecraft:golden_apple,minecraft:golden_carrot").define("foodHealList", "minecraft:golden_apple"));
            this.DOWNTICKS = subscriber.subscribe(builder.comment("How many ticks a player can be downed without dying.").defineInRange("downTicks", 2000, 20, Integer.MAX_VALUE));
            this.REVIVETICKS = subscriber.subscribe(builder.comment("How long it takes to revive a downed player manually").defineInRange("reviveTicks", 150, 5, Integer.MAX_VALUE));
            this.DOWNCOUNT = subscriber.subscribe(builder.comment("How many times a player can go down without a healing or revive item, without instantly dying the next time they are supposed to go down.").defineInRange("downCounter", 3, 1, Integer.MAX_VALUE));
            this.GLOWING = subscriber.subscribe(builder.comment("Do players glow while downed to be easier to find?").define("glowingWhileDowned", true));
            this.INVINCIBLEDOWN = subscriber.subscribe(builder.comment("Are downed players invincible?").define("invincibleWhileDowned", false));
            this.SOMEINSTANTKILLS = subscriber.subscribe(builder.comment("Do some damage types like Lava down players, or instantly kill?").define("someInstantKills", true));
            this.GLOBALINCAPMESSAGES = subscriber.subscribe(builder.comment("Are incapacitation messages global?").define("globalIncapMessage", true));
            builder.pop();
        }

        public static void loadConfig(ForgeConfigSpec spec, Path path){
            final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            configData.load();;
            spec.setConfig(configData);
        }
}

