package com.cartoonishvillain.incapacitated.platform.services;

import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /*
      INCAPACITATED API METHODS HERE
     */

    IncapacitatedPlayerData getPlayerData(Player player);

    void writePlayerData(Player player, IncapacitatedPlayerData playerData);

    DamageSource getDamageSource(Player player, Level level);

    void setDamageSource(Level level, DamageSource source, Player player);

    /*
      INCAPACITATED COMMAND METHODS HERE.
     */

    void killPlayerIfIncappedCommand(ServerPlayer player);
    /*
                IncapacitatedPlayerData playerData = sourceStack.getPlayer().getData(INCAP_DATA);
            if (playerData.isIncapacitated()) {
                player.hurt(playerData.getDamageSource(player.level()), player.getMaxHealth() * 10);
                player.setForcedPose(null);
                playerData.setReviveCounter(IncapacitatedCommonConfig.DOWNCOUNT.get());
                playerData.setIncapacitated(false);
                player.removeEffect(MobEffects.GLOWING);
                PacketDistributor.PLAYER.with((ServerPlayer) player).send(new IncapPacket(player.getId(), false, (short) playerData.getDownsUntilDeath()));
            }
     */

    /*
      INCAPACITATED PACKET
     */
    default void sendIncapPacket(
            ServerPlayer player,
            int playerID,
            boolean isIncapacitated,
            short downsUntilDeath
    ) {
        sendIncapPacket(player, playerID, isIncapacitated, downsUntilDeath, -1);
    }

    void sendIncapPacket(
            ServerPlayer player,
            int playerID,
            boolean isIncapacitated,
            short downsUntilDeath,
            int downTicks
    );

    /*
      INCAPACITATED MOB EFFECTS
     */
    MobEffect getSlowEffect();
    MobEffect getWeakEffect();

    /*
      INCAPACITATED CONFIG
     */

    boolean clientConfigGrayScreen();

    boolean commonConfigGlowing();

    boolean commonConfigUseSeconds();

    boolean commonConfigSomeInstantKills();

    boolean commonConfigUnlimitedDowns();

    boolean commonConfigSlow();

    boolean commonConfigWeak();

    boolean commonConfigDownLogging();

    boolean commonConfigReviveMessage();

    boolean commonConfigGlobalReviveMessage();

    boolean commonConfigGlobalIncapMessage();

    boolean commonConfigHunter();

    boolean commonConfigRegenerating();

    int commonConfigMerciful();

    int commonConfigDownTicks();

    int commonConfigDownCount();

    int commonConfigReviveTicks();
}