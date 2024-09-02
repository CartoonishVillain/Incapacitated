package com.cartoonishvillain.incapacitated.config;

import java.io.Serializable;
import java.util.ArrayList;

public class IncapConfigData implements Serializable {
    String info;
    int merciful;
    boolean canBreakOrInteractWithBlocks;
    boolean canJumpWhileDown;
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
    String instantKills;
    boolean globalIncapMessage;
    boolean globalReviveMessage;
    boolean useSecondsForRevive;
    boolean healPercentageOfMaxHealth;
    float reviveHealth;
    int reviveHunger;
    float reviveSaturation;
    boolean shouldDownTimeReset;
    ArrayList<IncapEffectData> incapEffectData;
    boolean DANGERDisableGiveUp;

    public IncapConfigData(int merciful, boolean hunter, boolean canBreakOrInteractWithBlocks, boolean canJumpWhileDown, boolean slow, boolean weakened, boolean regenerating, boolean unlimitedDowns, boolean downLogging, boolean reviveMessage, String foodReviveList, String foodHealList, int downTicks, int reviveTicks, int downCounter, boolean glowingWhileDowned, boolean someInstantKills, String instantKills, boolean globalIncapMessage, boolean globalReviveMessage, boolean useSecondsForRevive,
    boolean healPercentageOfMaxHealth, float reviveHealth, int reviveHunger, float reviveSaturation, boolean shouldDownTimeReset, ArrayList<IncapEffectData> incapEffectData, boolean DANGERDisableGiveUp) {
        this.info = "For documentation on what each item does, see the readme file on github: https://github.com/CartoonishVillain/Incapacitated";
        this.merciful = merciful;
        this.hunter = hunter;
        this.canBreakOrInteractWithBlocks = canBreakOrInteractWithBlocks;
        this.canJumpWhileDown = canJumpWhileDown;
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
        this.instantKills = instantKills;
        this.globalIncapMessage = globalIncapMessage;
        this.globalReviveMessage = globalReviveMessage;
        this.useSecondsForRevive = useSecondsForRevive;
        this.healPercentageOfMaxHealth = healPercentageOfMaxHealth;
        this.reviveHealth = reviveHealth;
        this.reviveHunger = reviveHunger;
        this.reviveSaturation = reviveSaturation;
        this.shouldDownTimeReset = shouldDownTimeReset;
        this.incapEffectData = incapEffectData;
        this.DANGERDisableGiveUp = DANGERDisableGiveUp;
    }


    public static IncapConfigData buildDefaultConfig() {
        return new IncapConfigData(
                0, //merciful
                false, //hunter
                false, //canBreakOrInteractWithBlocks
                false, //canJumpWhileDown
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
                "wither,lava,outOfWorld",
                true, //globalIncapMessage
                true, //globalReviveMessage
                false, //useSecondsForRevive
                true, //healPercentageOfMaxHealth
                0.33f, //reviveHealth
                -1, //reviveHunger
                -1f, //reviveSaturation
                false, //shouldDownTimeReset
                new ArrayList<>(), //incapEffectData
                false //DANGERDisableGiveUp
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

    public boolean isCanBreakOrInteractWithBlocks() {
        return canBreakOrInteractWithBlocks;
    }

    public boolean isHealPercentageOfMaxHealth() {
        return healPercentageOfMaxHealth;
    }

    public float getReviveHealth() {
        return reviveHealth;
    }

    public int getReviveHunger() {
        return reviveHunger;
    }

    public float getReviveSaturation() {
        return reviveSaturation;
    }

    public String getInstantKills() {
        return instantKills;
    }

    public ArrayList<IncapEffectData> getIncapEffectData() {
        return incapEffectData;
    }

    public boolean isDANGERDisableGiveUp() {
        return DANGERDisableGiveUp;
    }

    public boolean isCanJumpWhileDown() {
        return canJumpWhileDown;
    }

    public boolean isShouldDownTimeReset() {
        return shouldDownTimeReset;
    }
}
