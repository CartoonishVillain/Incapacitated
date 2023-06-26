package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.IncapEffects;
import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cartoonishvillain.incapacitated.Incapacitated.*;
import static com.cartoonishvillain.incapacitated.events.ForgeEvents.broadcast;

public class AbstractedIncapacitation {

    public static void downOrKill(Player player) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            //if the player is not already incapacitated
            if (!h.getIsIncapacitated()) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!unlimitedDowns) {
                    h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (h.getDownsUntilDeath() > -1) {
                    h.setIsIncapacitated(true);
                    player.setHealth(player.getMaxHealth());
                    if (Incapacitated.config.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) h.getDownsUntilDeath()), player);

                    if (slow) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapSlow, -1, 6, true, false));
                    }

                    if (weakened) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapWeak, -1, 100, true, false));
                    }

                    if (Incapacitated.config.GLOBALINCAPMESSAGES.get()) {
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
            }
        });
    }

    public static void downOrKill(Player player, LivingDeathEvent event) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            //if the player is not already incapacitated
            if (!h.getIsIncapacitated() && !(Incapacitated.config.SOMEINSTANTKILLS.get())) {
                //reduce downs until KillPlayer, unless unlimitedDowns is on.
                if (!unlimitedDowns) {
                    h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                }
                //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                if (h.getDownsUntilDeath() > -1) {
                    h.setIsIncapacitated(true);
                    h.setSourceOfDeath(player.level(), event.getSource());
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    if (Incapacitated.config.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) h.getDownsUntilDeath()), player);

                    if (slow) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapSlow, -1, 6, true, false));
                    }

                    if (weakened) {
                        player.addEffect(new MobEffectInstance(IncapEffects.incapWeak, -1, 100, true, false));
                    }

                    if (Incapacitated.config.GLOBALINCAPMESSAGES.get()) {
                        broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                    } else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                        }
                    }

                }
            } else if (!h.getIsIncapacitated() && (config.SOMEINSTANTKILLS.get())) {
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
                        h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                    }
                    //if downs until KillPlayer is 0 or higher, we can cancel the KillPlayer event because the user is down.
                    if (h.getDownsUntilDeath() > -1) {
                        h.setIsIncapacitated(true);
                        h.setSourceOfDeath(player.level(), event.getSource());
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        if (Incapacitated.config.GLOWING.get())
                            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, true, false));
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) h.getDownsUntilDeath()), player);

                        if (slow) {
                            player.addEffect(new MobEffectInstance(IncapEffects.incapSlow, -1, 6, true, false));
                        }

                        if (weakened) {
                            player.addEffect(new MobEffectInstance(IncapEffects.incapWeak, -1, 100, true, false));
                        }

                        if (Incapacitated.config.GLOBALINCAPMESSAGES.get()) {
                            broadcast(player.getServer(), Component.translatable("message.incap.message", player.getScoreboardName()));
                        } else {
                            ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                            for (Player players : playerEntities) {
                                players.displayClientMessage(Component.translatable("message.incap.message", player.getScoreboardName()), false);
                            }
                        }

                    }
                }
            }
        });
    }

    public static void revive(Player player) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            h.setIsIncapacitated(false);
            player.setForcedPose(null);
            h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
            h.resetGiveUpJumps();
            player.removeEffect(MobEffects.GLOWING);
            player.removeEffect(IncapEffects.incapSlow);
            player.removeEffect(IncapEffects.incapWeak);
            IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false, (short) h.getDownsUntilDeath()), player);
            player.setHealth(player.getMaxHealth() / 3f);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1, 1);

            if (config.GLOBALREVIVEMESSAGES.get()) {
                broadcast(player.getServer(), Component.translatable("message.revive.message", player.getScoreboardName()));
            } else {
                ArrayList<Player> playerEntities = (ArrayList<Player>) player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                for (Player players : playerEntities) {
                    players.displayClientMessage(Component.translatable("message.revive.message", player.getScoreboardName()), false);
                }
            }

            if (config.REVIVE_MESSAGE.get() && !unlimitedDowns) {
                if (h.getDownsUntilDeath() > 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.normal", h.getDownsUntilDeath()), false);
                } else if (h.getDownsUntilDeath() == 1) {
                    player.displayClientMessage(Component.translatable("message.revivecount.one"), false);
                } else {
                    player.displayClientMessage(Component.translatable("message.revivecount.zero"), false);
                }
            }

        });
    }


    public static void setDownCount(Player player, short value) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            h.setDownsUntilDeath(value);
            IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated(), (short) h.getDownsUntilDeath()), player);
        });
    }

    public static short getDownCount(Player player) {
        AtomicInteger integer = new AtomicInteger(0);
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            integer.set(h.getDownsUntilDeath());
        });
        return (short) integer.get();
    }

}
