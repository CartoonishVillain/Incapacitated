package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapabilityManager;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.incapacitationMessenger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Mod.EventBusSubscriber(modid = Incapacitated.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void playerRegister(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof PlayerEntity){
            PlayerCapabilityManager provider = new PlayerCapabilityManager();
            event.addCapability(new ResourceLocation(Incapacitated.MODID, "incapacitated"), provider);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && !event.isCanceled()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
                //if the player is not already incapacitated
                if (!h.getIsIncapacitated()&& !(Incapacitated.config.SOMEINSTANTKILLS.get() && Incapacitated.instantKillDamageSourcesMessageID.contains(event.getSource().getMsgId()))) {
                    //reduce downs until death
                    h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                    //if downs until death is 0 or higher, we can cancel the death event because the user is down.
                    if (h.getDownsUntilDeath() > -1) {
                        h.setIsIncapacitated(true);
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        if(Incapacitated.config.GLOWING.get())
                            player.addEffect(new EffectInstance(Effects.GLOWING, Integer.MAX_VALUE, 0));
                        incapacitationMessenger.sendTo(new IncapPacket(player.getId(), true), player);

                        if(Incapacitated.config.GLOBALINCAPMESSAGES.get()){
                            broadcast(player.getServer(), new StringTextComponent(player.getScoreboardName() + " is incapacitated!"));
                        }
                        else {
                            ArrayList<PlayerEntity> playerEntities = (ArrayList<PlayerEntity>) player.level.getEntitiesOfClass(PlayerEntity.class, player.getBoundingBox().inflate(50));
                            for (PlayerEntity players : playerEntities) {
                                players.displayClientMessage(new StringTextComponent(player.getScoreboardName() + " is incapacitated!"), false);
                            }
                        }

                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void PlayerJoinEvent(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof PlayerEntity && !event.getWorld().isClientSide()){
            PlayerEntity player = (PlayerEntity) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                incapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated()), player);
            });
        }
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event){
        if(!event.isWasDeath()){
            PlayerEntity originalPlayer = event.getOriginal();
            PlayerEntity newPlayer = event.getPlayer();

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
    public static void playerTick(TickEvent.PlayerTickEvent event){
        event.player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
            h.countDelay();
            if(h.getIsIncapacitated()) {
                if(event.player.getForcedPose() == null){event.player.setForcedPose(Pose.SWIMMING);}
                if(!event.player.level.isClientSide()) {
                    ArrayList<PlayerEntity> playerEntities = (ArrayList<PlayerEntity>) event.player.level.getEntitiesOfClass(PlayerEntity.class, event.player.getBoundingBox().inflate(3));
                    boolean reviving = false;

                    PlayerEntity revivingPlayer = null;
                    for(PlayerEntity player : playerEntities) {
                        AtomicBoolean isTargetDown = new AtomicBoolean(false);
                        AtomicBoolean isReviverDown = new AtomicBoolean(false);
                        event.player.getCapability(PlayerCapability.INSTANCE).ifPresent(j->{
                            isTargetDown.set(j.getIsIncapacitated());
                        });
                        player.getCapability(PlayerCapability.INSTANCE).ifPresent(j->{
                            isReviverDown.set(j.getIsIncapacitated());
                        });
                        if (player.isCrouching() && isTargetDown.get() && !isReviverDown.get()) {
                            reviving = true;
                            revivingPlayer = player;
                            break;
                        }
                    }

                    if(reviving){
                        if(h.downReviveCount()){
                            h.setIsIncapacitated(false);
                            event.player.setForcedPose(null);
                            h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                            h.setJumpCount(3);
                            event.player.removeEffect(Effects.GLOWING);
                            incapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                            event.player.setHealth(event.player.getMaxHealth()/3f);
                            event.player.level.playSound(null, event.player.getX(), event.player.getY(), event.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1, 1);

                        }else{
                            event.player.displayClientMessage(new StringTextComponent("You are being revived! " + (int)(h.getReviveCount()/20) + " seconds..").withStyle(TextFormatting.GREEN), true);
                            revivingPlayer.displayClientMessage(new StringTextComponent("Reviving " + event.player.getScoreboardName() + " " + (int)(h.getReviveCount()/20) + " seconds...").withStyle(TextFormatting.GREEN), true);
                        }
                    }
                    else {
                        if (h.countTicksUntilDeath()) {
                            event.player.hurt(BleedOutDamage.playerOutOfTime(event.player), event.player.getMaxHealth() *2);
                            event.player.setForcedPose(null);
                            h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                            h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());
                            h.setJumpCount(3);
                            event.player.removeEffect(Effects.GLOWING);
                            h.setIsIncapacitated(false);
                            incapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                        } else if (h.getTicksUntilDeath() % 20 == 0) {
                            event.player.displayClientMessage(new StringTextComponent("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(TextFormatting.RED), true);
                        }

                        if(h.getReviveCount() != Incapacitated.config.REVIVETICKS.get()) h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                    }
                }
            }
            else{
                if(event.player.getForcedPose() != null) event.player.setForcedPose(null);
            }
        });
    }

    @SubscribeEvent
    public static void playerJump(LivingEvent.LivingJumpEvent event){
        if(event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide()){
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h ->{
                if(h.getIsIncapacitated() && h.getJumpDelay() == 0){
                    if(h.giveUpJumpCount()){
                        player.hurt(BleedOutDamage.playerOutOfTime(player), player.getMaxHealth() *2);
                        player.setForcedPose(null);
                        h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                        h.setJumpCount(3);
                        player.removeEffect(Effects.GLOWING);
                        h.setIsIncapacitated(false);
                        incapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerInjured(LivingHurtEvent event){
        if(event.getEntityLiving() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(h.getIsIncapacitated() && Incapacitated.config.INVINCIBLEDOWN.get() && !(event.getSource().getMsgId().equals("giveup") || event.getSource().getMsgId().equals("bleedout"))){
                    event.setCanceled(true);
                    return;
                }
                h.setJumpDelay(20);
            });
        }
    }


    @SubscribeEvent
    public static void playerEat(LivingEntityUseItemEvent.Finish event){
        if(event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide()){
            Item item = event.getItem().getItem();
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(Incapacitated.HealingFoods.contains(item.getRegistryName())) {h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get()); h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());}
                if(h.getIsIncapacitated()){
                    if(Incapacitated.ReviveFoods.contains(item.getRegistryName())){
                        h.setIsIncapacitated(false);
                        player.setForcedPose(null);
                        h.setReviveCount(Incapacitated.config.REVIVETICKS.get());
                        h.setJumpCount(3);
                        h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());
                        h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get());
                        player.removeEffect(Effects.GLOWING);
                        incapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                        player.setHealth(player.getMaxHealth()/3f);
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1, 1);
                    }
                }
                else if(Incapacitated.ReviveFoods.contains(item.getRegistryName())) {h.setDownsUntilDeath(Incapacitated.config.DOWNCOUNT.get()); h.setTicksUntilDeath(Incapacitated.config.DOWNTICKS.get());}
            });
        }
    }

    private static void broadcast(MinecraftServer server, StringTextComponent translationTextComponent){
        server.getPlayerList().broadcastMessage(translationTextComponent, ChatType.CHAT, UUID.randomUUID());
    }

}
