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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;


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
                if (!h.getIsIncapacitated()) {
                    //reduce downs until death
                    h.setDownsUntilDeath(h.getDownsUntilDeath() - 1);
                    //if downs until death is 0 or higher, we can cancel the death event because the user is down.
                    if (h.getDownsUntilDeath() > -1) {
                        h.setIsIncapacitated(true);
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        incapacitationMessenger.sendTo(new IncapPacket(player.getId(), true), player);

                        ArrayList<PlayerEntity> playerEntities = (ArrayList<PlayerEntity>) player.level.getEntitiesOfClass(PlayerEntity.class, player.getBoundingBox().inflate(50));

                        for(PlayerEntity players : playerEntities) {
                            players.displayClientMessage(new StringTextComponent(player.getScoreboardName() + " is incapacitated!"), false);
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
    public static void playerTick(TickEvent.PlayerTickEvent event){
        event.player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
            if(h.getIsIncapacitated()) {
                if(event.player.getForcedPose() == null){event.player.setForcedPose(Pose.SWIMMING);}
                if(!event.player.level.isClientSide()) {
                    ArrayList<PlayerEntity> playerEntities = (ArrayList<PlayerEntity>) event.player.level.getEntitiesOfClass(PlayerEntity.class, event.player.getBoundingBox().inflate(3));
                    boolean reviving = false;

                    PlayerEntity revivingPlayer = null;
                    for(PlayerEntity player : playerEntities) {
                        if (player.isCrouching()) {
                            reviving = true;
                            revivingPlayer = player;
                            break;
                        }
                    }

                    if(reviving){
                        if(h.downReviveCount()){
                            h.setIsIncapacitated(false);
                            event.player.setForcedPose(null);
                            h.setReviveCount(150);
                            h.setJumpCount(3);
                            incapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                            event.player.setHealth(event.player.getMaxHealth()/3f);
                            event.player.level.playSound(null, event.player.getX(), event.player.getY(), event.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1, 1);

                        }else{
                            event.player.displayClientMessage(new StringTextComponent("You are being revived! " + (float)h.getReviveCount()/20f + " seconds..").withStyle(TextFormatting.GREEN), true);
                            revivingPlayer.displayClientMessage(new StringTextComponent("Reviving " + event.player.getScoreboardName() + " " + (float)h.getReviveCount()/20f + " seconds...").withStyle(TextFormatting.GREEN), true);
                        }
                    }
                    else {
                        if (h.countTicksUntilDeath()) {
                            event.player.hurt(DamageSource.GENERIC, event.player.getMaxHealth() *2);
                            event.player.setForcedPose(null);
                            h.setReviveCount(150);
                            h.setJumpCount(3);
                            h.setIsIncapacitated(false);
                            incapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                        } else if (h.getTicksUntilDeath() % 20 == 0) {
                            event.player.displayClientMessage(new StringTextComponent("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(TextFormatting.RED), true);
                        }

                        if(h.getReviveCount() != 150) h.setReviveCount(150);
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
                if(h.getIsIncapacitated()){
                    if(h.giveUpJumpCount()){
                        player.hurt(DamageSource.GENERIC, player.getMaxHealth() *2);
                        player.setForcedPose(null);
                        h.setReviveCount(150);
                        h.setJumpCount(3);
                        h.setIsIncapacitated(false);
                        incapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                    }
                }
            });
        }
    }




}
