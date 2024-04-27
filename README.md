# Config details

The following is a description of all of the configs you can change to modify how incapacitated works.
Note: This does not include client specific configs. Those are their own file.
You can reload your config at any time with /incap config reload

* Merciless - Can be filled with a 0, 1, or a 2, determines if players are immune to damage while downed.
  * 0 - No, players are not immune to damage while downed.
  * 1 - Yes, players are immune to damage while downed, but part of the damage is removed from their time to live.
  * 2 - Yes, players are fully immune to damage while downed.

* Hunter - true or false, can players revive themselves with a (non-player) kill

* Slow - true or false, are incapacitated players slowed down dramatically?

* Weakened - true or false, are incapacitated players weakened dramatically

* Regenerating - true or false, does sleeping in a bed successfully award players with being able to go down again? (Not above maximum)

* UnlimitedDowns - true or false, does the player have unlimited downs

* DownLogging - true or false, does the player die when they log out, if they are incapacitated.

* ReviveMessage - true or false, does the player receive information in the chat when revived about their stats?

* FoodReviveList - string, a list of items ids of items that, when consumed, will revive the player (ex: minecraft:enchanted_golden_apple)

* FoodHealList - string, a list of items ids of items that, when consumed, will reset the amount of times a player can go down before instant death (ex: minecraft:golden_apple)

* DownTicks - whole number, how many ticks (20 per second if not lagging) can a player persist incapacitated before dying?

* ReviveTicks - whole number, how many ticks (20 per second if not lagging) does it take to revive another player?

* DownCounter - whole number, how many times can a player be revived without some form of proper healing, before they instantly die if they reach 0 HP again.

* GlowingWhileDowned - true or false, does the player have the glowing effect when incapacitated to be found easier?

* SomeInstantKills - true or false, do some damage types instantly kill the player when they reach 0 hp, regardless of how many downs they have left?

* GlobalIncapMessage - true or false, do messages about players being incapacitated get broadcast globally?

* GlobalReviveMessage - true or false, do messages about players being revived get broadcast globally?

* UseSecondsForRevive - true or false, when reviving a player, should seconds on the revive be displayed instead of the progress bar?

* HealPercentageOfMaxHealth - true or false, when true, reviveHealth is a multiplier added to a player's max health, healing them for the result, on false, reviveHealth is the exact amount healed.

* ReviveHealth - number, potentially decimal, depending on HealPercentageOfMaxHealth, a multiplier to the max health to heal by when reviving, or a direct number to heal by.

* ReviveHunger - whole number, how many food points should a user have when revived? Negative numbers avoid adjusting food values.

* ReviveSaturation - number, potentially decimal, what should the hunger saturation of a recently revived player be? Numbers at or below -1 avoid adjusting saturation values.

