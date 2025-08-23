# Config details

The following is a description of all of the configs you can change to modify how incapacitated works.
Note: This does not include client specific configs. Those are their own file.
You can reload your config at any time with /incap config reload

* Merciless - Can be filled with a 0, 1, or a 2, determines if players are immune to damage while downed.
    * 0 - No, players are not immune to damage while downed.
    * 1 - Yes, players are immune to damage while downed, but part of the damage is removed from their time to live.
    * 2 - Yes, players are fully immune to damage while downed.

* Hunter - true or false, can players revive themselves with a (non-player) kill

* CanBreakOrInteractWithBlocks - true or false, can players while downed break or interact with blocks

* CanJumpWhileDown - true or false, can players while downed jump

* Slow - true or false, are incapacitated players slowed down dramatically

* Weakened - true or false, are incapacitated players weakened dramatically

* Regenerating - true or false, does sleeping in a bed successfully award players with being able to go down again? (Not above maximum)

* UnlimitedDowns - true or false, does the player have unlimited downs

* DownLogging - true or false, does the player die when they log out, if they are incapacitated.

* ReviveMessage - true or false, does the player receive information in the chat when revived about their stats?

* FoodReviveList - string, a list of items ids of items that, when consumed, will revive the player (ex: minecraft:enchanted_golden_apple)

* FoodAdrenalineList - string, a list of item ids of items that, when consumed, will revive the player, but not heal them (no resetting down timers or counts)

* FoodHealList - string, a list of items ids of items that, when consumed, will reset the amount of times a player can go down before instant death (ex: minecraft:golden_apple)

* DownTicks - whole number, how many ticks (20 per second if not lagging) can a player persist incapacitated before dying?

* ReviveTicks - whole number, how many ticks (20 per second if not lagging) does it take to revive another player?

* DownCounter - whole number, how many times can a player be revived without some form of proper healing, before they instantly die if they reach 0 HP again.

* SomeInstantKills - true or false, do some damage types instantly kill the player when they reach 0 hp, regardless of how many downs they have left?

* InstantKills - string, names of damage types that should be a part of "SomeInstantKills". The name used is the translation ID of the death message (such as death.attack.wither -> "wither"). Comma separated, no spaces.

* GlobalIncapMessage - true or false, do messages about players being incapacitated get broadcast globally?

* GlobalReviveMessage - true or false, do messages about players being revived get broadcast globally?

* UseSecondsForRevive - true or false, when reviving a player, should seconds on the revive be displayed instead of the progress bar?

* HealPercentageOfMaxHealth - true or false, when true, reviveHealth is a multiplier added to a player's max health, healing them for the result, on false, reviveHealth is the exact amount healed.

* ReviveHealth - number, potentially decimal, depending on HealPercentageOfMaxHealth, a multiplier to the max health to heal by when reviving, or a direct number to heal by.

* ReviveHunger - whole number, how many food points should a user have when revived? Negative numbers avoid adjusting food values.

* ReviveSaturation - number, potentially decimal, what should the hunger saturation of a recently revived player be? Numbers at or below -1 avoid adjusting saturation values.

* ShouldDownTimeReset - true or false, if a user is revived, should the bleed out timer reset to the configured value for the next revive?

* ShouldDisableFallFlying - true or false, should players be restricted from using elytras while incapacitated?

* ShouldDieOnTimeout - true or false, should players die when the incap timer runs out? Defaults to true.

* ShouldDieOnOverkillDamage - true or false, should players when taking a significant amount of damage in the hit leading to 0 health be instantly killed? Defaults to true.

* IncapEffectData - custom json array, see below for more details on how to set up. Adds additional potion effects to users while they're incapacitated.

* DANGERDisableGiveUp - true or false, disable the give up command. Not recommended.

* DANGERDisableIncapPlayerDamage - true or false, should any damage attributed to an incapacitated player be canceled?

* DANGERManipulateGoalToAvoidDownPlayers - true or false, should entities ignore players while they're incapacitated.
* WARNING WARNING WARNING WARNING - This is achieved by faking that the player is in spectator mode. This shouldn't effect their capabilities in a vanilla sense, but may have weird effects when paired with other mods that check for spectators!
* Like all DANGER items, use this at your own risk, but seriously USE THIS AT YOUR OWN RISK

* DANGERFullServerKill - true or false, if all players are down, should everyone die? 
This checks every time someone goes down or dies.
* If the Hunter config is on, this will never run, as players could possibly revive themselves
* Inventories are scanned for any items on the FoodReviveList, if such an item is present in any live player's inventory, this won't count.
* If everyone is down, dead, or in spectator mode, everyone not in spectator or creative mode is instantly killed.


# IncapEffectData
IncapEffectData is an array of objects defined as   
{  
effectID: String  
amplifier: Integer  
ambient:  boolean  
}

An example would look like:

incapEffectData: [  
{  
effectID: "minecraft:speed",    
amplifier: 1,    
ambient: true  
},  
{  
effectID: "minecraft:resistance",  
amplifier: 3,  
ambient:false  
}  
]  

