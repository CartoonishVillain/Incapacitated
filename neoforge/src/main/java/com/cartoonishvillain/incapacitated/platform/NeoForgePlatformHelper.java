package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.IncapEffects;
import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.capability.NeoForgeIncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import com.cartoonishvillain.incapacitated.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cartoonishvillain.incapacitated.capability.PlayerCapability.INCAP_DATA;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
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
        NeoForgeIncapacitatedPlayerData neoForgedPlayerData = player.getData(INCAP_DATA);
        IncapacitatedPlayerData incapacitatedPlayerData = new IncapacitatedPlayerData();

        incapacitatedPlayerData.setIncapacitated(neoForgedPlayerData.isIncapacitated());
        incapacitatedPlayerData.setReviveCounter(neoForgedPlayerData.getReviveCounter());
        incapacitatedPlayerData.setDownsUntilDeath(neoForgedPlayerData.getDownsUntilDeath());
        incapacitatedPlayerData.setTicksUntilDeath(neoForgedPlayerData.getTicksUntilDeath());

        return incapacitatedPlayerData;
    }

    @Override
    public void writePlayerData(Player player, IncapacitatedPlayerData playerData) {
        NeoForgeIncapacitatedPlayerData neoForgedPlayerData = player.getData(INCAP_DATA);
        neoForgedPlayerData.setIncapacitated(playerData.isIncapacitated());
        neoForgedPlayerData.setReviveCounter(playerData.getReviveCounter());
        neoForgedPlayerData.setTicksUntilDeath(playerData.getTicksUntilDeath());
        neoForgedPlayerData.setDownsUntilDeath(playerData.getDownsUntilDeath());
        player.setData(INCAP_DATA, neoForgedPlayerData);
    }

    public DamageSource getDamageSource(Player player, Level level) {
        NeoForgeIncapacitatedPlayerData neoForgedPlayerData = player.getData(INCAP_DATA);
        return neoForgedPlayerData.getDamageSource(level);
    }

    public void setDamageSource(Level level, DamageSource source, Player player) {
        NeoForgeIncapacitatedPlayerData neoForgedPlayerData = player.getData(INCAP_DATA);
        neoForgedPlayerData.setDamageSource(level, source);
        player.setData(INCAP_DATA, neoForgedPlayerData);
    }

    @Override
    public void killPlayerIfIncappedCommand(ServerPlayer player) {
        NeoForgeIncapacitatedPlayerData playerData = player.getData(INCAP_DATA);
        if (playerData.isIncapacitated()) {
            player.hurt(playerData.getDamageSource(player.level()), player.getMaxHealth() * 10);
            player.setForcedPose(null);
            playerData.setReviveCounter(Incapacitated.configData.getDownCounter());
            playerData.setIncapacitated(false);
            player.removeEffect(MobEffects.GLOWING);
            PacketDistributor.PLAYER.with(player).send(new IncapPacket(player.getId(), false, (short) playerData.getDownsUntilDeath()));
        }
    }

    @Override
    public void sendIncapPacket(ServerPlayer player, int playerID, boolean isIncapacitated, short downsUntilDeath, int downTicks) {
        PacketDistributor.PLAYER.with(player).send(new IncapPacket(player.getId(), isIncapacitated, downsUntilDeath, downTicks));
    }

    @Override
    public MobEffect getSlowEffect() {
        return IncapEffects.incapSlow.get();
    }

    @Override
    public MobEffect getWeakEffect() {
        return IncapEffects.incapWeak.get();
    }
}