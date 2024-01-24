package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.FabricEffects;
import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import com.cartoonishvillain.incapacitated.component.IncapacitatedInterface;
import com.cartoonishvillain.incapacitated.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static com.cartoonishvillain.incapacitated.component.ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IncapacitatedPlayerData getPlayerData(Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        IncapacitatedPlayerData data = new IncapacitatedPlayerData();
        data.setReviveCounter(h.getReviveCount());
        data.setIncapacitated(h.getIsIncapacitated());
        data.setDownsUntilDeath(h.getDownsUntilDeath());
        data.setTicksUntilDeath(h.getTicksUntilDeath());
        return data;
    }

    @Override
    public void writePlayerData(Player player, IncapacitatedPlayerData playerData) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setDownsUntilDeath(playerData.getDownsUntilDeath());
        h.setTicksUntilDeath(playerData.getTicksUntilDeath());
        h.setIsIncapacitated(playerData.isIncapacitated());
        h.setReviveCount(playerData.getReviveCounter());
    }

    @Override
    public DamageSource getDamageSource(Player player, Level level) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        return h.getSourceOfDeath(level);
    }

    @Override
    public void setDamageSource(Level level, DamageSource source, Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setSourceOfDeath(level, source);
    }

    @Override
    public void killPlayerIfIncappedCommand(ServerPlayer player) {
        IncapacitatedComponent playerData = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        if (playerData.getIsIncapacitated()) {
            player.hurt(playerData.getSourceOfDeath(player.level()), player.getMaxHealth() * 10);
            playerData.setReviveCount(FabricIncapacitated.downCounter);
            playerData.setIsIncapacitated(false);
            player.removeEffect(MobEffects.GLOWING);
        }
    }

    @Override
    public void sendIncapPacket(ServerPlayer player, int playerID, boolean isIncapacitated, short downsUntilDeath, int downTicks) {

    }

    @Override
    public MobEffect getSlowEffect() {
        return FabricEffects.incapSlow;
    }

    @Override
    public MobEffect getWeakEffect() {
        return FabricEffects.incapWeak;
    }

    @Override
    public boolean clientConfigGrayScreen() {
        return FabricIncapacitated.lastDownDesaturate;
    }

    @Override
    public boolean commonConfigGlowing() {
        return FabricIncapacitated.glowingWhileDowned;
    }

    @Override
    public boolean commonConfigUseSeconds() {
        return FabricIncapacitated.useSecondsForRevive;
    }

    @Override
    public boolean commonConfigSomeInstantKills() {
        return FabricIncapacitated.someInstantKills;
    }

    @Override
    public boolean commonConfigUnlimitedDowns() {
        return FabricIncapacitated.unlimitedDowns;
    }

    @Override
    public boolean commonConfigSlow() {
        return FabricIncapacitated.slow;
    }

    @Override
    public boolean commonConfigWeak() {
        return FabricIncapacitated.weakened;
    }

    @Override
    public boolean commonConfigDownLogging() {
        return FabricIncapacitated.downLogging;
    }

    @Override
    public boolean commonConfigReviveMessage() {
        return FabricIncapacitated.reviveMessage;
    }

    @Override
    public boolean commonConfigGlobalReviveMessage() {
        return FabricIncapacitated.globalReviveMessage;
    }

    @Override
    public boolean commonConfigGlobalIncapMessage() {
        return FabricIncapacitated.globalIncapMessage;
    }

    @Override
    public boolean commonConfigHunter() {return FabricIncapacitated.hunter;}

    @Override
    public boolean commonConfigRegenerating() {
        return FabricIncapacitated.regenerating;
    }

    @Override
    public int commonConfigMerciful() {
        return FabricIncapacitated.merciful;
    }

    @Override
    public int commonConfigDownTicks() {
        return FabricIncapacitated.downTicks;
    }

    @Override
    public int commonConfigDownCount() {
        return FabricIncapacitated.downCounter;
    }

    @Override
    public int commonConfigReviveTicks() {
        return FabricIncapacitated.reviveTicks;
    }
}
