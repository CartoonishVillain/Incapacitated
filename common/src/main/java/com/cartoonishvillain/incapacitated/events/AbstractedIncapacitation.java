package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static com.cartoonishvillain.incapacitated.Incapacitated.noMercyDamageSourcesMessageID;

public class AbstractedIncapacitation {

    public static void downOrKill(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated()) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!Services.PLATFORM.commonConfigUnlimitedDowns()) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    incapacitatedPlayerData.setIncapacitated(true);
                    player.setHealth(player.getMaxHealth());
                    if (Services.PLATFORM.commonConfigGlowing())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                    Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath());

                    if (Services.PLATFORM.commonConfigSlow()) {
                        player.addEffect(new MobEffectInstance(Services.PLATFORM.getSlowEffect(), -1, 6, true, false));
                    }

                    if (Services.PLATFORM.commonConfigWeak()) {
                        player.addEffect(new MobEffectInstance(Services.PLATFORM.getWeakEffect(), -1, 100, true, false));
                    }

                    if (Services.PLATFORM.commonConfigGlobalIncapMessage()) {
                        broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }
                    Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
                } else {
                    player.kill();
                }
            } else {
                player.kill();
            }
    }

    public static void downOrKill(Player player, CallbackInfo event, DamageSource damageSource) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated() && !(Services.PLATFORM.commonConfigSomeInstantKills())) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!Services.PLATFORM.commonConfigUnlimitedDowns()) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    incapacitatedPlayerData.setIncapacitated(true);
                    Services.PLATFORM.setDamageSource(player.level(), damageSource, player);
                    event.cancel();
                    player.setHealth(player.getMaxHealth());

                    if (Services.PLATFORM.commonConfigGlowing())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                    Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath());

                    if (Services.PLATFORM.commonConfigSlow()) {
                        player.addEffect(new MobEffectInstance(Services.PLATFORM.getSlowEffect(), -1, 6, true, false));
                    }

                    if (Services.PLATFORM.commonConfigWeak()) {
                        player.addEffect(new MobEffectInstance(Services.PLATFORM.getWeakEffect(), -1, 100, true, false));
                    }

                    if (Services.PLATFORM.commonConfigGlobalIncapMessage()) {
                        broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }
                    Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
                }
            } else if (!incapacitatedPlayerData.isIncapacitated() && (Services.PLATFORM.commonConfigSomeInstantKills())) {
                boolean notInstantKill = true;
                //check if the damage type is in the instant kill list, if it does, don't cancel KillPlayer event.
                for (String damageType : Incapacitated.instantKillDamageSourcesMessageID) {
                    if (damageType.contains(damageSource.getMsgId())) {
                        notInstantKill = false;
                    }
                }
                if (notInstantKill) {
                    //reduce downs until KillPlayer, unless unlimitedDowns is on.
                    if (!Services.PLATFORM.commonConfigUnlimitedDowns()) {
                        incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                    }
                    //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                    if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                        incapacitatedPlayerData.setIncapacitated(true);
                        Services.PLATFORM.setDamageSource(player.level(), damageSource, player);
                        event.cancel();
                        player.setHealth(player.getMaxHealth());
                        if (Services.PLATFORM.commonConfigGlowing())
                            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));

                        Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath());

                        if (Services.PLATFORM.commonConfigSlow()) {
                            player.addEffect(new MobEffectInstance(Services.PLATFORM.getSlowEffect(), -1, 6, true, false));
                        }

                        if (Services.PLATFORM.commonConfigWeak()) {
                            player.addEffect(new MobEffectInstance(Services.PLATFORM.getWeakEffect(), -1, 100, true, false));
                        }

                        if (Services.PLATFORM.commonConfigGlobalIncapMessage()) {
                            broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                        } else {
                            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                            for (Player players : playerEntities) {
                                players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                            }
                        }
                    }
                    Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
                }
            }
            else {
                player.kill();
            }
    }

    public static void pose(Player player, CallbackInfo ci, boolean cancellable) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
        if(incapacitatedPlayerData.isIncapacitated()) {
            player.setPose(Pose.SWIMMING);
            if (cancellable)
                ci.cancel();
        }
    }

    public static void revive(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
        incapacitatedPlayerData.setIncapacitated(false);
        incapacitatedPlayerData.setReviveCounter(Services.PLATFORM.commonConfigReviveTicks());
        player.removeEffect(MobEffects.GLOWING);
        player.removeEffect(Services.PLATFORM.getSlowEffect());
        player.removeEffect(Services.PLATFORM.getWeakEffect());
        Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
        if (!player.level().isClientSide) {
            Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), false, (short) incapacitatedPlayerData.getDownsUntilDeath());
        }
        player.setHealth(player.getMaxHealth() / 3f);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);

        if (Services.PLATFORM.commonConfigGlobalReviveMessage()) {
                broadcast(player.getServer(), Component.translatable("message.revive.message", player.getScoreboardName()));
            } else {
                ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                for (Player players : playerEntities) {
                    players.displayClientMessage(Component.translatable("message.revive.message", player.getScoreboardName()), false);
                }
            }

            if (Services.PLATFORM.commonConfigGlobalReviveMessage() && !Services.PLATFORM.commonConfigUnlimitedDowns()) {
                if (incapacitatedPlayerData.getDownsUntilDeath() > 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.normal", incapacitatedPlayerData.getDownsUntilDeath()), false);
                } else if (incapacitatedPlayerData.getDownsUntilDeath() == 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.one"), false);
                } else {
                    player.displayClientMessage(Component.translatable("message.revivecount.zero"), false);
                }
            }
    }


    public static void setDownCount(Player player, short value) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
        incapacitatedPlayerData.setDownsUntilDeath(value);
        Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
        Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), incapacitatedPlayerData.isIncapacitated(), (short) incapacitatedPlayerData.getDownsUntilDeath());
    }

    public static short getDownCount(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
        return (short) incapacitatedPlayerData.getDownsUntilDeath();
    }

    public static void eat(LivingEntity entity, ItemStack itemStack){
        if(entity instanceof Player player && !entity.level().isClientSide()){
            Item item = itemStack.getItem();
            IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(player);
            if(Incapacitated.HealingFoods.contains(item.toString())) {
                incapacitatedPlayerData.setDownsUntilDeath(Services.PLATFORM.commonConfigDownCount());
                incapacitatedPlayerData.setTicksUntilDeath(Services.PLATFORM.commonConfigDownTicks());
            }

            if(incapacitatedPlayerData.isIncapacitated()) {
                if(Incapacitated.ReviveFoods.contains(item.toString())){
                    incapacitatedPlayerData.setIncapacitated(false);
                    incapacitatedPlayerData.setReviveCounter(Services.PLATFORM.commonConfigReviveTicks());
                    incapacitatedPlayerData.setDownsUntilDeath(Services.PLATFORM.commonConfigDownCount());
                    incapacitatedPlayerData.setTicksUntilDeath(Services.PLATFORM.commonConfigDownTicks());
                    player.removeEffect(MobEffects.GLOWING);
                    player.setHealth(player.getMaxHealth()/3f);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);
                }
            } else if(Incapacitated.ReviveFoods.contains(item.toString())) {
                incapacitatedPlayerData.setDownsUntilDeath(Services.PLATFORM.commonConfigDownCount());
                incapacitatedPlayerData.setTicksUntilDeath(Services.PLATFORM.commonConfigDownTicks());
            }
            Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), incapacitatedPlayerData.isIncapacitated(), (short) incapacitatedPlayerData.getDownsUntilDeath());
            Services.PLATFORM.writePlayerData(player, incapacitatedPlayerData);
        }
    }

    public static void hurt(Player player, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, float amount){
        IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData(player);
        if (data.isIncapacitated() && Services.PLATFORM.commonConfigMerciful() > 0 && !(damageSource.getMsgId().equals("bleedout"))) {
            if (Services.PLATFORM.commonConfigMerciful() == 1 && !player.level().isClientSide) {
                int timeToRemove = (int) amount;
                if (timeToRemove > 2000) timeToRemove = 2000;
                data.setTicksUntilDeath(data.getTicksUntilDeath() - timeToRemove);
                Services.PLATFORM.sendIncapPacket((ServerPlayer) player, player.getId(), data.isIncapacitated(), (short) data.getDownsUntilDeath(), data.getTicksUntilDeath());
                Services.PLATFORM.writePlayerData(player, data);
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
        IncapacitatedPlayerData playerData = Services.PLATFORM.getPlayerData(downPlayer);

        //If the player is down, run all the code associated every tick, otherwise don't.
        if(playerData.isIncapacitated()) {
            downPlayer.setPose(Pose.SWIMMING);
            if (!downPlayer.level().isClientSide) {
                //Scan for any players nearby
                ArrayList<Player> playerEntities = (ArrayList<Player>) downPlayer.level().getEntitiesOfClass(Player.class, downPlayer.getBoundingBox().inflate(3));
                boolean reviving = false;
                Player revivingPlayer = null;

                //Loop through nearby players to check if any are reviving the downed player successfully
                for (Player player : playerEntities) {
                    boolean isdown;
                    IncapacitatedPlayerData potentialHeroData = Services.PLATFORM.getPlayerData(player);
                    isdown = potentialHeroData.isIncapacitated();

                    //Since we are here, we know the event player is down. So if a nearby player is crouching and not down themselves, we set the reviving state and
                    //mark the reviving player.
                    if (player.isCrouching() && !isdown) {
                        reviving = true;
                        revivingPlayer = player;
                    }
                }

                //If our event player is actively being revived.
                if (reviving) {
                    //Count down the revive timer. Returns true if the timer is 0, at which point the player is revived.
                    if (playerData.downReviveCount()) {
                        revive(downPlayer);
                    } else {
                        //If the timer is not 0 on the revive timer, tell both parties that the revive is occuring, and how much longer until it is done.
                        if (!Services.PLATFORM.commonConfigUseSeconds()) {
                            downPlayer.displayClientMessage(revivingComponent(playerData, "message.downindicator.revivingbar"), true);
                            revivingPlayer.displayClientMessage(revivingComponent(playerData, "message.reviveindicator.revivingbar", downPlayer), true);
                        } else {
                            downPlayer.displayClientMessage(revivingComponent(playerData, "message.downindicator.reviving"), true);
                            revivingPlayer.displayClientMessage(revivingComponent(playerData, "message.reviveindicator.reviving", downPlayer), true);
                        }
                        Services.PLATFORM.writePlayerData(downPlayer, playerData);
                    }
                } else {
                    //If our event player is not being revived, count down the timer until; their death. Returns true when the player runs out of time.
                    if (playerData.countTicksUntilDeath()) {
                        
                        downPlayer.hurt(Services.PLATFORM.getDamageSource(downPlayer, downPlayer.level()), Float.MAX_VALUE);
                        playerData.setReviveCounter(Services.PLATFORM.commonConfigReviveTicks());
                        downPlayer.removeEffect(MobEffects.GLOWING);
                        playerData.setIncapacitated(false);
                        Services.PLATFORM.writePlayerData(downPlayer, playerData);
                        Services.PLATFORM.sendIncapPacket((ServerPlayer) downPlayer, downPlayer.getId(), false, (short) playerData.getDownsUntilDeath());
                    } else if (playerData.getTicksUntilDeath() % 20 == 0) {
                        //Otherwise, every 20 ticks (1 second) send the dying player a message about how long, in seconds, they have until death.
                        downPlayer.displayClientMessage(Component.translatable("message.downindicator.norevive", "/incap die", playerData.getTicksUntilDeath() / 20f).withStyle(ChatFormatting.RED), true);
                    }

                    //Additionally, if the user is not reviving, make sure the revive timer is reset.
                    if (playerData.getReviveCounter() != Services.PLATFORM.commonConfigReviveTicks()) {
                        playerData.setReviveCounter(Services.PLATFORM.commonConfigReviveTicks());
                    }
                    Services.PLATFORM.writePlayerData(downPlayer, playerData);
                }
            }
        }
    }

    public static void downLogging(Player player) {
        if (Services.PLATFORM.commonConfigDownLogging()) {
            IncapacitatedPlayerData playerData = Services.PLATFORM.getPlayerData(player);
            if (playerData.isIncapacitated()) {
                downOrKill(player);
            }
        }
    }

    public static void sleep(Player player, boolean wakeImmediately, boolean updateLevel) {
        if (!updateLevel && !wakeImmediately && Services.PLATFORM.commonConfigRegenerating()) {
            IncapacitatedPlayerData playerData = Services.PLATFORM.getPlayerData(player);
            if (playerData.getDownsUntilDeath() < Services.PLATFORM.commonConfigDownCount()) {
                AbstractedIncapacitation.setDownCount(player, (short) (playerData.getDownsUntilDeath() + 1));
            }
        }
    }

    private static MutableComponent revivingComponent(IncapacitatedPlayerData playerData, String translatable) {
        if (!Services.PLATFORM.commonConfigUseSeconds()) {
            MutableComponent barComponent = Component.literal("[").withStyle(ChatFormatting.GREEN);
            float percentage = 1 - ((float)playerData.getReviveCounter()/(float)Services.PLATFORM.commonConfigReviveTicks());
            percentage *= 100;
//            LOGGER.debug("Config amount: " + IncapacitatedCommonConfig.REVIVETICKS.get() + " current revive counter: " + playerData.getReviveCounter() + " calculated: " + (100 - (playerData.getReviveCounter()/IncapacitatedCommonConfig.REVIVETICKS.get())));
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
        if (!Services.PLATFORM.commonConfigUseSeconds()) {
            MutableComponent barComponent = Component.literal("[").withStyle(ChatFormatting.GREEN);
            float percentage = 1 - ((float)playerData.getReviveCounter()/(float)Services.PLATFORM.commonConfigReviveTicks());
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

    public static void broadcast(MinecraftServer server, Component translationTextComponent){
        server.getPlayerList().broadcastSystemMessage(translationTextComponent, false);
    }

}
