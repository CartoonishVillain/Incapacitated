package com.cartoonishvillain.incapacitated.config;

import java.io.Serializable;

public class IncapConfigData implements Serializable {
    String info;
    int merciful;
    boolean hunter;
    boolean slow;
    boolean weakened;
    boolean regenerating;
    boolean unlimitedDowns;
    boolean downLogging;
    boolean reviveMessage;
    String foodReviveList;
    String foodHealList;
    int downTicks;
    int reviveTicks;
    int downCounter;
    boolean glowingWhileDowned;
    boolean someInstantKills;
    boolean globalIncapMessage;
    boolean globalReviveMessage;
    boolean useSecondsForRevive;

    public IncapConfigData(int merciful, boolean hunter, boolean slow, boolean weakened, boolean regenerating, boolean unlimitedDowns, boolean downLogging, boolean reviveMessage, String foodReviveList, String foodHealList, int downTicks, int reviveTicks, int downCounter, boolean glowingWhileDowned, boolean someInstantKills, boolean globalIncapMessage, boolean globalReviveMessage, boolean useSecondsForRevive) {
        this.info = "For documentation on what each item does, see the readme file on github: https://github.com/CartoonishVillain/Incapacitated";
        this.merciful = merciful;
        this.hunter = hunter;
        this.slow = slow;
        this.weakened = weakened;
        this.regenerating = regenerating;
        this.unlimitedDowns = unlimitedDowns;
        this.downLogging = downLogging;
        this.reviveMessage = reviveMessage;
        this.foodReviveList = foodReviveList;
        this.foodHealList = foodHealList;
        this.downTicks = downTicks;
        this.reviveTicks = reviveTicks;
        this.downCounter = downCounter;
        this.glowingWhileDowned = glowingWhileDowned;
        this.someInstantKills = someInstantKills;
        this.globalIncapMessage = globalIncapMessage;
        this.globalReviveMessage = globalReviveMessage;
        this.useSecondsForRevive = useSecondsForRevive;
    }


    public static IncapConfigData buildDefaultConfig() {
        return new IncapConfigData(
                0, //merciful
                false, //hunter
                false, //slow
                false, //weakened
                false, //regenerating
                false, //unlimitedDowns
                false, //downLogging
                true, //reviveMessage
                "minecraft:enchanted_golden_apple", //food revive list
                "minecraft:golden_apple", //food heal list
                2000, //downTicks
                150, //reviveTicks
                3, //downCounter
                true, //glowingWhileDowned
                true, //someInstantKills
                true, //globalIncapMessage
                true, //globalReviveMessage
                false //useSecondsForRevive
        );
    }

    public int getMerciful() {
        return merciful;
    }

    public boolean isHunter() {
        return hunter;
    }

    public boolean isSlow() {
        return slow;
    }

    public boolean isWeakened() {
        return weakened;
    }

    public boolean isRegenerating() {
        return regenerating;
    }

    public boolean isUnlimitedDowns() {
        return unlimitedDowns;
    }

    public boolean isDownLogging() {
        return downLogging;
    }

    public boolean isReviveMessage() {
        return reviveMessage;
    }

    public String getFoodReviveList() {
        return foodReviveList;
    }

    public String getFoodHealList() {
        return foodHealList;
    }

    public int getDownTicks() {
        return downTicks;
    }

    public int getReviveTicks() {
        return reviveTicks;
    }

    public int getDownCounter() {
        return downCounter;
    }

    public boolean isGlowingWhileDowned() {
        return glowingWhileDowned;
    }

    public boolean isSomeInstantKills() {
        return someInstantKills;
    }

    public boolean isGlobalIncapMessage() {
        return globalIncapMessage;
    }

    public boolean isGlobalReviveMessage() {
        return globalReviveMessage;
    }

    public boolean isUseSecondsForRevive() {
        return useSecondsForRevive;
    }
}
