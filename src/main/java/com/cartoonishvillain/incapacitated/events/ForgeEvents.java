package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapabilityManager;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Mod.EventBusSubscriber(modid = Incapacitated.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void playerRegister(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            PlayerCapabilityManager provider = new PlayerCapabilityManager();
            event.addCapability(new ResourceLocation(Incapacitated.MODID, "incapacitated"), provider);
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !event.isCanceled()) {
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
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true), player);

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
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event){
        if(!event.isWasDeath()){
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            AtomicBoolean incapacitated = new AtomicBoolean(false);
            AtomicInteger ticksUntilDeath = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger downsUntilDeath = new AtomicInteger(Integer.MAX_VALUE);

            originalPlayer.revive();

            originalPlayer.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                incapacitated.set(h.getIsIncapacitated());
                ticksUntilDeath.set(h.getTicksUntilDeath());
                downsUntilDeath.set(h.getDownsUntilDeath());
            });

            originalPlayer.kill();

            newPlayer.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                h.setIsIncapacitated(incapacitated.get());
                h.setTicksUntilDeath(ticksUntilDeath.get());
                h.setDownsUntilDeath(downsUntilDeath.get());
            });
        }
    }

    @SubscribeEvent
    public static void PlayerJoinEvent(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player && !event.getLevel().isClientSide()){
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated()), player);
            });
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            event.player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                h.countDelay();
                if (h.getIsIncapacitated()) {
                    if (event.player.getForcedPose() == null) {
                        event.player.setForcedPose(Pose.SWIMMING);
                    }
                    if (!event.player.level.isClientSide()) {
                        ArrayList<Player> playerEntities = (ArrayList<Player>) event.player.level.getEntitiesOfClass(Player.class, event.player.getBoundingBox().inflate(3));
                        boolean reviving = false;

                        Player revivingPlayer = null;
                        for (Player player : playerEntities) {
                            AtomicBoolean isdown = new AtomicBoolean(false);
                            AtomicBoolean targetIsDown = new AtomicBoolean(false);
                            player.getCapability(PlayerCapability.INSTANCE).ifPresent(j -> {
                                isdown.set(j.getIsIncapacitated());
                            });
                            event.player.getCapability(PlayerCapability.INSTANCE).ifPresent(j -> {
                                targetIsDown.set(j.getIsIncapacitated());
                            });
                            if (player.isCrouching() && targetIsDown.get() && !isdown.get()) {
                                reviving = true;
                                revivingPlayer = player;
                                break;
                            }
                        }

                        if (reviving) {
                            if (h.downReviveCount()) {
                                h.setIsIncapacitated(false);
                                event.player.setForcedPose(null);
                                h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                                h.resetGiveUpJumps();
                                event.player.removeEffect(MobEffects.GLOWING);
                                IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                                event.player.setHealth(event.player.getMaxHealth() / 3f);
                                event.player.level.playSound(null, event.player.getX(), event.player.getY(), event.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);

                            } else {
                                event.player.displayClientMessage(Component.literal("You are being revived! " + (int) (h.getReviveCount() / 20) + " seconds..").withStyle(ChatFormatting.GREEN), true);
                                revivingPlayer.displayClientMessage(Component.literal("Reviving " + event.player.getScoreboardName() + " " + (int) (h.getReviveCount() / 20) + " seconds...").withStyle(ChatFormatting.GREEN), true);
                            }
                        } else {
                            if (h.countTicksUntilDeath()) {
                                event.player.hurt(h.getSourceOfDeath(), event.player.getMaxHealth() * 10);
                                event.player.setForcedPose(null);
                                h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                                h.resetGiveUpJumps();
                                event.player.removeEffect(MobEffects.GLOWING);
                                h.setIsIncapacitated(false);
                                IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                            } else if (h.getTicksUntilDeath() % 20 == 0) {
                                event.player.displayClientMessage(Component.literal("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(ChatFormatting.RED), true);
                            }

                            if (h.getReviveCount() != Incapacitated.config.REVIVETICKS.get())
                                h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                        }
                    }
                } else {
                    if (event.player.getForcedPose() != null) event.player.setForcedPose(null);
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerJump(LivingEvent.LivingJumpEvent event){
        if(event.getEntity() instanceof Player && !event.getEntity().level.isClientSide()){
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h ->{
                if(h.getIsIncapacitated() && h.getJumpDelay() == 0){
                    if(h.giveUpJumpCount()){
                        player.hurt(h.getSourceOfDeath(), player.getMaxHealth() * 10);
                        player.setForcedPose(null);
                        h.setReviveCount(Incapacitated.config.DOWNCOUNT.get());
                        h.resetGiveUpJumps();
                        h.setIsIncapacitated(false);
                        player.removeEffect(MobEffects.GLOWING);
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerInjured(LivingHurtEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(h.getIsIncapacitated() && Incapacitated.config.INVINCIBLEDOWN.get() && !(event.getSource().getMsgId().equals("bleedout"))){
                    event.setCanceled(true);
                    return;
                }
                h.setJumpDelay(20);
            });
        }
    }

    @SubscribeEvent
    public static void playerEat(LivingEntityUseItemEvent.Finish event){
        if(event.getEntity() instanceof Player && !event.getEntity().level.isClientSide()){
            Item item = event.getItem().getItem();
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(Incapacitated.HealingFoods.contains(item.toString())) {h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get()); h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());}
                if(h.getIsIncapacitated()){
                    if(Incapacitated.ReviveFoods.contains(item.toString())){
                        h.setIsIncapacitated(false);
                        player.setForcedPose(null);
                        h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                        h.resetGiveUpJumps();
                        h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get());
                        player.removeEffect(MobEffects.GLOWING);
                        h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                        player.setHealth(player.getMaxHealth()/3f);
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);
                    }
                }else if(Incapacitated.ReviveFoods.contains(item.toString())) {h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get()); h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());}
            });
        }
    }

    private static void broadcast(MinecraftServer server, Component translationTextComponent){
        server.getPlayerList().broadcastSystemMessage(translationTextComponent, false);
    }




}
