package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapabilityManager;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;


@Mod.EventBusSubscriber(modid = Incapacitated.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void playerRegister(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            PlayerCapabilityManager provider = new PlayerCapabilityManager();
            event.addCapability(new ResourceLocation(Incapacitated.MODID, "incapacitated"), provider);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player && !event.isCanceled()) {
            Player player = (Player) event.getEntityLiving();
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
                        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10000, 0));
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), true), player);

                        ArrayList<Player> playerEntities = (ArrayList<Player>) player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(50));

                        for(Player players : playerEntities) {
                            players.displayClientMessage(new TextComponent(player.getScoreboardName() + " is incapacitated!"), false);
                        }

                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void PlayerJoinEvent(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof Player && !event.getWorld().isClientSide()){
            Player player = (Player) event.getEntity();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), h.getIsIncapacitated()), player);
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
                    ArrayList<Player> playerEntities = (ArrayList<Player>) event.player.level.getEntitiesOfClass(Player.class, event.player.getBoundingBox().inflate(3));
                    boolean reviving = false;

                    Player revivingPlayer = null;
                    for(Player player : playerEntities) {
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
                            h.resetGiveUpJumps();
                            event.player.removeEffect(MobEffects.GLOWING);
                            IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                            event.player.setHealth(event.player.getMaxHealth()/3f);
                            event.player.level.playSound(null, event.player.getX(), event.player.getY(), event.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);

                        }else{
                            event.player.displayClientMessage(new TextComponent("You are being revived! " + (float)h.getReviveCount()/20f + " seconds..").withStyle(ChatFormatting.GREEN), true);
                            revivingPlayer.displayClientMessage(new TextComponent("Reviving " + event.player.getScoreboardName() + " " + (float)h.getReviveCount()/20f + " seconds...").withStyle(ChatFormatting.GREEN), true);
                        }
                    }
                    else {
                        if (h.countTicksUntilDeath()) {
                            event.player.hurt(DamageSource.GENERIC, event.player.getMaxHealth() * 10);
                            event.player.setForcedPose(null);
                            h.setReviveCount(150);
                            h.resetGiveUpJumps();
                            event.player.removeEffect(MobEffects.GLOWING);
                            h.setIsIncapacitated(false);
                            IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false), event.player);
                        } else if (h.getTicksUntilDeath() % 20 == 0) {
                            event.player.displayClientMessage(new TextComponent("Incapacitated! Call for help or jump " + h.getJumpCount() + " times to give up! " + ((float) h.getTicksUntilDeath() / 20f) + " seconds left!").withStyle(ChatFormatting.RED), true);
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
        if(event.getEntityLiving() instanceof Player && !event.getEntityLiving().level.isClientSide()){
            Player player = (Player) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h ->{
                if(h.getIsIncapacitated() && h.getJumpDelay() == 0){
                    if(h.giveUpJumpCount()){
                        player.hurt(DamageSource.GENERIC, player.getMaxHealth() * 10);
                        player.setForcedPose(null);
                        h.setReviveCount(150);
                        h.resetGiveUpJumps();
                        h.setTicksUntilDeath(2000);
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
        if(event.getEntityLiving() instanceof Player){
            Player player = (Player) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                h.setJumpDelay(20);
            });
        }
    }

    @SubscribeEvent
    public static void playerEat(LivingEntityUseItemEvent.Finish event){
        if(event.getEntityLiving() instanceof Player && !event.getEntityLiving().level.isClientSide()){
            Item item = event.getItem().getItem();
            Player player = (Player) event.getEntityLiving();
            player.getCapability(PlayerCapability.INSTANCE).ifPresent(h->{
                if(item.equals(Items.GOLDEN_APPLE)) {h.setDownsUntilDeath(3); h.setTicksUntilDeath(2000);}
                if(h.getIsIncapacitated()){
                    if(item.equals(Items.ENCHANTED_GOLDEN_APPLE)){
                        h.setIsIncapacitated(false);
                        player.setForcedPose(null);
                        h.setReviveCount(150);
                        h.resetGiveUpJumps();
                        h.setDownsUntilDeath(3);
                        player.removeEffect(MobEffects.GLOWING);
                        h.setTicksUntilDeath(2000);
                        IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), false), player);
                        player.setHealth(player.getMaxHealth()/3f);
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1, 1);
                    }
                }else if(item.equals(Items.ENCHANTED_GOLDEN_APPLE)) {h.setDownsUntilDeath(3); h.setTicksUntilDeath(2000);}
            });
        }
    }




}
