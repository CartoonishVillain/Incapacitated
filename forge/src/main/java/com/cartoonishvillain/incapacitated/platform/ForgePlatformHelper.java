package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.ForgeIncapEffects;
import com.cartoonishvillain.incapacitated.ForgeIncapacitated;
import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.networking.IncapacitationMessenger;
import com.cartoonishvillain.incapacitated.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicReference;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public IncapacitatedPlayerData getPlayerData(Player player) {
        IncapacitatedPlayerData playerData = new IncapacitatedPlayerData();
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
          playerData.setIncapacitated(h.getIsIncapacitated());
          playerData.setTicksUntilDeath(h.getTicksUntilDeath());
          playerData.setDownsUntilDeath(h.getDownsUntilDeath());
          playerData.setReviveCounter(h.getReviveCount());
        });
        return playerData;
    }

    @Override
    public void writePlayerData(Player player, IncapacitatedPlayerData playerData) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            h.setIsIncapacitated(playerData.isIncapacitated());
            h.setDownsUntilDeath(playerData.getDownsUntilDeath());
            h.setTicksUntilDeath(playerData.getTicksUntilDeath());
            h.setReviveCount(playerData.getReviveCounter());
        });
    }

    @Override
    public DamageSource getDamageSource(Player player, Level level) {
        AtomicReference<DamageSource> source = new AtomicReference<>();
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            source.set(h.getSourceOfDeath(level));
        });
            return source.get();
    }

    @Override
    public void setDamageSource(Level level, DamageSource source, Player player) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(h -> {
            h.setSourceOfDeath(level, source);
        });
    }

    @Override
    public void killPlayerIfIncappedCommand(ServerPlayer player) {
        player.getCapability(PlayerCapability.INSTANCE).ifPresent(playerData -> {
            if (playerData.getIsIncapacitated()) {
                player.hurt(playerData.getSourceOfDeath(player.level()), player.getMaxHealth() * 10);
                player.setForcedPose(null);
                playerData.setReviveCount(Incapacitated.configData.getDownCounter());
                playerData.setIsIncapacitated(false);
                player.removeEffect(MobEffects.GLOWING);
                IncapacitationMessenger.INSTANCE.send(new IncapPacket(player.getId(), false, (short) playerData.getDownsUntilDeath()), PacketDistributor.PLAYER.with(player));
            }
        });
    }

    @Override
    public void sendIncapPacket(ServerPlayer player, int playerID, boolean isIncapacitated, short downsUntilDeath, int downTicks) {
        IncapacitationMessenger.INSTANCE.send(new IncapPacket(player.getId(), isIncapacitated, downsUntilDeath, downTicks), PacketDistributor.PLAYER.with(player));
    }

    @Override
    public MobEffect getSlowEffect() {
        return ForgeIncapEffects.incapSlow.get();
    }

    @Override
    public MobEffect getWeakEffect() {
        return ForgeIncapEffects.incapWeak.get();
    }
}