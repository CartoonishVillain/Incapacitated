package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.*;
import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import com.cartoonishvillain.incapacitated.events.IncapacitatedRevivalCallback;
import com.cartoonishvillain.incapacitated.events.RevivePlayerState;
import com.cartoonishvillain.incapacitated.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static com.cartoonishvillain.incapacitated.FabricKeybind.giveUpKeybind;
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
    public RevivePlayerState reviveCheckEvent(Player revivingPlayer, Player downPlayer) {
        return IncapacitatedRevivalCallback.EVENT.invoker().interact(revivingPlayer, downPlayer);
    }

    @Override
    public IncapacitatedPlayerData getPlayerData(Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        IncapacitatedPlayerData data = new IncapacitatedPlayerData();
        data.setReviveCounter(h.getReviveCount());
        data.setIncapacitated(h.getIsIncapacitated());
        data.setDownsUntilDeath(h.getDownsUntilDeath());
        data.setTicksUntilDeath(h.getTicksUntilDeath());
        data.setLastDmgTaken(h.getLastDmgTaken());
        data.setLastHealthBeforeDamage(h.getLastHealthBeforeDamage());
        data.setKillsRequiredForRevive(h.getKillsRequiredForRevive());
        return data;
    }

    @Override
    public void writePlayerData(Player player, IncapacitatedPlayerData playerData) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setDownsUntilDeath(playerData.getDownsUntilDeath());
        h.setTicksUntilDeath(playerData.getTicksUntilDeath());
        h.setIsIncapacitated(playerData.isIncapacitated());
        h.setReviveCount(playerData.getReviveCounter());
        h.setLastHealthBeforeDamage(playerData.getLastHealthBeforeDamage());
        h.setLastDmgTaken(playerData.getLastDmgTaken());
        h.setKillsRequiredForRevive(playerData.getKillsRequiredForRevive());
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
    public void setLastDmgTaken(float lastDmgTaken, Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setLastDmgTaken(lastDmgTaken);
    }

    @Override
    public void setLastHealthBeforeDamage(float lastHealthBeforeDamage, Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setLastHealthBeforeDamage(lastHealthBeforeDamage);
    }

    @Override
    public void killPlayerIfIncappedCommand(ServerPlayer player) {
        IncapacitatedComponent playerData = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        if (playerData.getIsIncapacitated()) {
            player.hurt(playerData.getSourceOfDeath(player.level()), player.getMaxHealth() * 10);
            player.kill(player.level());
            playerData.setReviveCount(Incapacitated.configData.getDownCounter());
            playerData.setIsIncapacitated(false);
            player.removeEffect(MobEffects.GLOWING);
        }
    }

    @Override
    public void sendIncapPacket(ServerPlayer player, int playerID, boolean isIncapacitated, short downsUntilDeath, int downTicks) {

    }

    @Override
    public MobEffect getSlowEffect() {
        return FabricEffects.INCAPSLOW.get();
    }

    @Override
    public MobEffect getWeakEffect() {
        return FabricEffects.INCAPWEAK.get();
    }

    @Override
    public ResourceLocation getIncappedStat() {
        return FabricStats.TIMES_INCAPPED.get();
    }

    @Override
    public ResourceLocation getReviveStat() {
        return FabricStats.TIMES_REVIVED.get();
    }

    @Override
    public ResourceLocation getSelfReviveStat() {
        return FabricStats.TIMES_REVIVED_SELF.get();
    }

    @Override
    public Boolean shouldShowGUIDownCounter() {
        return FabricIncapacitated.renderDownCounter;
    }

    @Override
    public Boolean shouldGUIDownCounterBeColorful() {
        return FabricIncapacitated.downCounterColorful;
    }

    @Override
    public int GUIDownCounterXModifier() {
        return FabricIncapacitated.downCounterModX;
    }

    @Override
    public int GUIDownCounterYModifier() {
        return FabricIncapacitated.downCounterModY;
    }
}
