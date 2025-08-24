package com.cartoonishvillain.incapacitated.config;

import com.cartoonishvillain.incapacitated.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class IncapConfigData implements Serializable {
    String info;
    Integer merciful;
    Boolean canBreakOrInteractWithBlocks;
    Boolean canJumpWhileDown;
    Integer hunter;
    Boolean slow;
    Boolean weakened;
    Boolean regenerating;
    Boolean unlimitedDowns;
    Boolean downLogging;
    Boolean reviveMessage;
    Integer downTicks;
    Integer reviveTicks;
    Integer downCounter;
    Boolean someInstantKills;
    Boolean globalIncapMessage;
    Boolean globalReviveMessage;
    Boolean useSecondsForRevive;
    Boolean healPercentageOfMaxHealth;
    Float reviveHealth;
    Integer reviveHunger;
    Float reviveSaturation;
    Boolean shouldDownTimeReset;
    Boolean shouldDisableFallFlying;
    Boolean shouldDieOnTimeout;
    Boolean shouldDieOnOverkillDamage;
    Boolean shouldBlameIncapacitations;
    Boolean shouldBlameRevives;
    ArrayList<IncapEffectData> incapEffectData;
    ArrayList<IncapEffectData> reviveEffectData;
    Boolean DANGERDisableGiveUp;
    Boolean DANGERDisableIncapPlayerDamage;
    Boolean DANGERManipulateGoalToAvoidDownPlayers;
    Boolean DANGERFullServerKill;
    
    public static IncapConfigData defaultData = buildDefaultConfig();

    public IncapConfigData(Integer merciful, Integer hunter, Boolean canBreakOrInteractWithBlocks, Boolean canJumpWhileDown, Boolean slow, Boolean weakened, Boolean regenerating, Boolean unlimitedDowns, Boolean downLogging, Boolean reviveMessage, Integer downTicks, Integer reviveTicks, Integer downCounter, Boolean someInstantKills, Boolean globalIncapMessage, Boolean globalReviveMessage, Boolean useSecondsForRevive,
    Boolean healPercentageOfMaxHealth, float reviveHealth, Integer reviveHunger, float reviveSaturation, Boolean shouldDownTimeReset, Boolean shouldDieOnTimeout, Boolean shouldDieOnOverkillDamage, ArrayList<IncapEffectData> incapEffectData, ArrayList<IncapEffectData> reviveEffectData, Boolean DANGERDisableGiveUp, Boolean DANGERDisableIncapPlayerDamage, Boolean shouldDisableFallFlying, Boolean shouldBlameIncapacitations, Boolean shouldBlameRevives, Boolean DANGERManipulateGoalToAvoidDownPlayers, Boolean DANGERFullServerKill) {
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
        this.downTicks = downTicks;
        this.reviveTicks = reviveTicks;
        this.downCounter = downCounter;
        this.someInstantKills = someInstantKills;
        this.globalIncapMessage = globalIncapMessage;
        this.globalReviveMessage = globalReviveMessage;
        this.useSecondsForRevive = useSecondsForRevive;
        this.healPercentageOfMaxHealth = healPercentageOfMaxHealth;
        this.reviveHealth = reviveHealth;
        this.reviveHunger = reviveHunger;
        this.reviveSaturation = reviveSaturation;
        this.shouldDownTimeReset = shouldDownTimeReset;
        this.shouldDisableFallFlying = shouldDisableFallFlying;
        this.shouldDieOnTimeout = shouldDieOnTimeout;
        this.shouldDieOnOverkillDamage = shouldDieOnOverkillDamage;
        this.incapEffectData = incapEffectData;
        this.reviveEffectData = reviveEffectData;
        this.shouldBlameIncapacitations = shouldBlameIncapacitations;
        this.shouldBlameRevives = shouldBlameRevives;
        this.DANGERDisableGiveUp = DANGERDisableGiveUp;
        this.DANGERDisableIncapPlayerDamage = DANGERDisableIncapPlayerDamage;
        this.DANGERManipulateGoalToAvoidDownPlayers = DANGERManipulateGoalToAvoidDownPlayers;
        this.DANGERFullServerKill = DANGERFullServerKill;
    }

    public static IncapConfigData buildDefaultConfig() {
        ArrayList<IncapEffectData> downEffectData = new ArrayList<>();
        downEffectData.add(
                new IncapEffectData(
                        "minecraft:glowing",
                        70,
                        true,
                        true,
                        0
                )
        );

        return new IncapConfigData(
                0, //merciful
                0, //hunter
                false, //canBreakOrInteractWithBlocks
                false, //canJumpWhileDown
                false, //slow
                false, //weakened
                false, //regenerating
                false, //unlimitedDowns
                false, //downLogging
                true, //reviveMessage
                2000, //downTicks
                150, //reviveTicks
                3, //downCounter
                true, //someInstantKills
                true, //globalIncapMessage
                true, //globalReviveMessage
                false, //useSecondsForRevive
                true, //healPercentageOfMaxHealth
                0.33f, //reviveHealth
                -1, //reviveHunger
                -1f, //reviveSaturation
                false, //shouldDownTimeReset
                true, //shouldDieOnTimeout
                true, //shouldDieOnOverkillDamage
                downEffectData, //incapEffectData
                new ArrayList<>(), //reviveEffectData
                false, //DANGERDisableGiveUp
                false, //DANGERDisableIncapPlayerDamage
                false, //shouldDisableFallFlying
                true, //shouldBlameIncapacitations
                true, //shouldBlameRevives
                false, //DANGERManipulateGoalToAvoidDownPlayers
                false //DANGERFullServerKill
        );
    }

    public Boolean getShouldBlameIncapacitations() {
        if (shouldBlameIncapacitations != null) return shouldBlameIncapacitations;
        else {
            Constants.LOG.warn("Warning - shouldBlameIncapacitations config not set. Using default value.");
            shouldBlameIncapacitations = defaultData.shouldBlameIncapacitations;
            return defaultData.shouldBlameIncapacitations;
        }
    }

    public Boolean getShouldBlameRevives() {
        if (shouldBlameRevives != null) return shouldBlameRevives;
        else {
            Constants.LOG.warn("Warning - shouldBlameRevives config not set. Using default value.");
            shouldBlameRevives = defaultData.shouldBlameRevives;
            return defaultData.shouldBlameRevives;
        }
    }

    public Boolean getDANGERManipulateGoalToAvoidDownPlayers() {
        if (DANGERManipulateGoalToAvoidDownPlayers != null) return DANGERManipulateGoalToAvoidDownPlayers;
        else {
            Constants.LOG.warn("Warning - DANGERManipulateGoalToAvoidDownPlayers config not set. Using default value.");
            DANGERManipulateGoalToAvoidDownPlayers = defaultData.DANGERManipulateGoalToAvoidDownPlayers;
            return defaultData.DANGERManipulateGoalToAvoidDownPlayers;
        }
    }

    public Boolean getShouldDieOnTimeout() {
        if (shouldDieOnTimeout != null) return shouldDieOnTimeout;
        else {
            Constants.LOG.warn("Warning - shouldDieOnTimeout config not set. Using default value.");
            shouldDieOnTimeout = defaultData.shouldDieOnTimeout;
            return defaultData.shouldDieOnTimeout;
        }
    }

    public Boolean getShouldDieOnOverkillDamage() {
        if (shouldDieOnOverkillDamage != null) return shouldDieOnOverkillDamage;
        else {
            Constants.LOG.warn("Warning - shouldDieOnOverkillDamage config not set. Using default value.");
            shouldDieOnOverkillDamage = defaultData.shouldDieOnOverkillDamage;
            return defaultData.shouldDieOnOverkillDamage;
        }
    }

    public Integer getMerciful() {
        if (merciful != null) return merciful;
        else {
            Constants.LOG.warn("Warning - merciful config not set. Using default value.");
            merciful = defaultData.merciful;
            return defaultData.merciful;
        }
    }

    public Integer isHunter() {
        if (hunter != null) return hunter;
        else {
            Constants.LOG.warn("Warning - hunter config not set. Using default value.");
            hunter = defaultData.hunter;
            return defaultData.hunter;
        }
    }

    public Boolean isSlow() {
        if (slow != null) return slow;
        else {
            Constants.LOG.warn("Warning - slow config not set. Using default value.");
            slow = defaultData.slow;
            return defaultData.slow;
        }
    }

    public Boolean isWeakened() {
        if (weakened != null) return weakened;
        else {
            Constants.LOG.warn("Warning - weakened config not set. Using default value.");
            weakened = defaultData.weakened;
            return defaultData.weakened;
        }
    }

    public Boolean isRegenerating() {
        if (regenerating != null) return regenerating;
        else {
            Constants.LOG.warn("Warning - regenerating config not set. Using default value.");
            regenerating = defaultData.regenerating;
            return defaultData.regenerating;
        }
    }

    public Boolean isUnlimitedDowns() {
        if (unlimitedDowns != null) return unlimitedDowns;
        else {
            Constants.LOG.warn("Warning - unlimitedDowns config not set. Using default value.");
            unlimitedDowns = defaultData.unlimitedDowns;
            return defaultData.unlimitedDowns;
        }
    }

    public Boolean isDownLogging() {
        if (downLogging != null) return downLogging;
        else {
            Constants.LOG.warn("Warning - downLogging config not set. Using default value.");
            downLogging = defaultData.downLogging;
            return defaultData.downLogging;
        }
    }

    public Boolean isReviveMessage() {
        if (reviveMessage != null) return reviveMessage;
        else {
            Constants.LOG.warn("Warning - reviveMessage config not set. Using default value.");
            reviveMessage = defaultData.reviveMessage;
            return defaultData.reviveMessage;
        }
    }

    public Integer getDownTicks() {
        if (downTicks != null) return downTicks;
        else {
            Constants.LOG.warn("Warning - downTicks config not set. Using default value.");
            downTicks = defaultData.downTicks;
            return defaultData.downTicks;
        }
    }

    public Integer getReviveTicks() {
        if (reviveTicks != null) return reviveTicks;
        else {
            Constants.LOG.warn("Warning - reviveTicks config not set. Using default value.");
            reviveTicks = defaultData.reviveTicks;
            return defaultData.reviveTicks;
        }
    }

    public Integer getDownCounter() {
        if (downCounter != null) return downCounter;
        else {
            Constants.LOG.warn("Warning - downCounter config not set. Using default value.");
            downCounter = defaultData.downCounter;
            return defaultData.downCounter;
        }
    }

    public Boolean isSomeInstantKills() {
        if (someInstantKills != null) return someInstantKills;
        else {
            Constants.LOG.warn("Warning - someInstantKills config not set. Using default value.");
            someInstantKills = defaultData.someInstantKills;
            return defaultData.someInstantKills;
        }
    }

    public Boolean isGlobalIncapMessage() {
        if (globalIncapMessage != null) return globalIncapMessage;
        else {
            Constants.LOG.warn("Warning - globalIncapMessage config not set. Using default value.");
            globalIncapMessage = defaultData.globalIncapMessage;
            return defaultData.globalIncapMessage;
        }
    }

    public Boolean isGlobalReviveMessage() {
        if (globalReviveMessage != null) return globalReviveMessage;
        else {
            Constants.LOG.warn("Warning - globalReviveMessage config not set. Using default value.");
            globalReviveMessage = defaultData.globalReviveMessage;
            return defaultData.globalReviveMessage;
        }
    }

    public Boolean isUseSecondsForRevive() {
        if (useSecondsForRevive != null) return useSecondsForRevive;
        else {
            Constants.LOG.warn("Warning - useSecondsForRevive config not set. Using default value.");
            useSecondsForRevive = defaultData.useSecondsForRevive;
            return defaultData.useSecondsForRevive;
        }
    }

    public Boolean isCanBreakOrInteractWithBlocks() {
        if (canBreakOrInteractWithBlocks != null) return canBreakOrInteractWithBlocks;
        else {
            Constants.LOG.warn("Warning - canBreakOrInteractWithBlocks config not set. Using default value.");
            canBreakOrInteractWithBlocks = defaultData.canBreakOrInteractWithBlocks;
            return defaultData.canBreakOrInteractWithBlocks;
        }
    }

    public Boolean isHealPercentageOfMaxHealth() {
        if (healPercentageOfMaxHealth != null) return healPercentageOfMaxHealth;
        else {
            Constants.LOG.warn("Warning - healPercentageOfMaxHealth config not set. Using default value.");
            healPercentageOfMaxHealth = defaultData.healPercentageOfMaxHealth;
            return defaultData.healPercentageOfMaxHealth;
        }
    }

    public float getReviveHealth() {
        if (reviveHealth != null) return reviveHealth;
        else {
            Constants.LOG.warn("Warning - reviveHealth config not set. Using default value.");
            reviveHealth = defaultData.reviveHealth;
            return defaultData.reviveHealth;
        }
    }

    public Integer getReviveHunger() {
        if (reviveHunger != null) return reviveHunger;
        else {
            Constants.LOG.warn("Warning - reviveHunger config not set. Using default value.");
            reviveHunger = defaultData.reviveHunger;
            return defaultData.reviveHunger;
        }
    }

    public float getReviveSaturation() {
        if (reviveSaturation != null) return reviveSaturation;
        else {
            Constants.LOG.warn("Warning - reviveSaturation config not set. Using default value.");
            reviveSaturation = defaultData.reviveSaturation;
            return defaultData.reviveSaturation;
        }
    }

    public ArrayList<IncapEffectData> getIncapEffectData() {
        if (incapEffectData != null) return incapEffectData;
        else {
            Constants.LOG.warn("Warning - incapEffectData config not set. Using default value.");
            incapEffectData = defaultData.incapEffectData;
            return defaultData.incapEffectData;
        }
    }

    public ArrayList<IncapEffectData> getReviveEffectData() {
        if (reviveEffectData != null) return reviveEffectData;
        else {
            Constants.LOG.warn("Warning - reviveEffectData config not set. Using default value.");
            reviveEffectData = defaultData.reviveEffectData;
            return defaultData.reviveEffectData;
        }
    }

    public Boolean isDANGERDisableGiveUp() {
        if (DANGERDisableGiveUp != null) return DANGERDisableGiveUp;
        else {
            Constants.LOG.warn("Warning - DANGERDisableGiveUp config not set. Using default value.");
            DANGERDisableGiveUp = defaultData.DANGERDisableGiveUp;
            return defaultData.DANGERDisableGiveUp;
        }
    }

    public Boolean isCanJumpWhileDown() {
        if (canJumpWhileDown != null) return canJumpWhileDown;
        else {
            Constants.LOG.warn("Warning - canJumpWhileDown config not set. Using default value.");
            canJumpWhileDown = defaultData.canJumpWhileDown;
            return defaultData.canJumpWhileDown;
        }
    }

    public Boolean getDANGERDisableIncapPlayerDamage() {
        if (DANGERDisableIncapPlayerDamage != null) return DANGERDisableIncapPlayerDamage;
        else {
            Constants.LOG.warn("Warning - DANGERDisableIncapPlayerDamage config not set. Using default value.");
            DANGERDisableIncapPlayerDamage = defaultData.DANGERDisableIncapPlayerDamage;
            return defaultData.DANGERDisableIncapPlayerDamage;
        }
    }

    public Boolean isShouldDownTimeReset() {
        if (shouldDownTimeReset != null) return shouldDownTimeReset;
        else {
            Constants.LOG.warn("Warning - shouldDownTimeReset config not set. Using default value.");
            shouldDownTimeReset = defaultData.shouldDownTimeReset;
            return defaultData.shouldDownTimeReset;
        }
    }

    public Boolean getShouldDisableFallFlying() {
        if (shouldDisableFallFlying != null) return shouldDisableFallFlying;
        else {
            Constants.LOG.warn("Warning - shouldDisableFallFlying config not set. Using default value.");
            shouldDisableFallFlying = defaultData.shouldDisableFallFlying;
            return defaultData.shouldDisableFallFlying;
        }
    }

    public Boolean getDANGERFullServerKill() {
        if (DANGERFullServerKill != null) return DANGERFullServerKill;
        else {
            Constants.LOG.warn("Warning - shouldDisableFallFlying config not set. Using default value.");
            DANGERFullServerKill = defaultData.DANGERFullServerKill;
            return defaultData.DANGERFullServerKill;
        }
    }
}
