package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapabilityManager;
import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cartoonishvillain.incapacitated.Incapacitated.*;
import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.downOrKill;
import static com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation.revive;


@Mod.EventBusSubscriber(modid = Incapacitated.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event){
        SetIncapacitatedCommand.register(event.getDispatcher());
        SetDownCount.register(event.getDispatcher());
        GetDownCount.register(event.getDispatcher());
        KillPlayer.register(event.getDispatcher());

        if(!FMLLoader.isProduction()) {
            IncapDevMode.register(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public static void playerRegister(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            PlayerCapabilityManager provider = new PlayerCapabilityManager();
            event.addCapability(new ResourceLocation(Incapacitated.MODID, "incapacitated"), provider);
        }
    }

    @SubscribeEvent
    public static void playerHurtCheck(LivingHurtEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            //Merciful check
            event.getEntity().getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                if (h.getIsIncapacitated() && merciful && !(event.getSource().getMsgId().equals("bleedout"))) {
                    event.setAmount(0);
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerKillCheck(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer && !(event.getEntity() instanceof Player) && hunter) {
            event.getSource().getEntity().getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                if (h.getIsIncapacitated()) {
                    revive((Player) event.getSource().getEntity());
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !event.isCanceled()) {
            downOrKill(player, event);
        }
    }

    @SubscribeEvent
    public static void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (downLogging && event.getEntity() instanceof ServerPlayer) {
            event.getEntity().getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                if (h.getIsIncapacitated()) {
                   event.getEntity().kill();
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
        if(event.getEntity() instanceof Player player && !event.getLevel().isClientSide()){
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated(), (short) h.getDownsUntilDeath()), player);
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

                        if (devMode) {
                            if(h.getJumpCount() == 2) {
                                revive(event.player);
                            }
                        } else {
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
                                revive(event.player);
                            } else {
                                event.player.displayClientMessage(Component.translatable("message.downindicator.reviving", (h.getReviveCount() / 20)).withStyle(ChatFormatting.GREEN), true);
                                revivingPlayer.displayClientMessage(Component.translatable("message.reviveindicator.reviving", event.player.getScoreboardName(), (h.getReviveCount() / 20)).withStyle(ChatFormatting.GREEN), true);
                            }
                        } else {
                            if (h.countTicksUntilDeath()) {
                                event.player.hurt(h.getSourceOfDeath(), event.player.getMaxHealth() * 10);
                                event.player.setForcedPose(null);
                                h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                                h.resetGiveUpJumps();
                                event.player.removeEffect(MobEffects.GLOWING);
                                h.setIsIncapacitated(false);
                                IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false, (short) h.getDownsUntilDeath()), event.player);
                            } else if (h.getTicksUntilDeath() % 20 == 0) {
                                event.player.displayClientMessage(Component.translatable("message.downindicator.norevive", "/incap die", h.getTicksUntilDeath() / 20f).withStyle(ChatFormatting.RED), true);
                            }

                            if (h.getReviveCount() != Incapacitated.config.REVIVETICKS.get())
                                h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                        }
                    }
                    }
                } else {
                    if (event.player.getForcedPose() != null) event.player.setForcedPose(null);
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerRested(PlayerWakeUpEvent event) {
        if(!event.updateLevel() && !event.wakeImmediately() && regenerating) {
            event.getEntity().getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                if (h.getDownsUntilDeath() < config.DOWNCOUNT.get()) {
                    AbstractedIncapacitation.setDownCount(event.getEntity(), (short) (h.getDownsUntilDeath()+1));
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerEat(LivingEntityUseItemEvent.Finish event){
        if(event.getEntity() instanceof Player && !event.getEntity().level.isClientSide()){
            Item item = event.getItem().getItem();
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(Incapacitated.HealingFoods.contains(item.toString())) {
                    h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get());
                    h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated(), (short) h.getDownsUntilDeath()), player);
                }
                if(h.getIsIncapacitated()){
                    if(Incapacitated.ReviveFoods.contains(item.toString())){
                        h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get());
                        h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());
                        revive(player);
                    }
                }
            });
        }
    }

    public static void broadcast(MinecraftServer server, Component translationTextComponent){
        server.getPlayerList().broadcastSystemMessage(translationTextComponent, false);
    }




}
