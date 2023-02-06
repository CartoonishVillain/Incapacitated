package com.cartoonishvillain.incapacitated.events;

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

import static com.cartoonishvillain.incapacitated.events.ForgeEvents.broadcast;

public class AbstractedIncapacitation {

    public static void downOrKill(Player player) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            //if the player is not already incapacitated
            if (!h.getIsIncapacitated()) {
                //reduce downs until death
                h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                //if downs until death is 0 or higher, we can cancel the death event because the user is down.
                if (h.getDownsUntilDeath() > -1) {
                    h.setIsIncapacitated(true);
                    player.setHealth(player.getMaxHealth());
                    if(Incapacitated.config.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) h.getDownsUntilDeath()), player);

                    if(Incapacitated.config.GLOBALINCAPMESSAGES.get()){
                        broadcast(player.getServer(), Component.literal(player.getScoreboardName() + " is incapacitated!"));
                    }
                    else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.literal(player.getScoreboardName() + " is incapacitated!"), false);
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
            if (!h.getIsIncapacitated()&& !(Incapacitated.config.SOMEINSTANTKILLS.get() && Incapacitated.instantKillDamageSourcesMessageID.contains(event.getSource().getMsgId()))) {
                //reduce downs until death
                h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                //if downs until death is 0 or higher, we can cancel the death event because the user is down.
                if (h.getDownsUntilDeath() > -1) {
                    h.setIsIncapacitated(true);
                    h.setSourceOfDeath(event.getSource());
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    if(Incapacitated.config.GLOWING.get())
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0));
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true, (short) h.getDownsUntilDeath()), player);

                    if(Incapacitated.config.GLOBALINCAPMESSAGES.get()){
                        broadcast(player.getServer(), Component.literal(player.getScoreboardName() + " is incapacitated!"));
                    }
                    else {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));
                        for (Player players : playerEntities) {
                            players.displayClientMessage(Component.literal(player.getScoreboardName() + " is incapacitated!"), false);
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
            IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false, (short) h.getDownsUntilDeath()), player);
            player.setHealth(player.getMaxHealth() / 3f);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);
        });
    }


    public static void setDownCount(Player player, short value) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            h.setDownsUntilDeath(value);
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
