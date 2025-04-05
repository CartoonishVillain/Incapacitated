package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import com.cartoonishvillain.incapacitated.events.RevivePlayerState;
import com.cartoonishvillain.incapacitated.mixin.IncapacitatedInventoryAccessor;
import com.cartoonishvillain.incapacitated.mixin.IncapacitatedItemAccessor;
import com.cartoonishvillain.incapacitated.mixin.LoadEffectInvoker;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.cartoonishvillain.incapacitated.FabricIncapacitated.*;
import static net.minecraft.world.entity.player.Inventory.SLOT_OFFHAND;
import static net.minecraft.world.entity.player.Player.BedSleepingProblem.OTHER_PROBLEM;

public class AbstractedIncapacitation {

    public static void downOrKill(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated() && !allKill(player)) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!configData.isUnlimitedDowns()) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    if (player instanceof ServerPlayer) player.awardStat(Services.getIncappedStat());
                    incapacitatedPlayerData.setIncapacitated(true);
                    player.setHealth(player.getMaxHealth());
                    if (configData.isGlowingWhileDowned())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                    Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);

                    if (configData.isSlow()) {
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()), -1, 6, true, false));
                    }

                    if (configData.isWeakened()) {
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()), -1, 100, true, false));
                    }

                    if (!FabricIncapacitated.effectInstances.isEmpty()) {
                        for (MobEffectInstance effectInstance : FabricIncapacitated.effectInstances) {
                            player.addEffect(effectInstance);
                        }
                    }

                    if (configData.isGlobalIncapMessage()) {
                        broadcast(player.theGame(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }
                    Services.writePlayerData(player, incapacitatedPlayerData);
                } else {
                    if (player instanceof ServerPlayer) player.kill(((ServerPlayer) player).serverLevel());
                }
            } else if(!incapacitatedPlayerData.isIncapacitated()) { //if the player is incapacitated, and everyone is with no chance of revive
                killAllPlayers(player);
            }
            else {
                if (player instanceof ServerPlayer) player.kill(((ServerPlayer) player).serverLevel());
            }
    }

    public static void downOrKill(Player player, CallbackInfo event, DamageSource damageSource) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        boolean shouldCancel = false;
        Boolean allKillCheck = allKill(player);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated() && !(configData.isSomeInstantKills()) && !allKillCheck) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!configData.isUnlimitedDowns()) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    if (player instanceof ServerPlayer) player.awardStat(Services.getIncappedStat());
                    incapacitatedPlayerData.setIncapacitated(true);
                    Services.setDamageSource(player.level(), damageSource, player);
                    shouldCancel = true;
                    player.setHealth(player.getMaxHealth());

                    if (configData.isGlowingWhileDowned())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                    Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);

                    if (configData.isSlow()) {
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()), -1, 6, true, false));
                    }

                    if (configData.isWeakened()) {
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()), -1, 100, true, false));
                    }

                    if (!FabricIncapacitated.effectInstances.isEmpty()) {
                        for (MobEffectInstance effectInstance : FabricIncapacitated.effectInstances) {
                            player.addEffect(effectInstance);
                        }
                    }

                    if (configData.isGlobalIncapMessage()) {
                        broadcast(player.theGame(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }
                    Services.writePlayerData(player, incapacitatedPlayerData);
                }
            } else if (!incapacitatedPlayerData.isIncapacitated() && (configData.isSomeInstantKills()) && !allKillCheck) {
                boolean notInstantKill = true;
                //check if the damage type is in the instant kill list, if it does, don't cancel KillPlayer event.
                for (String damageType : FabricIncapacitated.instantKillDamageSourcesMessageID) {
                    if (damageType.contains(damageSource.getMsgId())) {
                        notInstantKill = false;
                    }
                }
                if (notInstantKill) {
                    //reduce downs until KillPlayer, unless unlimitedDowns is on.
                    if (!configData.isUnlimitedDowns()) {
                        incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                    }
                    //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                    if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                        if (player instanceof ServerPlayer) player.awardStat(Services.getIncappedStat());
                        incapacitatedPlayerData.setIncapacitated(true);
                        Services.setDamageSource(player.level(), damageSource, player);
                        shouldCancel = true;
                        player.setHealth(player.getMaxHealth());
                        if (configData.isGlowingWhileDowned())
                            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                        Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);

                        if (configData.isSlow()) {
                            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()), -1, 6, true, false));
                        }

                        if (configData.isWeakened()) {
                            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()), -1, 100, true, false));
                        }

                        if (!FabricIncapacitated.effectInstances.isEmpty()) {
                            for (MobEffectInstance effectInstance : FabricIncapacitated.effectInstances) {
                                player.addEffect(effectInstance);
                            }
                        }

                        if (configData.isGlobalIncapMessage()) {
                            broadcast(player.theGame(), Component.translatable("message.incap.message", player.getScoreboardName()));
                        } else {
                            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                            for (Player players : playerEntities) {
                                players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                            }
                        }

                    }
                    Services.writePlayerData(player, incapacitatedPlayerData);
                    if (shouldCancel) {
                        event.cancel();
                    }
                }
            }
            else if (!incapacitatedPlayerData.isIncapacitated() && allKillCheck) {
                killAllPlayers(player);
            }
            else {
                if (player instanceof ServerPlayer) player.kill(((ServerPlayer) player).serverLevel());
            }
    }

    private static void killAllPlayers(Player player) {
        for (ServerPlayer deadPlayer : player.theGame().playerList().getPlayers()) {
            if (deadPlayer.isSpectator() || deadPlayer.isCreative()) {} //don't kill dead or creative players.
            else {
                if (player instanceof ServerPlayer) deadPlayer.kill(((ServerPlayer) player).serverLevel());
            }
        }
    }

    private static boolean allKill(Player player) {
        boolean shouldEveryoneDie = false;
        if (configData.getDANGERFullServerKill() && !configData.isHunter()) { //Hunter is a hard conflict as players can easily revive themselves with it.
            TheGame server = player.theGame();
            List<ServerPlayer> players = server.playerList().getPlayers();
            boolean everyoneIsDown = true;
            for (ServerPlayer playerChecked : players) {
                if (!playerChecked.isDeadOrDying() && !playerChecked.isSpectator()) { //don't inventory check or whatever if the player is dead or spectating.
                    for (ItemStack items : ((IncapacitatedInventoryAccessor) playerChecked.getInventory()).getItems()) {
                        String item = ((IncapacitatedItemAccessor) items.getItem()).getBuiltInRegistryHolder().key().location().toString();
                        if (reviveFoods.contains(item) || adrenalineFoods.contains(item)) {
                            everyoneIsDown = false; //The player can revive themselves with an item in their inventory. Not all hope is lost.
                            break;
                        }
                    }

                    if (!player.getInventory().getItem(SLOT_OFFHAND).isEmpty()) {
                        String offhand = ((IncapacitatedItemAccessor) playerChecked.getInventory().getItem(SLOT_OFFHAND).getItem()).getBuiltInRegistryHolder().key().location().toString();
                        if (reviveFoods.contains(offhand) || adrenalineFoods.contains(offhand)) {
                            everyoneIsDown = false; //The player can revive themselves with an item in their inventory. Not all hope is lost.
                            break;
                        }
                    }


                    IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(playerChecked);
                    if (!incapacitatedPlayerData.isIncapacitated() && playerChecked != player) {
                        everyoneIsDown = false; //If someone is found not down, we don't need to check any more players
                        break;
                    }
                }
            }
            shouldEveryoneDie = everyoneIsDown;
        }
        return shouldEveryoneDie;
    }

    public static void pose(Player player, CallbackInfo ci, boolean cancellable) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        if(incapacitatedPlayerData.isIncapacitated()) {
            player.setPose(Pose.SWIMMING);
            if (cancellable)
                ci.cancel();
        }
    }

    public static void revive(Player player, IncapacitatedPlayerData incapacitatedPlayerData, boolean shouldResetTimer) {
        incapacitatedPlayerData.setIncapacitated(false);
        incapacitatedPlayerData.setReviveCounter(configData.getReviveTicks());
        if (shouldResetTimer) incapacitatedPlayerData.setTicksUntilDeath(configData.getDownTicks());
        player.removeEffect(MobEffects.GLOWING);
        player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()));
        player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()));

        if (!effectInstances.isEmpty()) {
            for (MobEffectInstance effectInstance : effectInstances) {
                player.removeEffect(effectInstance.getEffect());
            }
        }

        Services.writePlayerData(player, incapacitatedPlayerData);
        if (!player.level().isClientSide) {
            Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);
        }
        healPlayerWhenReviving(player);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);

        if (configData.isGlobalReviveMessage()) {
            broadcast(player.theGame(), Component.translatable("message.revive.message", player.getScoreboardName()));
        } else {
            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
            for (Player players : playerEntities) {
                players.displayClientMessage(Component.translatable("message.revive.message", player.getScoreboardName()), false);
            }
        }

        if (configData.isReviveMessage() && !configData.isUnlimitedDowns()) {
            if (incapacitatedPlayerData.getDownsUntilDeath() > 1) {
                player.displayClientMessage(Component.translatable("message.revivecount.normal", incapacitatedPlayerData.getDownsUntilDeath()), false);
            } else if (incapacitatedPlayerData.getDownsUntilDeath() == 1) {
                player.displayClientMessage(Component.translatable("message.revivecount.one"), false);
            } else {
                player.displayClientMessage(Component.translatable("message.revivecount.zero"), false);
            }
        }

        resetDownTicks(player, incapacitatedPlayerData);
    }

    public static void revive(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        incapacitatedPlayerData.setIncapacitated(false);
        incapacitatedPlayerData.setReviveCounter(configData.getReviveTicks());
        player.removeEffect(MobEffects.GLOWING);
        player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()));
        player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()));

        if (!effectInstances.isEmpty()) {
            for (MobEffectInstance effectInstance : effectInstances) {
                player.removeEffect(effectInstance.getEffect());
            }
        }

        Services.writePlayerData(player, incapacitatedPlayerData);
        if (!player.level().isClientSide) {
            Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);
        }
        healPlayerWhenReviving(player);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);

        if (configData.isGlobalReviveMessage()) {
                broadcast(player.theGame(), Component.translatable("message.revive.message", player.getScoreboardName()));
            } else {
                ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                for (Player players : playerEntities) {
                    players.displayClientMessage(Component.translatable("message.revive.message", player.getScoreboardName()), false);
                }
            }

            if (configData.isReviveMessage() && !configData.isUnlimitedDowns()) {
                if (incapacitatedPlayerData.getDownsUntilDeath() > 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.normal", incapacitatedPlayerData.getDownsUntilDeath()), false);
                } else if (incapacitatedPlayerData.getDownsUntilDeath() == 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.one"), false);
                } else {
                    player.displayClientMessage(Component.translatable("message.revivecount.zero"), false);
                }
            }

            resetDownTicks(player, incapacitatedPlayerData);
    }


    public static void setDownCount(Player player, short value) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        incapacitatedPlayerData.setDownsUntilDeath(value);
        Services.writePlayerData(player, incapacitatedPlayerData);
        Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);
    }

    public static void setDownTicks(Player player, int value) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        incapacitatedPlayerData.setTicksUntilDeath(value);
        Services.writePlayerData(player, incapacitatedPlayerData);
        Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);
    }

    public static short getDownCount(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
        return (short) incapacitatedPlayerData.getDownsUntilDeath();
    }

    public static void healPlayerWhenReviving(Player player) {
        if (configData.isHealPercentageOfMaxHealth()) {
            player.setHealth(player.getMaxHealth() * configData.getReviveHealth());
        } else {
            player.setHealth(configData.getReviveHealth());
        }

        FoodData foodData = player.getFoodData();
        if (configData.getReviveHunger() > -1) {
            foodData.setFoodLevel(configData.getReviveHunger());
        }

        if (configData.getReviveSaturation() > -1) {
            foodData.setSaturation(configData.getReviveSaturation());
        }
    }

    public static void eat(LivingEntity entity, ItemStack itemStack){
        if(entity instanceof Player player && !entity.level().isClientSide()){
            String item = ((IncapacitatedItemAccessor) itemStack.getItem()).getBuiltInRegistryHolder().key().location().toString();
            IncapacitatedPlayerData incapacitatedPlayerData = Services.getPlayerData(player);
            if(FabricIncapacitated.healingFoods.contains(item)) {
                incapacitatedPlayerData.setDownsUntilDeath(configData.getDownCounter());
                incapacitatedPlayerData.setTicksUntilDeath(configData.getDownTicks());
            }

            if(incapacitatedPlayerData.isIncapacitated()) {
                if(reviveFoods.contains(item) || adrenalineFoods.contains(item)){
                    if (player instanceof ServerPlayer) player.awardStat(Services.getSelfReviveStat(), 1);
                    incapacitatedPlayerData.setIncapacitated(false);
                    incapacitatedPlayerData.setReviveCounter(configData.getReviveTicks());

                    if (reviveFoods.contains(item)) {
                        incapacitatedPlayerData.setDownsUntilDeath(configData.getDownCounter());
                        incapacitatedPlayerData.setTicksUntilDeath(configData.getDownTicks());
                    }
                    player.removeEffect(MobEffects.GLOWING);
                    player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getSlowEffect()));
                    player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Services.getWeakEffect()));

                    if (!effectInstances.isEmpty()) {
                        for (MobEffectInstance effectInstance : effectInstances) {
                            player.removeEffect(effectInstance.getEffect());
                        }
                    }
                    healPlayerWhenReviving(player);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);
                }
            } else if(reviveFoods.contains(item)) {
                incapacitatedPlayerData.setDownsUntilDeath(configData.getDownCounter());
                incapacitatedPlayerData.setTicksUntilDeath(configData.getDownTicks());
            }
            Services.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath(), incapacitatedPlayerData.ticksUntilDeath);
            Services.writePlayerData(player, incapacitatedPlayerData);
        }
    }

    public static void hurt(Player player, DamageSource damageSource, CallbackInfo cir, float amount){
        IncapacitatedPlayerData data = Services.getPlayerData(player);
        if (data.isIncapacitated() && configData.getMerciful() > 0 && !(damageSource.getMsgId().equals("bleedout"))) {
            if (configData.getMerciful() == 1 && !player.level().isClientSide) {
                int timeToRemove = (int) amount;
                if (timeToRemove > 2000) timeToRemove = 2000;
                data.setTicksUntilDeath(data.getTicksUntilDeath() - timeToRemove);
                Services.sendIncapPacket((ServerPlayer) player, player.getId(), data.isIncapacitated(), (short) data.getDownsUntilDeath(), data.getTicksUntilDeath());
                Services.writePlayerData(player, data);
            }

            boolean doDamageRegardless = false;
            for (String damageType : noMercyDamageSourcesMessageID) {
                if (damageType.contains(damageSource.getMsgId())) {
                    doDamageRegardless = true;
                    break;
                }
            }
            if (data.getTicksUntilDeath() > 0 && !doDamageRegardless)
                cir.cancel();
        }
    }
    
    public static void tick(Player downPlayer) {
        //Given event player's data
        IncapacitatedPlayerData playerData = Services.getPlayerData(downPlayer);
        if (downPlayer.tickCount == 10) Services.sendIncapPacket((ServerPlayer) downPlayer, downPlayer.getId(), playerData.isIncapacitated(), (short) playerData.getDownsUntilDeath(), playerData.ticksUntilDeath);

        //If the player is down, run all the code associated every tick, otherwise don't.
        if(playerData.isIncapacitated()) {
            downPlayer.setPose(Pose.SWIMMING);
            if (!downPlayer.level().isClientSide) {
                //Scan for any players nearby
                ArrayList<Player> playerEntities = (ArrayList<Player>) downPlayer.level().getEntitiesOfClass(Player.class, downPlayer.getBoundingBox().inflate(3));
                RevivePlayerState reviving = RevivePlayerState.CAPABLE_OF_REVIVING;
                Player revivingPlayer = null;

                //Loop through nearby players to check if any are reviving the downed player successfully
                for (Player player : playerEntities) {
                    if (player != downPlayer) {
                        reviving = Services.reviveCheckEvent(player, downPlayer);
                        if (reviving == RevivePlayerState.REVIVING) {
                            revivingPlayer = player;
                            break;
                        } else if (reviving == RevivePlayerState.CAPABLE_OF_REVIVING) { //If the player is in range, not down, and not reviving
                            player.displayClientMessage((Component.translatable("message.reviveindicator.revivetutorial").withStyle(ChatFormatting.GREEN)), true);
                        }
                    }
                }

                //If our event player is actively being revived.
                if (reviving == RevivePlayerState.REVIVING) {
                    //Count down the revive timer. Returns true if the timer is 0, at which point the player is revived.
                    if (playerData.downReviveCount()) {
                        if (revivingPlayer instanceof ServerPlayer) revivingPlayer.awardStat(Services.getReviveStat(), 1);
                        revive(downPlayer);
                    } else {
                        //If the timer is not 0 on the revive timer, tell both parties that the revive is occurring, and how much longer until it is done.
                        if (!configData.isUseSecondsForRevive()) {
                            downPlayer.displayClientMessage(revivingComponent(playerData, "message.downindicator.revivingbar"), true);
                            revivingPlayer.displayClientMessage(revivingComponent(playerData, "message.reviveindicator.revivingbar", downPlayer), true);
                        } else {
                            downPlayer.displayClientMessage(revivingComponent(playerData, "message.downindicator.reviving"), true);
                            revivingPlayer.displayClientMessage(revivingComponent(playerData, "message.reviveindicator.reviving", downPlayer), true);
                        }
                        Services.writePlayerData(downPlayer, playerData);
                    }
                } else {
                    //If our event player is not being revived, count down the timer until; their death. Returns true when the player runs out of time.
                    if (playerData.countTicksUntilDeath()) {
                        if (configData.getShouldDieOnTimeout()) killFromTimeout(downPlayer, playerData); //We now have a config to disable death based on bleedouts, reviving the player, as if they've recovered after somw downtime.
                        else revive(downPlayer, playerData, true);
                    } else if (playerData.getTicksUntilDeath() % 20 == 0) {
                        //Otherwise, every 20 ticks (1 second) send the dying player a message about how long, in seconds, they have until death.
                        if (configData.getShouldDieOnTimeout()) downPlayer.displayClientMessage(Component.translatable("message.downindicator.norevive", "/incap die", playerData.getTicksUntilDeath() / 20f).withStyle(ChatFormatting.RED), true);
                        else downPlayer.displayClientMessage(Component.translatable("message.downindicator.norevivesafe", playerData.getTicksUntilDeath() /20f).withStyle(ChatFormatting.LIGHT_PURPLE), true);
                    }

                    //Additionally, if the user is not reviving, make sure the revive timer is reset.
                    if (playerData.getReviveCounter() != configData.getReviveTicks()) {
                        playerData.setReviveCounter(configData.getReviveTicks());
                    }
                    Services.writePlayerData(downPlayer, playerData);
                }
            }
        }
    }

    private static void killFromTimeout(Player downPlayer, IncapacitatedPlayerData playerData) {
        downPlayer.hurt(Services.getDamageSource(downPlayer, downPlayer.level()), Float.MAX_VALUE);
        playerData.setReviveCounter(configData.getReviveTicks());
        downPlayer.removeEffect(MobEffects.GLOWING);
        playerData.setIncapacitated(false);
        Services.writePlayerData(downPlayer, playerData);
        Services.sendIncapPacket((ServerPlayer) downPlayer, downPlayer.getId(), false, (short) playerData.getDownsUntilDeath(), playerData.ticksUntilDeath);
    }

    public static void downLogging(Player player) {
        if (configData.isDownLogging()) {
            IncapacitatedPlayerData playerData = Services.getPlayerData(player);
            if (playerData.isIncapacitated()) {
                downOrKill(player);
            }
        }
    }

    public static void sleep(Player player, boolean wakeImmediately, boolean updateLevel) {
        if (!updateLevel && !wakeImmediately && configData.isRegenerating()) {
            IncapacitatedPlayerData playerData = Services.getPlayerData(player);
            if (playerData.getDownsUntilDeath() < configData.getDownCounter()) {
                AbstractedIncapacitation.setDownCount(player, (short) (playerData.getDownsUntilDeath() + 1));
            }
        }
    }

    public static void breakBlocks(Player player, CallbackInfoReturnable<Boolean> ci) {
        IncapacitatedPlayerData playerData = Services.getPlayerData(player);
        if (!configData.isCanBreakOrInteractWithBlocks() && playerData.isIncapacitated()) { //if they can, we do not change behavior..
            ci.setReturnValue(true);
        }
    }

    public static void sleep(ServerPlayer player, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> ci) {
        IncapacitatedPlayerData playerData = Services.getPlayerData(player);
        if (!configData.isCanBreakOrInteractWithBlocks() && playerData.isIncapacitated()) { //if they can, we do not change behavior..
            ci.setReturnValue(Either.left(OTHER_PROBLEM));
        }
    }

    public static void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> ci) {
        IncapacitatedPlayerData playerData = Services.getPlayerData(context.getPlayer());
        if (!configData.isCanBreakOrInteractWithBlocks() && playerData.isIncapacitated()) { //if they can, we do not change behavior..
            ci.setReturnValue(InteractionResult.FAIL);
        }
    }

    private static MutableComponent revivingComponent(IncapacitatedPlayerData playerData, String translatable) {
        if (!configData.isUseSecondsForRevive()) {
            MutableComponent barComponent = Component.literal("[").withStyle(ChatFormatting.GREEN);
            float percentage = 1 - ((float)playerData.getReviveCounter()/(float) configData.getReviveTicks());
            percentage *= 100;
            for (int i = 10; i > 0; i--) {
                if (percentage >= 10) {
                    percentage -= 10;
                    barComponent.append(Component.literal(":").withStyle(ChatFormatting.GOLD));
                } else {
                    barComponent.append(Component.literal(":").withStyle(ChatFormatting.DARK_GRAY));
                }
            }
            barComponent.append(Component.literal("]").withStyle(ChatFormatting.GREEN));

            return Component.translatable(translatable, barComponent).withStyle(ChatFormatting.GREEN);
        } else {
            return Component.translatable(translatable, (playerData.getReviveCounter() / 20)).withStyle(ChatFormatting.GREEN);
        }
    }

    private static MutableComponent revivingComponent(IncapacitatedPlayerData playerData, String translatable, Player player) {
        if (!configData.isUseSecondsForRevive()) {
            MutableComponent barComponent = Component.literal("[").withStyle(ChatFormatting.GREEN);
            float percentage = 1 - ((float)playerData.getReviveCounter()/(float) configData.getReviveTicks());
            percentage *= 100;
            for (int i = 10; i > 0; i--) {
                if (percentage >= 10) {
                    percentage -= 10;
                    barComponent.append(Component.literal(":").withStyle(ChatFormatting.GOLD));
                } else {
                    barComponent.append(Component.literal(":").withStyle(ChatFormatting.DARK_GRAY));
                }
            }
            barComponent.append(Component.literal("]").withStyle(ChatFormatting.GREEN));

            return Component.translatable(translatable, player.getScoreboardName(), barComponent).withStyle(ChatFormatting.GREEN);
        } else {
            return Component.translatable(translatable, player.getScoreboardName(),(playerData.getReviveCounter() / 20)).withStyle(ChatFormatting.GREEN);
        }
    }

    public static void broadcast(TheGame server, Component translationTextComponent){
        server.playerList().broadcastSystemMessage(translationTextComponent, false);
    }

    private static void resetDownTicks(Player player, IncapacitatedPlayerData playerData) {
        if (configData.isShouldDownTimeReset()) {
            playerData.setTicksUntilDeath(configData.getDownTicks());
            Services.writePlayerData(player, playerData);
            Services.sendIncapPacket((ServerPlayer) player, player.getId(), playerData.isIncapacitated(), (short) playerData.getDownsUntilDeath(), playerData.ticksUntilDeath);
        }
    }

    public static void shaderStuff(IncapacitatedComponent playerData) {
        if (FabricIncapacitated.lastDownDesaturate && playerData.getDownsUntilDeath() <= 0 && !playerData.isShader()) {
            ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("desaturate");
            ((LoadEffectInvoker) Minecraft.getInstance().gameRenderer).incapacitatedLoadEffect(resourceLocation);
            playerData.setShader(true);
        } else if ((!FabricIncapacitated.lastDownDesaturate || !(playerData.getDownsUntilDeath() <= 0)) && playerData.isShader()) {
            Minecraft.getInstance().gameRenderer.clearPostEffect();
            playerData.setShader(false);
        }
    }
}
