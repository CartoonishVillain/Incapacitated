package com.cartoonishvillain.incapacitated.mixin;


import com.cartoonishvillain.incapacitated.component.IncapacitatedComponent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.incapacitated.AbstractedIncapacitation.shaderStuff;
import static com.cartoonishvillain.incapacitated.component.ComponentStarter.INCAPACITATEDCOMPONENTINSTANCE;

@Mixin(LocalPlayer.class)
public class LocalPlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void incapacitatedLocalTick(CallbackInfo ci){
        IncapacitatedComponent playerData = INCAPACITATEDCOMPONENTINSTANCE.get((LocalPlayer) (Object) this);
        shaderStuff(playerData);
    }
}
