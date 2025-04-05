package com.cartoonishvillain.incapacitated.platform;

import com.cartoonishvillain.incapacitated.FabricEffects;
import com.cartoonishvillain.incapacitated.FabricIncapacitated;
import com.cartoonishvillain.incapacitated.FabricStats;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import com.cartoonishvillain.incapacitated.events.IncapacitatedRevivalCallback;
import com.cartoonishvillain.incapacitated.events.RevivePlayerState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static com.cartoonishvillain.incapacitated.component.ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE;

public class Services {

    
    public static String getPlatformName() {
        return "Fabric";
    }

    
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    
    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    
    public static RevivePlayerState reviveCheckEvent(Player revivingPlayer, Player downPlayer) {
        return IncapacitatedRevivalCallback.EVENT.invoker().interact(revivingPlayer, downPlayer);
    }

    
    public static IncapacitatedPlayerData getPlayerData(Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        IncapacitatedPlayerData data = new IncapacitatedPlayerData();
        data.setReviveCounter(h.getReviveCount());
        data.setIncapacitated(h.getIsIncapacitated());
        data.setDownsUntilDeath(h.getDownsUntilDeath());
        data.setTicksUntilDeath(h.getTicksUntilDeath());
        return data;
    }

    
    public static void writePlayerData(Player player, IncapacitatedPlayerData playerData) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setDownsUntilDeath(playerData.getDownsUntilDeath());
        h.setTicksUntilDeath(playerData.getTicksUntilDeath());
        h.setIsIncapacitated(playerData.isIncapacitated());
        h.setReviveCount(playerData.getReviveCounter());
        h.setShader(playerData.isShader());
    }

    
    public static DamageSource getDamageSource(Player player, Level level) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        return h.getSourceOfDeath(level);
    }

    
    public static void setDamageSource(Level level, DamageSource source, Player player) {
        IncapacitatedComponent h = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        h.setSourceOfDeath(level, source);
    }

    
    public static void killPlayerIfIncappedCommand(ServerPlayer player) {
        IncapacitatedComponent playerData = INCAPACITATEDCOMPONENTINSTANCE.get(player);
        if (playerData.getIsIncapacitated()) {
            player.hurt(playerData.getSourceOfDeath(player.level()), player.getMaxHealth() * 10);
            player.kill(player.serverLevel());
            playerData.setReviveCount(FabricIncapacitated.configData.getDownCounter());
            playerData.setIsIncapacitated(false);
            player.removeEffect(MobEffects.GLOWING);
        }
    }

    
    public static void sendIncapPacket(ServerPlayer player, int playerID, boolean isIncapacitated, short downsUntilDeath, int downTicks) {

    }

    
    public static void loadConfig() {
        FabricIncapacitated.loadConfig();
    }

    
    public static MobEffect getSlowEffect() {
        return FabricEffects.INCAPSLOW.get();
    }

    
    public static MobEffect getWeakEffect() {
        return FabricEffects.INCAPWEAK.get();
    }

    
    public static ResourceLocation getIncappedStat() {
        return FabricStats.TIMES_INCAPPED.get();
    }

    
    public static ResourceLocation getReviveStat() {
        return FabricStats.TIMES_REVIVED.get();
    }

    
    public static ResourceLocation getSelfReviveStat() {
        return FabricStats.TIMES_REVIVED_SELF.get();
    }
}
