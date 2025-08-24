package com.cartoonishvillain.incapacitated.mixin;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.incapacitated.Incapacitated.NOTFORHUNTING;

@Mixin(LivingEntity.class)
public class LivingEntityDieMixin {
    @Inject(at = @At("HEAD"), method = "die")
    private void incapacitatedDie(DamageSource damageSource, CallbackInfo ci){
        LivingEntity entity = ((LivingEntity) (Object) this);
        if(!(entity instanceof Player) && !entity.level().isClientSide && damageSource.getEntity() instanceof Player) {
            IncapacitatedPlayerData data = Services.PLATFORM.getPlayerData((Player) damageSource.getEntity());
            if (data.isIncapacitated() && (Incapacitated.configData.isHunter() > 0) && !entity.getType().is(NOTFORHUNTING)) {
                Player player = ((Player) damageSource.getEntity());

                data.setKillsRequiredForRevive(data.getKillsRequiredForRevive() - 1);
                Services.PLATFORM.writePlayerData(player, data);

                if (data.getKillsRequiredForRevive() <= 0) {
                    if (player instanceof ServerPlayer) player.awardStat(Services.PLATFORM.getSelfReviveStat(), 1);
                    AbstractedIncapacitation.revive(player, null);
                }
            }
        }
    }
}