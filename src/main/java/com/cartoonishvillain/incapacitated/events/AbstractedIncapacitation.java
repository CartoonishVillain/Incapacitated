package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.IncapEffects;
import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import java.util.ArrayList;
import static com.cartoonishvillain.incapacitated.Incapacitated.*;
import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;
import static com.cartoonishvillain.incapacitated.events.ForgeEvents.broadcast;

public class AbstractedIncapacitation {

    public static void downOrKill(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = player.getData(INCAP_DATA);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated()) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!unlimitedDowns) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    incapacitatedPlayerData.setIncapacitated(true);
                    player.setHealth(player.getMaxHealth());
                    if (IncapacitatedCommonConfig.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath()), player);

                    if (slow) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapSlow.get(), -1, 6, true, false));
                    }

                    if (weakened) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapWeak.get(), -1, 100, true, false));
                    }

                    if (IncapacitatedCommonConfig.GLOBALINCAPMESSAGES.get()) {
                        broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }
                } else {
                    player.kill();
                }
            } else {
                player.kill();
            }
    }

    public static void downOrKill(Player player, LivingDeathEvent event) {
        IncapacitatedPlayerData incapacitatedPlayerData = player.getData(INCAP_DATA);
            //if the player is not already incapacitated
            if (!incapacitatedPlayerData.isIncapacitated() && !(IncapacitatedCommonConfig.SOMEINSTANTKILLS.get())) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!unlimitedDowns) {
                    incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                    incapacitatedPlayerData.setIncapacitated(true);
                    incapacitatedPlayerData.setDamageSource(player.level(), event.getSource());
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    if (IncapacitatedCommonConfig.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath()), player);

                    if (slow) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapSlow.get(), -1, 6, true, false));
                    }

                    if (weakened) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapWeak.get(), -1, 100, true, false));
                    }

                    if (IncapacitatedCommonConfig.GLOBALINCAPMESSAGES.get()) {
                        broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }

                }
            } else if (!incapacitatedPlayerData.isIncapacitated() && (IncapacitatedCommonConfig.SOMEINSTANTKILLS.get())) {
                boolean notInstantKill = true;
                //check if the damage type is in the instant kill list, if it does, don't cancel KillPlayer event.
                for (String damageType : Incapacitated.instantKillDamageSourcesMessageID) {
                    if (damageType.contains(event.getSource().getMsgId())) {
                        notInstantKill = false;
                    }
                }
                if (notInstantKill) {
                    //reduce downs until KillPlayer, unless unlimitedDowns is on.
                    if (!unlimitedDowns) {
                        incapacitatedPlayerData.setDownsUntilDeath(incapacitatedPlayerData.getDownsUntilDeath() - 1);
                    }
                    //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                    if (incapacitatedPlayerData.getDownsUntilDeath() > -1) {
                        incapacitatedPlayerData.setIncapacitated(true);
                        incapacitatedPlayerData.setDamageSource(player.level(), event.getSource());
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        if (IncapacitatedCommonConfig.GLOWING.get())
                            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) incapacitatedPlayerData.getDownsUntilDeath()), player);

                        if (slow) {
                            player.addEffect(new MobEffectInstance(IncapEffects.incapSlow.get(), -1, 6, true, false));
                        }

                        if (weakened) {
                            player.addEffect(new MobEffectInstance(IncapEffects.incapWeak.get(), -1, 100, true, false));
                        }

                        if (IncapacitatedCommonConfig.GLOBALINCAPMESSAGES.get()) {
                            broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                        } else {
                            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                            for (Player players : playerEntities) {
                                players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                            }
                        }

                    }
                    player.setData(INCAP_DATA, incapacitatedPlayerData);
                }
            }
            else {
                player.kill();
            }
    }

    public static void revive(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = player.getData(INCAP_DATA);
        incapacitatedPlayerData.setIncapacitated(false);
        player.setForcedPose(null);
        incapacitatedPlayerData.setReviveCounter(IncapacitatedCommonConfig.REVIVETICKS.get());
        player.removeEffect(MobEffects.GLOWING);
        player.removeEffect(IncapEffects.incapSlow.get());
        player.removeEffect(IncapEffects.incapWeak.get());
        player.setData(INCAP_DATA, incapacitatedPlayerData);
        if (!player.level().isClientSide) {
            IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false, (short) incapacitatedPlayerData.getDownsUntilDeath()), player);
        }
        player.setHealth(player.getMaxHealth() / 3f);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);

        if (IncapacitatedCommonConfig.GLOBALREVIVEMESSAGES.get()) {
                broadcast(player.getServer(), Component.translatable("message.revive.message", player.getScoreboardName()));
            } else {
                ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                for (Player players : playerEntities) {
                    players.displayClientMessage(Component.translatable("message.revive.message", player.getScoreboardName()), false);
                }
            }

            if (IncapacitatedCommonConfig.REVIVE_MESSAGE.get() && !unlimitedDowns) {
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
        IncapacitatedPlayerData incapacitatedPlayerData = player.getData(INCAP_DATA);
        incapacitatedPlayerData.setDownsUntilDeath(value);
        player.setData(INCAP_DATA, incapacitatedPlayerData);
        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), incapacitatedPlayerData.isIncapacitated(), (short) incapacitatedPlayerData.getDownsUntilDeath()), player);
    }

    public static short getDownCount(Player player) {
        IncapacitatedPlayerData incapacitatedPlayerData = player.getData(INCAP_DATA);
        return (short) incapacitatedPlayerData.getDownsUntilDeath();
    }

}
