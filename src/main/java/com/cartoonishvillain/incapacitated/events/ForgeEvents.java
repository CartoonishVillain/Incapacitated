package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.IncapacitatedCommonConfig;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

import java.util.ArrayList;

import static com.cartoonishvillain.incapacitated.Incapacitated.*;
import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;
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
    public static void playerHurtCheck(LivingHurtEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            //Merciful check
            IncapacitatedPlayerData data = event.getEntity().getData(INCAP_DATA);
            if (data.isIncapacitated() && Incapacitated.merciful > 0 && !(event.getSource().getMsgId().equals("bleedout"))) {
                if (merciful == 1 && !event.getEntity().level().isClientSide) {
                    data.setTicksUntilDeath((int) (data.getTicksUntilDeath() - event.getAmount()));
                    IncapacitationMessenger.sendTo(new IncapPacket(event.getEntity().getId(), data.isIncapacitated(), (short) data.getDownsUntilDeath(), data.getTicksUntilDeath()), (Player) event.getEntity());
                    event.getEntity().setData(INCAP_DATA, data);
                }
                event.setAmount(0);
            }
        }
    }

    @SubscribeEvent
    public static void playerKillCheck(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer && !(event.getEntity() instanceof Player) && hunter) {
            IncapacitatedPlayerData data = event.getSource().getEntity().getData(INCAP_DATA);
            if (data.isIncapacitated()) {
                revive((Player) event.getSource().getEntity());
            }
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
            IncapacitatedPlayerData data = event.getEntity().getData(INCAP_DATA);
            if (data.isIncapacitated()) {
                downOrKill(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            event.getEntity().sendSystemMessage(Component.translatable("incapacitated.info.joinrevivetutorial"));
        }
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event){
        if(!event.isWasDeath()){
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            originalPlayer.revive();

            IncapacitatedPlayerData playerData = originalPlayer.getData(INCAP_DATA);

            originalPlayer.kill();

            newPlayer.setData(INCAP_DATA, playerData);
        }
    }

    @SubscribeEvent
    public static void PlayerJoinEvent(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player && !event.getLevel().isClientSide()){
            IncapacitatedPlayerData playerData = event.getEntity().getData(INCAP_DATA);
            IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), playerData.isIncapacitated(), (short) playerData.getDownsUntilDeath(), playerData.getTicksUntilDeath()), player);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            //Given event player's data
            IncapacitatedPlayerData playerData = event.player.getData(INCAP_DATA);

            //If the player is down, run all the code associated every tick, otherwise don't.
            if(playerData.isIncapacitated()) {
                //If the player isn't already in a forced pose, force them into the swimming pose every tick.
                //This is to prevent conflicts with other mods that use this forced pose. There is no priority system to
                //ensure we get this forced pose uncontested, so if other mods are also setting their poses every tick, we could cause a major issue if we don't yield.

                //When we are the only one forcing poses though, this leaves a player on the ground while incapacitated.
                if (event.player.getForcedPose() == null) {
                    event.player.setForcedPose(Pose.SWIMMING);
                }

                //Scan for any players nearby
                ArrayList<Player> playerEntities = (ArrayList<Player>) event.player.level().getEntitiesOfClass(Player.class, event.player.getBoundingBox().inflate(3));
                boolean reviving = false;
                Player revivingPlayer = null;

                //Loop through nearby players to check if any are reviving the downed player successfully
                for (Player player : playerEntities) {
                    boolean isdown;
                    IncapacitatedPlayerData potentialHeroData = player.getData(INCAP_DATA);
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
                        revive(event.player);
                    } else {
                        //If the timer is not 0 on the revive timer, tell both parties that the revive is occuring, and how much longer until it is done.
                        event.player.displayClientMessage(Component.translatable("message.downindicator.reviving", (playerData.getReviveCounter() / 20)).withStyle(ChatFormatting.GREEN), true);
                        revivingPlayer.displayClientMessage(Component.translatable("message.reviveindicator.reviving", event.player.getScoreboardName(), (playerData.getReviveCounter() / 20)).withStyle(ChatFormatting.GREEN), true);
                    }
                } else {
                    //If our event player is not being revived, count down the timer until; their death. Returns true when the player runs out of time.
                    if (playerData.countTicksUntilDeath()) {
                        event.player.hurt(playerData.getDamageSource(event.player.level()), event.player.getMaxHealth() * 10);
                        event.player.setForcedPose(null);
                        playerData.setReviveCounter(IncapacitatedCommonConfig.REVIVETICKS.get());
                        event.player.removeEffect(MobEffects.GLOWING);
                        playerData.setIncapacitated(false);
                        event.player.setData(INCAP_DATA, playerData);
                        IncapacitationMessenger.sendTo(new IncapPacket(event.player.getId(), false, (short) playerData.getDownsUntilDeath()), event.player);
                    } else if (playerData.getTicksUntilDeath() % 20 == 0) {
                        //Otherwise, every 20 ticks (1 second) send the dying player a message about how long, in seconds, they have until death.
                        event.player.displayClientMessage(Component.translatable("message.downindicator.norevive", "/incap die", playerData.getTicksUntilDeath() / 20f).withStyle(ChatFormatting.RED), true);
                    }

                    //Additionally, if the user is not reviving, make sure the revive timer is reset.
                    if (playerData.getReviveCounter() != IncapacitatedCommonConfig.REVIVETICKS.get()) {
                        playerData.setReviveCounter(IncapacitatedCommonConfig.REVIVETICKS.get());
                        event.player.setData(INCAP_DATA, playerData);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerRested(PlayerWakeUpEvent event) {
        if(!event.updateLevel() && !event.wakeImmediately() && regenerating) {
            IncapacitatedPlayerData playerData = event.getEntity().getData(INCAP_DATA);
            if (playerData.getDownsUntilDeath() < IncapacitatedCommonConfig.DOWNCOUNT.get()) {
                AbstractedIncapacitation.setDownCount(event.getEntity(), (short) (playerData.getDownsUntilDeath() + 1));
            }
        }
    }

    @SubscribeEvent
    public static void playerEat(LivingEntityUseItemEvent.Finish event){
        if(event.getEntity() instanceof Player player && !event.getEntity().level().isClientSide()){
            Item item = event.getItem().getItem();
            IncapacitatedPlayerData playerData = player.getData(INCAP_DATA);
            if (HealingFoods.contains(item.toString())) {
                playerData.setDownsUntilDeath(IncapacitatedCommonConfig.DOWNCOUNT.get());
                playerData.setTicksUntilDeath(IncapacitatedCommonConfig.DOWNTICKS.get());
                player.setData(INCAP_DATA, playerData);
                IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), playerData.isIncapacitated(), (short) playerData.getDownsUntilDeath()), player);
            }

            if (ReviveFoods.contains(item.toString())) {
                playerData.setDownsUntilDeath(IncapacitatedCommonConfig.DOWNCOUNT.get());
                playerData.setTicksUntilDeath(IncapacitatedCommonConfig.DOWNTICKS.get());
                player.setData(INCAP_DATA, playerData);

                if (playerData.isIncapacitated())
                    revive(player);
                else
                    IncapacitationMessenger.sendTo(new IncapPacket(player.getId(), playerData.isIncapacitated(), (short) playerData.getDownsUntilDeath()), player);
            }
        }
    }

    public static void broadcast(MinecraftServer server, Component translationTextComponent){
        server.getPlayerList().broadcastSystemMessage(translationTextComponent, false);
    }




}
