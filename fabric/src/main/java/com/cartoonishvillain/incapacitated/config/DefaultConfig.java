package com.cartoonishvillain.incapacitated.config;

public class DefaultConfig {
    public static String provider( String name ) {
        return """
                # Incapacitated config.
                # Does the player screen desaturate on their last down?
                lastDownDesaturate=true
                
                # Are players immune to damage while downed?
                # 0: No, players are not immune to damage while downed.
                # 1: Yes, but part of the damage received is removed from the down timer
                # 2: Yes, with no caveats.
                # Range: 0 ~ 2
                merciful=0
                
                # Can players revive themselves with a (non-player) kill?
                hunter=false
                
                # Are Incapacitated players slowed down dramatically?
                slow=false
                
                # Are Incapacitated players weakened dramatically?
                weakened=false
                
                # Does being restful award players with another down?
                regenerating=false
                
                # Does the player have unlimited downs?
                unlimitedDowns=false
                
                # Does the player die when they log out while downed
                downLogging=false
                
                # Does the player receive a chat message when revived with information
                reviveMessage=true
                
                # A list of comma separated item IDs for foods player can eat to revive themselves. IE: minecraft:enchanted_golden_apple,minecraft:apple
                foodReviveList=minecraft:enchanted_golden_apple
                
                # A list of comma separated item IDs for foods player can eat to reset their down counters. IE: minecraft:golden_apple,minecraft:golden_carrot
                foodHealList=minecraft:golden_apple
               
                # How many ticks a player can be downed without dying.
                downTicks=2000
                
                # How long it takes to revive a downed player manually
                reviveTicks=150
                
                # How many times a player can go down without a healing or revive item, without instantly dying the next time they are supposed to go down.
                downCounter=3
                
                # Do players glow while downed to be easier to find?
                glowingWhileDowned=true
                
                # Do some damage types like Lava down players, or instantly kill?
                someInstantKills = true
                
                # Are incapacitation messages global?
                globalIncapMessage=true
                
                # Are revive messages global?
                globalReviveMessage=true
                
                # When reviving, do we display seconds until revive instead of the bar?
                useSecondsForRevive=false
                """;
    }
}
