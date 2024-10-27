package com.cartoonishvillain.incapacitated.config;

import com.cartoonishvillain.incapacitated.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class IncapConfigData implements Serializable {
    String info;
    Integer merciful;
    Boolean canBreakOrInteractWithBlocks;
    Boolean canJumpWhileDown;
    Boolean hunter;
    Boolean slow;
    Boolean weakened;
    Boolean regenerating;
    Boolean unlimitedDowns;
    Boolean downLogging;
    Boolean reviveMessage;
    String foodReviveList;
    String foodHealList;
    Integer downTicks;
    Integer reviveTicks;
    Integer downCounter;
    Boolean glowingWhileDowned;
    Boolean someInstantKills;
    String instantKills;
    Boolean globalIncapMessage;
    Boolean globalReviveMessage;
    Boolean useSecondsForRevive;
    Boolean healPercentageOfMaxHealth;
    Float reviveHealth;
    Integer reviveHunger;
    Float reviveSaturation;
    Boolean shouldDownTimeReset;
    ArrayList<IncapEffectData> incapEffectData;
    Boolean DANGERDisableGiveUp;
    Boolean DANGERDisableIncapPlayerDamage;
    
    public static IncapConfigData defaultData = buildDefaultConfig();

    public IncapConfigData(Integer merciful, Boolean hunter, Boolean canBreakOrInteractWithBlocks, Boolean canJumpWhileDown, Boolean slow, Boolean weakened, Boolean regenerating, Boolean unlimitedDowns, Boolean downLogging, Boolean reviveMessage, String foodReviveList, String foodHealList, Integer downTicks, Integer reviveTicks, Integer downCounter, Boolean glowingWhileDowned, Boolean someInstantKills, String instantKills, Boolean globalIncapMessage, Boolean globalReviveMessage, Boolean useSecondsForRevive,
    Boolean healPercentageOfMaxHealth, float reviveHealth, Integer reviveHunger, float reviveSaturation, Boolean shouldDownTimeReset, ArrayList<IncapEffectData> incapEffectData, Boolean DANGERDisableGiveUp, Boolean DANGERDisableIncapPlayerDamage) {
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
        this.DANGERDisableIncapPlayerDamage = DANGERDisableIncapPlayerDamage;
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
                false, //DANGERDisableGiveUp
                false //DANGERDisableIncapPlayerDamage
        );
    }

    public Integer getMerciful() {
        if (merciful != null) return merciful;
        else {
            Constants.LOG.warn("Warning - merciful config not set. Using default value.");
            return defaultData.merciful;
        }
    }

    public Boolean isHunter() {
        if (hunter != null) return hunter;
        else {
            Constants.LOG.warn("Warning - hunter config not set. Using default value.");
            return defaultData.hunter;
        }
    }

    public Boolean isSlow() {
        if (slow != null) return slow;
        else {
            Constants.LOG.warn("Warning - slow config not set. Using default value.");
            return defaultData.slow;
        }
    }

    public Boolean isWeakened() {
        if (weakened != null) return weakened;
        else {
            Constants.LOG.warn("Warning - weakened config not set. Using default value.");
            return defaultData.weakened;
        }
    }

    public Boolean isRegenerating() {
        if (regenerating != null) return regenerating;
        else {
            Constants.LOG.warn("Warning - regenerating config not set. Using default value.");
            return defaultData.regenerating;
        }
    }

    public Boolean isUnlimitedDowns() {
        if (unlimitedDowns != null) return unlimitedDowns;
        else {
            Constants.LOG.warn("Warning - unlimitedDowns config not set. Using default value.");
            return defaultData.unlimitedDowns;
        }
    }

    public Boolean isDownLogging() {
        if (downLogging != null) return downLogging;
        else {
            Constants.LOG.warn("Warning - downLogging config not set. Using default value.");
            return defaultData.downLogging;
        }
    }

    public Boolean isReviveMessage() {
        if (reviveMessage != null) return reviveMessage;
        else {
            Constants.LOG.warn("Warning - reviveMessage config not set. Using default value.");
            return defaultData.reviveMessage;
        }
    }

    public String getFoodReviveList() {
        if (foodReviveList != null) return foodReviveList;
        else {
            Constants.LOG.warn("Warning - foodReviveList config not set. Using default value.");
            return defaultData.foodReviveList;
        }
    }

    public String getFoodHealList() {
        if (foodHealList != null) return foodHealList;
        else {
            Constants.LOG.warn("Warning - foodHealList config not set. Using default value.");
            return defaultData.foodHealList;
        }
    }

    public Integer getDownTicks() {
        if (downTicks != null) return downTicks;
        else {
            Constants.LOG.warn("Warning - downTicks config not set. Using default value.");
            return defaultData.downTicks;
        }
    }

    public Integer getReviveTicks() {
        if (reviveTicks != null) return reviveTicks;
        else {
            Constants.LOG.warn("Warning - reviveTicks config not set. Using default value.");
            return defaultData.reviveTicks;
        }
    }

    public Integer getDownCounter() {
        if (downCounter != null) return downCounter;
        else {
            Constants.LOG.warn("Warning - downCounter config not set. Using default value.");
            return defaultData.downCounter;
        }
    }

    public Boolean isGlowingWhileDowned() {
        if (glowingWhileDowned != null) return glowingWhileDowned;
        else {
            Constants.LOG.warn("Warning - glowingWhileDowned config not set. Using default value.");
            return defaultData.glowingWhileDowned;
        }
    }

    public Boolean isSomeInstantKills() {
        if (someInstantKills != null) return someInstantKills;
        else {
            Constants.LOG.warn("Warning - someInstantKills config not set. Using default value.");
            return defaultData.someInstantKills;
        }
    }

    public Boolean isGlobalIncapMessage() {
        if (globalIncapMessage != null) return globalIncapMessage;
        else {
            Constants.LOG.warn("Warning - globalIncapMessage config not set. Using default value.");
            return defaultData.globalIncapMessage;
        }
    }

    public Boolean isGlobalReviveMessage() {
        if (globalReviveMessage != null) return globalReviveMessage;
        else {
            Constants.LOG.warn("Warning - globalReviveMessage config not set. Using default value.");
            return defaultData.globalReviveMessage;
        }
    }

    public Boolean isUseSecondsForRevive() {
        if (useSecondsForRevive != null) return useSecondsForRevive;
        else {
            Constants.LOG.warn("Warning - useSecondsForRevive config not set. Using default value.");
            return defaultData.useSecondsForRevive;
        }
    }

    public Boolean isCanBreakOrInteractWithBlocks() {
        if (canBreakOrInteractWithBlocks != null) return canBreakOrInteractWithBlocks;
        else {
            Constants.LOG.warn("Warning - canBreakOrInteractWithBlocks config not set. Using default value.");
            return defaultData.canBreakOrInteractWithBlocks;
        }
    }

    public Boolean isHealPercentageOfMaxHealth() {
        if (healPercentageOfMaxHealth != null) return healPercentageOfMaxHealth;
        else {
            Constants.LOG.warn("Warning - healPercentageOfMaxHealth config not set. Using default value.");
            return defaultData.healPercentageOfMaxHealth;
        }
    }

    public float getReviveHealth() {
        if (reviveHealth != null) return reviveHealth;
        else {
            Constants.LOG.warn("Warning - reviveHealth config not set. Using default value.");
            return defaultData.reviveHealth;
        }
    }

    public Integer getReviveHunger() {
        if (reviveHunger != null) return reviveHunger;
        else {
            Constants.LOG.warn("Warning - reviveHunger config not set. Using default value.");
            return defaultData.reviveHunger;
        }
    }

    public float getReviveSaturation() {
        if (reviveSaturation != null) return reviveSaturation;
        else {
            Constants.LOG.warn("Warning - reviveSaturation config not set. Using default value.");
            return defaultData.reviveSaturation;
        }
    }

    public String getInstantKills() {
        if (instantKills != null) return instantKills;
        else {
            Constants.LOG.warn("Warning - instantKills config not set. Using default value.");
            return defaultData.instantKills;
        }
    }

    public ArrayList<IncapEffectData> getIncapEffectData() {
        if (incapEffectData != null) return incapEffectData;
        else {
            Constants.LOG.warn("Warning - incapEffectData config not set. Using default value.");
            return defaultData.incapEffectData;
        }
    }

    public Boolean isDANGERDisableGiveUp() {
        if (DANGERDisableGiveUp != null) return DANGERDisableGiveUp;
        else {
            Constants.LOG.warn("Warning - DANGERDisableGiveUp config not set. Using default value.");
            return defaultData.DANGERDisableGiveUp;
        }
    }

    public Boolean isCanJumpWhileDown() {
        if (canJumpWhileDown != null) return canJumpWhileDown;
        else {
            Constants.LOG.warn("Warning - canJumpWhileDown config not set. Using default value.");
            return defaultData.canJumpWhileDown;
        }
    }

    public Boolean getDANGERDisableIncapPlayerDamage() {
        if (DANGERDisableIncapPlayerDamage != null) return DANGERDisableIncapPlayerDamage;
        else {
            Constants.LOG.warn("Warning - DANGERDisableIncapPlayerDamage config not set. Using default value.");
            return defaultData.DANGERDisableIncapPlayerDamage;
        }
    }

    public Boolean isShouldDownTimeReset() {
        if (shouldDownTimeReset != null) return shouldDownTimeReset;
        else {
            Constants.LOG.warn("Warning - shouldDownTimeReset config not set. Using default value.");
            return defaultData.shouldDownTimeReset;
        }
    }
}
