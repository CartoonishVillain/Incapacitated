package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.ForgeIncapEffects;
import com.cartoonishvillain.incapacitated.ForgeIncapacitated;
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
                playerData.setReviveCount(ForgeIncapacitated.config.DOWNCOUNT.get());
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

    @Override
    public boolean clientConfigGrayScreen() {
        return ForgeIncapacitated.clientConfig.GRAYSCREEN.get();
    }

    @Override
    public boolean commonConfigGlowing() {
        return ForgeIncapacitated.config.GLOWING.get();
    }

    @Override
    public boolean commonConfigUseSeconds() {
        return ForgeIncapacitated.config.USESECONDS.get();
    }

    @Override
    public boolean commonConfigSomeInstantKills() {
        return ForgeIncapacitated.config.SOMEINSTANTKILLS.get();
    }

    @Override
    public boolean commonConfigUnlimitedDowns() {
        return ForgeIncapacitated.config.UNLIMITEDDOWNS.get();
    }

    @Override
    public boolean commonConfigSlow() {
        return ForgeIncapacitated.config.SLOW.get();
    }

    @Override
    public boolean commonConfigWeak() {
        return ForgeIncapacitated.config.WEAKENED.get();
    }

    @Override
    public boolean commonConfigDownLogging() {
        return ForgeIncapacitated.config.DOWNLOGGING.get();
    }

    @Override
    public boolean commonConfigReviveMessage() {
        return ForgeIncapacitated.config.REVIVE_MESSAGE.get();
    }

    @Override
    public boolean commonConfigGlobalReviveMessage() {
        return ForgeIncapacitated.config.GLOBALREVIVEMESSAGES.get();
    }

    @Override
    public boolean commonConfigGlobalIncapMessage() {
        return ForgeIncapacitated.config.GLOBALINCAPMESSAGES.get();
    }

    @Override
    public boolean commonConfigHunter() {
        return ForgeIncapacitated.config.HUNTER.get();
    }

    @Override
    public boolean commonConfigRegenerating() {
        return ForgeIncapacitated.config.REGENERATING.get();
    }

    @Override
    public int commonConfigMerciful() {
        return ForgeIncapacitated.config.MERCIFUL.get();
    }

    @Override
    public int commonConfigDownTicks() {
        return ForgeIncapacitated.config.DOWNTICKS.get();
    }

    @Override
    public int commonConfigDownCount() {
        return ForgeIncapacitated.config.DOWNCOUNT.get();
    }

    @Override
    public int commonConfigReviveTicks() {
        return ForgeIncapacitated.config.REVIVETICKS.get();
    }
}