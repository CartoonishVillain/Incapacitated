package com.cartoonishvillain.incapacitated.mixin;


import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void incapacitatedTick(CallbackInfo ci){
        AbstractedIncapacitation.tick((ServerPlayer) (Object) this);
    }
}